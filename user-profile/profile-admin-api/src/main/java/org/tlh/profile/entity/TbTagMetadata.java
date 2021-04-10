package org.tlh.profile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 标签数据元数据信息
 * </p>
 *
 * @author 离歌笑
 * @since 2021-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbTagMetadata implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 数据源类型： 1 RDBMS 2 File 3 Hbase 4 Hive
     */
    private Integer inType;

    /**
     * RDBMS数据库驱动
     */
    private String driver;

    /**
     * RDBMS数据库连接地址
     */
    private String url;

    /**
     * RDBMS数据库用户名
     */
    private String user;

    /**
     * RDBMS数据库密码
     */
    private String password;

    /**
     * RDBMS数据库表名
     */
    private String dbTable;

    /**
     * 查询的sql语句
     */
    private String querySql;

    /**
     * 文件地址
     */
    private String inPath;

    /**
     * 分隔符
     */
    private String sperator;

    /**
     * 处理后输出的文件地址
     */
    private String outPath;

    /**
     * zookeeper主机地址, 格式： host:port
     */
    private String zkHosts;

    private String hbaseNamespace;
    private String rowKey;

    /**
     * Hbase数据源中的表名
     */
    private String hbaseTable;

    /**
     * hbase数据源列簇
     */
    private String family;

    /**
     * 查询结果集中的列名，采用","分隔
     */
    private String selectFieldNames;

    /**
     * 查询where 字段
     */
    private String whereFieldNames;

    /**
     * 查询where 字段值
     */
    private String whereFieldValues;

    /**
     * 处理之后的输出字段
     */
    private String outFields;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;


}
