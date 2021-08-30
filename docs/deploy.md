## 项目部署文档
### litemall部署
1. 创建数据库
	1. 执行`doc/mysql`文件夹下的`litemall.sql`文件，创建`litemall`数据库及表，和初始化数据
	2. 创建用户名/密码 
2. 数据模拟器
	1. 打包项目：`data-simulate`
	2. `doc/script/log`文件夹下的`data-simuldate.sh`文件，用于启动或者停止数据模拟器

### 离线数仓
#### 前端埋点数据采集
1. 采集Kafka数据转发到hdfs
	1. 打包`flume-interceptor`项目，按照`README.md`中的**页面事件处理说明处理**
	2. `doc/script/log`文件夹下的`kafka_to_hdfs.sh`文件，用于启动或者停止数据采集器，通过`flume`的`interceptor`分发到不同的hdfs路径 

#### 数仓流程
1. 创建hive表
	1. 在hive中创建`litemall`数据库 
	2. `doc/hive-schema`文件夹下为数仓每层对应的表结构，可以通过以下命令进行创建

			hive -f xxx.sql
	3. 执行`docs/hive_schema/date_info_gen.py`生存时间维度数据，并按照**加载时间维度数据**进行数据导入
2. 注册函数：参考`README.md`中的**自定义hive函数来解析events事件日志**
3. 提交Azkaban任务
	1.  `doc/script/hive`和`docs/scripts/sqoop`文件夹下是数仓每层的ETL执行脚本以及将mysql和hdfs的交互脚本
	2.  将`doc/script/azkaban`目录打包，上传到Azkaban中，定时每天执行ETL任务(注意修改为前面脚本对应的路径)
4. 创建report报表库
	1. 执行`doc/mysql`文件夹下的`litemall_report.sql`文件，创建`litemall_report`数据库及表，和初始化数据  

### 实时数仓
1. 开启binlog
	1. 开启mysql中`litemall`数据库的binlog 
2. 使用Maxwell采集业务数据到Kafka
	1. 下载安装Maxwell，并配置Kafka和mysql的信息

			producer=kafka
			kafka.bootstrap.servers=kafka主机信息
			
			# mysql login info
			host=mysql主机
			user=maxwell
			password=maxwell
			
			kafka_topic=maxwell-litemall-db
			kafka.retries=0
			kafka.acks=1
			client_id=client_1
			producer_partition_by=primary_key
	2. 执行脚本 
		1. `docs/scripts/maxwell/maxwell.sh`用于启动或停止Maxwell，执行增量导入 
		2. `docs/scripts/maxwell/maxwell-bootstrap.sh`用于全量到入某张表的数据
3. 创建hbase中的表
	1. hbase集成Phoenix
	2. 执行`docs/phoenix/litemall_phoenix.sql` 文件，初始化`LITEMALL`数据库表结构
4. 执行计算：`data-warehouse`模块
	1. 导入ods数据：执行`MaxWellOdsDispatcher`程序
	2. 初始化维度表数据，存储到hbase中
		1. 执行`org.tlh.rt.dw.ods.hbase` 中程序将商品品牌、分类及区域数据导入到hbase中，便于后期事实表聚合计算
	3. 启动dwd层
		1. 执行`org.tlh.rt.dw.dwd.dim` 中程序，将实时的用户和商品信息导入到hbase中(注意，先通过`maxwell-bootstrap.sh`导入全量数据)
		2. 执行`org.tlh.rt.dw.dwd.fact`中的程序，进行订单和订单详情的实时计算
	4. 执行`org.tlh.rt.dw.dws`的程序  

### 用户画像
1. 创建元数据数据库
	1. 执行`docs/mysql/user_profile.sql`文件，创建`user_profile`数据库及表，和初始化数据 
2. 执行画像模型
	1. 参考`README.md`中的**用户画像**的**环境准备**搭建hbase到solr的etl
	2. 执行`tags-model`模块中相应的模型即可将数据最终导入到solr中