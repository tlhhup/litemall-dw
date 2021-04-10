alter table tb_tag_metadata
    add column row_key varchar(100) comment 'hBase 的rowkey' AFTER hbase_table,
    add column hbase_namespace varchar(100) comment 'hBase 表的 namespace' AFTER zk_hosts;