## 项目说明
基于litemall构建的数据仓库

### 核心模块
1. 数据仓库(离线/实时)
2. 推荐系统
3. 用户画像

### 使用技术
1. 大数据
	1. Hadoop
	2. hive on tez
	3. sqoop
	4. spark
	5. kafka	
	6. azkaban
2. 后端
	1. spring boot
	2. mysql
	3. redis
	4. mongo
	5. openresty

### 运行环境
1. Hadoop主机
	1. Hadoop：3.1.2
	2. hive：3.1.2
	3. tez：0.10.1 结合对应的hive版本进行源码编译
	3. sqoop：1.4.7
	4. spark $\ge$2.4.7 
2. Kafka
	1. zookeeper：3.6.1
	2. Kafka：2.13-2.6.0  
3. CDH：6.3.2

## 数据准备
### 埋点
处理用户行为数据，采用json数据格式

1. openresty采集数据发送到kafka
	1. 创建topic

			kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 2 --partitions 3 --topic litemall-action 
	2. 配置nginx
		1. 生成工作目录

				mkdir -p {litemall/logs,litemall/conf,litemall/luas}
		2. 配置nginx

				worker_processes  1;
				error_log logs/error.log;
				events {
				    worker_connections 1024;
				}
				
				user root root; # 定义用户名、用户组，防止权限问题
				
				http {
				    # 设置共享字典
				    lua_shared_dict kafka_data 10m;
				    resolver 192.168.241.20; #使用能解析kafka域名的dns
				    server {
				        listen 80;
				        location / {
				            default_type text/html;
				            content_by_lua_block {
				                ngx.say("<p>hello, world</p>")
				            }
				        }
				
					location /process{
					   default_type application/json;
					   content_by_lua_file /root/project/litemall/luas/kafka_test.lua;
					}
				
				    }
				}
		3. 发送到kafka 

				local cjson=require "cjson"
				local producer = require "resty.kafka.producer"
				
				local broker_list = {
				    { host = "192.168.241.180", port = 9092 },
				    { host = "192.168.241.181", port = 9092 },
				    { host = "192.168.241.182", port = 9092 },
				}
				
				local topic="litemall-action"
				local partitions=3
				
				local shared_data = ngx.shared.kafka_data
				local partitionNum=shared_data:get("count")
				if not partitionNum then
				    shared_data:set("count",1)
				    partitionNum=shared_data:get("count")
				end
				local key = ""..(partitionNum%partitions)
				shared_data:incr("count",1)
				
				ngx.req.read_body()
				local request_body = ngx.req.get_body_data()
				if request_body then
				     local p = producer:new(broker_list)
				    local offset, err = p:send(topic, key, request_body)
				end
				
				ngx.say(cjson.encode{code = 200, msg = request_body})
2. 页面埋点
	1. 不同日志的处理：监听生命周期函数
		1. 启动日志：在`main.js`通过绑定`vue`对象的`mounted`事件来处理，保证只有一次请求
		2. 事件日志
			1.  在`main.js`通过绑定`vue`对象的`mounted`和`beforeDestory`来启动或清除定时器
			2. 在定时器中发送事件日志，消除频繁请求日志服务器
			3. 采用数组来记录事件，并在发送之后清空
	2. 跨域的处理
		1. 前端通过代理处理(修改`vue.config.js`配置文件,`vue cli 3.0+`)

				devServer: {
				    port:6255,
				    proxy:{
				      '/process': {
				        target: process.env.VUE_LOG_BASE_API,
				        ws: true,
				        changeOrigin: true //允许跨域
				      },
				      '/wx': {
				        target: process.env.VUE_APP_BASE_API,
				        ws: true,
				        changeOrigin: true
				      }
				    }
				  },
		2. 后端通过nginx处理

				location /process{
				   default_type application/json;
				   # 添加响应头，在opthons请求返回的响应中，从而在下个请求中携带
				   add_header Access-Control-Allow-Credentials true;
				   add_header Access-Control-Allow-Headers *;
			         add_header Access-Control-Allow-Origin *;
				   content_by_lua_file /root/project/litemall/luas/kafka_test.lua;
				}

### 预处理
#### 将数据导入hdfs

	hdfs dfs -mkdir -p {/original_data/litemall/db,/original_data/litemall/log}

