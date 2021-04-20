alter table tb_tag_metadata
    add column zk_port int comment 'zookeeper主机端口号' AFTER zk_hosts;