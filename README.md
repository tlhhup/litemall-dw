基于litemall构建的数据仓库

1. 数据仓库
2. 推荐系统
3. 反爬虫

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
2. 将业务数据导入hdfs
	1. sqoop将mysql中`tinyint`的数据在HDFS 上面显示的`true、false`及在hive中需要使用`boolean`存储
		1.  原因：jdbc会把tinyint 认为是`java.sql.Types.BIT`
		2. 解决：`jdbc:mysql://localhost/test?tinyInt1isBit=false`
	2. sqoop：源数据含有默认换行符导致的数据不正确(出现数据错位)
		1. 解决：import添加参数

				# Drops \n, \r, and \01 from string fields when importing to Hive.
				--hive-drop-import-delims

## 致谢

本项目基于或参考以下项目：

1. [litemall](https://github.com/linlinjava/litemall)：又一个小商场系统，Spring Boot后端 + Vue管理员前端 + 微信小程序用户前端 + Vue用户移动端