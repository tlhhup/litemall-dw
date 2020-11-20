基于litemall构建的数据仓库

1. 数据仓库
2. 推荐系统
3. 反爬虫

## 数据模拟
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


## 致谢

本项目基于或参考以下项目：

1. [litemall](https://github.com/linlinjava/litemall)：又一个小商场系统，Spring Boot后端 + Vue管理员前端 + 微信小程序用户前端 + Vue用户移动端