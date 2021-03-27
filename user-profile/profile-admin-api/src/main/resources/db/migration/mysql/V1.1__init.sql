/************ Update: Tables ***************/ /******************** Add Table: tb_basic_tag ************************/ /* Build Table Structure */
CREATE TABLE tb_basic_tag ( id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, name VARCHAR(100) COMMENT '标签名称' NOT NULL, industry VARCHAR(200) COMMENT '行业、子行业、业务类型、标签、属性' NOT NULL, rule VARCHAR(500) COMMENT '标签规则: 四级 metadata表中数据 五级 值域' NULL, business VARCHAR(200) COMMENT '业务描述' NULL, level INTEGER COMMENT '标签等级' NULL, pid BIGINT COMMENT '父标签ID' NULL, `order` INTEGER COMMENT '子标签排序字段' NULL, create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间' NOT NULL, update_time TIMESTAMP COMMENT '更新时间' NULL, state INTEGER COMMENT '状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用' NULL, remark VARCHAR(200) COMMENT '备注' NULL) ENGINE=InnoDB;

 /* Add Comments */
ALTER TABLE tb_basic_tag COMMENT = '基础标签';

 /******************** Add Table: tb_merge_tag ************************/ /* Build Table Structure */
CREATE TABLE tb_merge_tag ( id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, name VARCHAR(100) COMMENT '组合标签名称' NOT NULL, `condition` VARCHAR(100) COMMENT '组合标签条件' NULL, intro VARCHAR(100) COMMENT '组合标签含义' NULL, purpose VARCHAR(100) COMMENT '组合用途' NULL, remark VARCHAR(100) COMMENT '备注' NULL, create_time DATETIME DEFAULT current_timestamp NOT NULL, update_time TIMESTAMP NULL, state INTEGER COMMENT '状态：1申请中、2开发中、3开发完成、4已上线、5已下线、6已禁用' NULL) ENGINE=InnoDB;

 /* Add Comments */
ALTER TABLE tb_merge_tag COMMENT = '组合标签';

 /******************** Add Table: tb_merge_tag_detail ************************/ /* Build Table Structure */
CREATE TABLE tb_merge_tag_detail ( id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, merge_tag_id BIGINT COMMENT '组合标签ID' NOT NULL, basic_tag_id BIGINT COMMENT '基础标签ID（1级行业 or 5级属性）' NOT NULL, `condition` INTEGER COMMENT '条件间关系： 1 and 2 or 3 not' NOT NULL, condition_order INTEGER DEFAULT 0 COMMENT '条件顺序' NOT NULL, remark VARCHAR(200) COMMENT '备注' NULL, create_time DATETIME DEFAULT current_timestamp NOT NULL, update_time TIMESTAMP NULL) ENGINE=InnoDB;

 /* Add Comments */
ALTER TABLE tb_merge_tag_detail COMMENT = '组合标签规则详情';

 /******************** Add Table: tb_tag_metadata ************************/ /* Build Table Structure */
CREATE TABLE tb_tag_metadata ( id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, tag_id BIGINT COMMENT '标签ID' NOT NULL, in_type INTEGER COMMENT '数据源类型： 1 RDBMS 2 File 3 Hbase 4 Hive' NOT NULL, driver VARCHAR(200) COMMENT 'RDBMS数据库驱动' NULL, url VARCHAR(300) COMMENT 'RDBMS数据库连接地址' NULL, user VARCHAR(200) COMMENT 'RDBMS数据库用户名' NULL, password VARCHAR(100) COMMENT 'RDBMS数据库密码' NULL, db_table VARCHAR(100) COMMENT 'RDBMS数据库表名' NULL, query_sql VARCHAR(500) COMMENT '查询的sql语句' NULL, in_path VARCHAR(500) COMMENT '文件地址' NULL, sperator VARCHAR(50) COMMENT '分隔符' NULL, out_path VARCHAR(500) COMMENT '处理后输出的文件地址' NULL, zk_hosts VARCHAR(200) COMMENT 'zookeeper主机地址, 格式： host:port' NULL, hbase_table VARCHAR(100) COMMENT 'Hbase数据源中的表名' NULL, family VARCHAR(100) COMMENT 'hbase数据源列簇' NULL, select_field_names VARCHAR(200) COMMENT '查询结果集中的列名，采用","分隔' NULL, where_field_names VARCHAR(200) COMMENT '查询where 字段' NULL, where_field_values VARCHAR(200) COMMENT '查询where 字段值' NULL, out_fields VARCHAR(200) COMMENT '处理之后的输出字段' NULL, create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间' NOT NULL, update_time TIMESTAMP COMMENT '更新时间' NULL, state INTEGER COMMENT '状态' NULL, remark VARCHAR(200) COMMENT '备注' NULL) ENGINE=InnoDB;

 /* Add Comments */
ALTER TABLE tb_tag_metadata COMMENT = '标签数据元数据信息';

 /******************** Add Table: tb_tag_model ************************/ /* Build Table Structure */
CREATE TABLE tb_tag_model ( id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, tag_id BIGINT COMMENT '标签ID(四级标签)' NOT NULL, model_type INTEGER COMMENT '模型类型：1 匹配 2 统计 3 挖掘' NULL, model_name VARCHAR(100) COMMENT '模型名称' NULL, model_main VARCHAR(200) COMMENT '模型driver全限定名' NOT NULL, model_path VARCHAR(500) COMMENT '模型在hdfs中的地址' NOT NULL, model_jar VARCHAR(500) COMMENT '模型jar包文件名' NOT NULL, model_args VARCHAR(500) COMMENT '模型参数' NULL, spark_opts VARCHAR(1000) COMMENT 'spark的执行参数' NULL, schedule_rule VARCHAR(100) COMMENT 'oozie的调度规则' NOT NULL,
                           operator VARCHAR(100) COMMENT '操作人' NULL, operation VARCHAR(100) COMMENT '操作类型' NULL, create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间' NOT NULL, update_time TIMESTAMP COMMENT '更新时间' NULL, state INTEGER COMMENT '状态' NULL) ENGINE=InnoDB;

 /* Add Comments */
ALTER TABLE tb_tag_model COMMENT = '标签模型';

 /************ Add Foreign Keys ***************/ /* Add Foreign Key: fk_tb_merge_tag_detail_tb_basic_tag */
ALTER TABLE tb_merge_tag_detail ADD CONSTRAINT fk_tb_merge_tag_detail_tb_basic_tag
FOREIGN KEY (basic_tag_id) REFERENCES tb_basic_tag (id) ON
UPDATE NO ACTION ON
DELETE NO ACTION;

 /* Add Foreign Key: fk_tb_merge_tag_detail_tb_merge_tag */
ALTER TABLE tb_merge_tag_detail ADD CONSTRAINT fk_tb_merge_tag_detail_tb_merge_tag
FOREIGN KEY (merge_tag_id) REFERENCES tb_merge_tag (id) ON
UPDATE NO ACTION ON
DELETE NO ACTION;

 /* Add Foreign Key: fk_tb_tag_metadata_tb_basic_tag */
ALTER TABLE tb_tag_metadata ADD CONSTRAINT fk_tb_tag_metadata_tb_basic_tag
FOREIGN KEY (tag_id) REFERENCES tb_basic_tag (id) ON
UPDATE NO ACTION ON
DELETE NO ACTION;

 /* Add Foreign Key: fk_tb_tag_model_tb_basic_tag */
ALTER TABLE tb_tag_model ADD CONSTRAINT fk_tb_tag_model_tb_basic_tag
FOREIGN KEY (tag_id) REFERENCES tb_basic_tag (id) ON
UPDATE NO ACTION ON
DELETE NO ACTION;