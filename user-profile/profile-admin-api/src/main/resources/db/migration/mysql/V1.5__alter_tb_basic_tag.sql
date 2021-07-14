alter table tb_basic_tag
    add column hbase_fields varchar(100) comment '冗余字段，对应hbase中的列名';