1. 将事件日志导入hdfs
	1. 自定义flume的interceptor将数据拆分为start和event类型的数据，并存储到hdfs中不同的文件夹
	2. 打包之后的jar放入到flume的`plugins.d/interceptor/lib`文件夹下
		1. `plugins.d`文件夹用于集成第三方的`plugin`
	3. **flume守护进程**问题
		1. 原因：启动配置参数`-c`设置不正确
		2. 解决：

				FLUME_HOME=/opt/apache-flume-1.9.0-bin
				# 需要配置到flume安装路径下的conf文件夹
				nohup $FLUME_HOME/bin/flume-ng agent -c $FLUME_HOME/conf -f $FLUME_HOME/conf/litemall_kafka_to_hdfs.conf --name a1 -Dflume.root.logger=INFO,console >$FLUME_HOME/logs/litemall 2>&1 &
	4. 处理**hive**报错:**Cannot obtain block length for LocatedBlock**
		1. 在跑Azkaban任务的时候出现以上错误，其原因是：文件处于`openforwrite`的状态，没有关闭
		2. 解决：
			1. 查看文件状态

					hdfs fsck dir -files
			2. 查看路径下是否有`openforwrite的文件`

					hdfs fsck dir -openforwrite
			3. 释放租约 

					hdfs debug recoverLease -path filepath [-retries retry-num]
		3. [分析](https://www.cnblogs.com/cssdongl/p/6700512.html)：
			1. 没有关闭flume重启了hdfs 
2. 将业务数据导入hdfs
	1. sqoop将mysql中`tinyint`的数据在HDFS 上面显示的`true、false`及在hive中需要使用`boolean`存储
		1.  原因：jdbc会把tinyint 认为是`java.sql.Types.BIT`
		2. 解决：`jdbc:mysql://localhost/test?tinyInt1isBit=false`
	2. sqoop：源数据含有默认换行符导致的数据不正确(出现数据错位)
		1. 解决：import添加参数

				# Drops \n, \r, and \01 from string fields when importing to Hive.
				--hive-drop-import-delims
	3. 支付和退款数据
		1. 因该数据都存在于订单表中，所以进行特殊处理
			1. 支付：除了分区数据以外，同时添加`pay_time`作为过滤条件
			2. 退款：除了分区数据以外，同时添加`refund_time`作为过滤条件
3. 自定义hive函数来解析events事件日志
	1. `hive-function`：中定义UDF和UDTF
		1. 将打包后的jar包上传到hdfs

				hdfs dfs -put hive-function.jar /user/hive/jars
	2. 注册全局函数

			# udf
			create function litemall.parse_json_object as 'org.tlh.litemall.udf.ParseJsonObject' using jar 'hdfs:///user/hive/jars/hive-function.jar'	
			# udtf
			create function litemall.extract_event_type as 'org.tlh.litemall.udtf.ExtractEventType' using jar 'hdfs:///user/hive/jars/hive-function.jar'
4. 对于**分区表，新增加列**，重新覆盖写已有的分区，新增列为`null`
	1. 原因：由于分区元数据缺少新添加的字段导致的
	2. 解决：往出错的分区元数据中添加这个列

			alter table dwd_fact_cart_info partition(dt = '2020-12-04') add columns(add_time string comment '加购时间');	
5. 加载时间维度数据

		LOAD DATA  INPATH '/date_info.txt' OVERWRITE INTO TABLE dwd_dim_date_info;

#### 导出数据
1. 将数据导出到mysql数据重复
	1. 对于以日期为基础的数据
		1. 如果只有一条，则mysql中日期列设置为主键
		2. 如果是多条，则hive中需要以日期进行分区
2. 目前通过Sqoop从Hive的parquet抽数到关系型数据库的时候会报kitesdk找不到文件的错，这是Sqoop已知的问题
	1. 将需要导出的表存储方式采用默认的`textfile`

### 推荐系统
#### 加载hive中的数据
1. 启动hiveserver

		hive --service hiveserver2
2. spark配置通过域名通信

		conf.set("dfs.client.use.datanode.hostname", "true")
3. 注意事项
	1. mac环境下读取hive中lzo压缩的数据
		1. 使用homebrew安装lzo、lzop

				brew install lzop lzo
		2. 编译安装hadoop-lzo  

				git clone git@github.com:twitter/hadoop-lzo.git
				mvn clean package install -DskipTests
				
### 用户画像
#### 环境准备
1. 创建画像`namespace`

		create_namespace 'litemall_profile'	

#### 将用户标签导入solr
1.  开启hbase表的复制功能

		# 禁用表
		disable 'litemall_profile:user_profile'
		# 开启表复制
		alter 'litemall_profile:user_profile',{NAME=>'cf',REPLICATION_SCOPE =>1}
		# 启用表
		enable 'litemall_profile:user_profile'
2. 创建`solr`的collection
	1. 生成配置

			solrctl instancedir --generate $HOME/hbase_indexer/litemall
	2. 定义schema

			cd $HOME/hbase_indexer/litemall/conf
			# 修改managed-schema配置,添加如下内容
			vim managed-schema
			
			<field name="gender" type="string" indexed="true" stored="true"/>
			<field name="age" type="string" indexed="true" stored="true"/>
			<field name="consumptionAbility" type="string" indexed="true" stored="true"/>
			<field name="consumption" type="string" indexed="true" stored="true"/>
			<field name="coupon" type="string" indexed="true" stored="true"/>
			<field name="discountRate" type="string" indexed="true" stored="true"/>
			<field name="singleOrderMax" type="string" indexed="true" stored="true"/>
			<field name="orderFrequency" type="string" indexed="true" stored="true"/>
			<field name="pct" type="string" indexed="true" stored="true"/>
			<field name="refundRate" type="string" indexed="true" stored="true"/>
			<field name="rfm" type="string" indexed="true" stored="true"/>
			<field name="psm" type="string" indexed="true" stored="true"/>
			<field name="brandFavor" type="string" indexed="true" stored="true"/>
	3. 创建solr的collection并上传配置

			# 上传配置
			solrctl instancedir --create litemall $HOME/hbase_indexer/litemall
			# 创建collection
			solrctl collection --create litemall -s 3 -r 3 -m 9
3. 创建`Lily HBase Indexer` 配置
	1. 定义`morphline 和 hbase`的映射配置

			cd $HOME/hbase_indexer/litemall
			vim morphline-hbase-mapper.xml
			
			<?xml version="1.0"?>
			<!--
			         table: hbase中表名
			-->
			<indexer table="litemall_profile:user_profile" mapper="com.ngdata.hbaseindexer.morphline.MorphlineResultToSolrMapper">
			   <!-- 设置morphlines文件的路径：绝对路径或相对路径 -->
			   <param name="morphlineFile" value="morphlines.conf"/>
			
			   <!-- 设置使用的是那个ETL -->
			   <param name="morphlineId" value="userProfile"/>
			</indexer>
	2. 创建`Morphline`配置文件(主要用于导入历史数据)，定义ETL(hbase-solr的转换关系)，同时将`userProfile`的配置信息通过CDH控制台页面进行添加(`Lily HBase Indexer`->`配置`->`Morphlines 文件`)，注册一个新的ETL

			cd $HOME/hbase_indexer/litemall
			vim morphlines.conf
			
			# 内容如下
			SOLR_LOCATOR : {
				  # Name of solr collection
				  collection : collection
				
				  # ZooKeeper ensemble
				  zkHost : "$ZK_HOST"
				}
				
				
				morphlines : [
				{
				id : userProfile
				importCommands : ["org.kitesdk.**", "com.ngdata.**"]
				
				commands : [
				  {
				    extractHBaseCells {
				      mappings : [
				        {
				          inputColumn : "cf:gender"
				          outputField : "gender"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:age"
				          outputField : "age"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:consumptionAbility"
				          outputField : "consumptionAbility"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:consumption"
				          outputField : "consumption"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:coupon"
				          outputField : "coupon"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:discountRate"
				          outputField : "discountRate"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:singleOrderMax"
				          outputField : "singleOrderMax"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:orderFrequency"
				          outputField : "orderFrequency"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:pct"
				          outputField : "pct"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:refundRate"
				          outputField : "refundRate"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:rfm"
				          outputField : "rfm"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:psm"
				          outputField : "psm"
				          type : string
				          source : value
				        }
				        {
				          inputColumn : "cf:brandFavor"
				          outputField : "brandFavor"
				          type : string
				          source : value
				        }
				      ]
				    }
				  }
				
				
				  { logDebug { format : "output record: {}", args : ["@{}"] } }
				]
			}
			]
	3. 注册`Lily HBase Indexer Configuration` 和 `Lily HBase Indexer Service`

			hbase-indexer add-indexer \
			--name litemallIndexer \
			--indexer-conf $HOME/hbase_indexer/litemall/morphline-hbase-mapper.xml \
			--connection-param solr.zk=cdh-master:2181,cdh-slave-3:2181,cdh-slave-2:2181/solr \
			--connection-param solr.collection=litemall \
			--zookeeper cdh-master:2181,cdh-slave-3:2181,cdh-slave-2:2181
	4. 查看Indexer

			hbase-indexer list-indexers
4. 导入历史数据	

		hadoop --config /etc/hadoop/conf \
		jar /opt/cloudera/parcels/CDH/lib/hbase-solr/tools/hbase-indexer-mr-*-job.jar \
		--conf /etc/hbase/conf/hbase-site.xml -Dmapreduce.map.java.opts="-Xmx512m" \
		-Dmapreduce.reduce.java.opts="-Xmx512m" \
		--morphline-file $HOME/hbase_indexer/litemall/morphlines.conf \
		--hbase-indexer-file $HOME/hbase_indexer/litemall/morphline-hbase-mapper.xml \
		--zk-host cdh-master:2181,cdh-slave-3:2181,cdh-slave-2:2181/solr \
		--collection litemall \
		--go-live
		
5. [solr数据备份与恢复(将其他集群中的数据导入)](https://docs.cloudera.com/documentation/enterprise/latest/topics/search_backup_restore.html)
	1.  备份其他集群的数据
		1. 创建快照

				solrctl collection --create-snapshot litemall_bk -c litemall
		2. 创建备份目录

				hdfs dfs -mkdir -p /backup/litemall_16
				# 修改权限
				sudo -u hdfs hdfs dfs -chown solr:solr /backup/litemall_16
		3. 准备导出(目的导出元数据合索引数据)

				solrctl collection --prepare-snapshot-export litemall_bk -c litemall -d /backup/litemall_16
		4. 导出

				solrctl collection --export-snapshot litemall_bk -s /backup/litemall_16 -d hdfs://cdh-master:8020/backup/bk
		5. 复制到本地 

				hdfs dfs -copyToLocal /backup/bk litemall
				tar -zcvf litemall.tar.gz litemall/
		
	2. 恢复到当前集群	
		1. 创建目录上传到出的数据

				hdfs dfs -mkdir -p /path/to/restore-staging
				hdfs dfs -copyFromLocal litemall /path/to/restore-staging
		2.  数据恢复

				solrctl collection --restore litemall -l /path/to/restore-staging/litemall -b litemall_bk -i restore-litemall
		3.  查看状态

				solrctl collection --request-status restore-litemall
				
### 注意事项
1. CDH中使用lzo压缩，本地读取数据问题(报`No LZO codec found, cannot run.`错误)
	1. 原因：在`hadoop-common`包中使用的是**SPI**来加载解压缩方式，默认配置中并不包含`lzo`的配置
	2. 解决：添加`core-site.xml`文件，并添加lzo解压缩配置

			<?xml version="1.0" encoding="UTF-8"?>
			<configuration>
			    <property>
			        <name>io.compression.codecs</name>
			        <value>
			            org.apache.hadoop.io.compress.DefaultCodec,
			            org.apache.hadoop.io.compress.GzipCodec,
			            org.apache.hadoop.io.compress.BZip2Codec,
			            org.apache.hadoop.io.compress.DeflateCodec,
			            org.apache.hadoop.io.compress.SnappyCodec,
			            org.apache.hadoop.io.compress.Lz4Codec,
			            com.hadoop.compression.lzo.LzoCodec,
			            com.hadoop.compression.lzo.LzopCodec
			        </value>
			    </property>
			</configuration>	 	

## 术语
1. 支付转换率：所选时间内，支付买家数除以访客数（支付买家数/访客数），即访客转化为支付买家的比例
	$$
		支付转换率={支付买家数 \over 访客数}
	$$
2. 客单价：每一个顾客平均购买商品的金额，也即是平均交易金额
	$$
		客单价={销售额 \over 成交顾客数}
	$$	

## 致谢

本项目基于或参考以下项目：

1. [litemall](https://github.com/linlinjava/litemall)：又一个小商场系统，Spring Boot后端 + Vue管理员前端 + 微信小程序用户前端 + Vue用户移动端