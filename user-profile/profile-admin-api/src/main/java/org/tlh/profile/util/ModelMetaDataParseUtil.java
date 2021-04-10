package org.tlh.profile.util;

import org.tlh.profile.entity.TbTagMetadata;
import org.tlh.profile.enums.MetaDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-24
 */
public final class ModelMetaDataParseUtil {

    // hive or RDBMS
    private static final String DRIVER = "driver";
    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String DB_TABLE = "dbTable";
    private static final String QUERY_SQL = "querySql";
    // hdfs
    private static final String IN_PATH = "inPath";
    private static final String SEPARATOR = "separator";
    private static final String OUT_PATH = "outPath";
    // hbase
    private static final String ZK_HOSTS = "zkHosts";
    private static final String HBASE_NAMESPACE = "hbaseNamespace";
    private static final String HBASE_TABLE = "hbaseTable";
    private static final String ROW_KEY = "rowKey";
    private static final String FAMILY = "family";
    // commons
    private static final String SELECT_FIELD_NAMES = "selectFieldNames";
    private static final String WHERE_FIELD_NAMES = "whereFieldNames";
    private static final String WHERE_FIELD_VALUES = "whereFieldValues";
    private static final String OUT_FIELDS = "outFields";
    private static final String IN_TYPE = "inType";

    private ModelMetaDataParseUtil() {
    }

    public static TbTagMetadata parse(String rule) {
        //1.转换为map
        String[] rules = rule.split("\n");
        Map<String, String> ruleMap = new HashMap<>();
        for (String s : rules) {
            String[] data = s.split("=");
            ruleMap.put(data[0], data[1]);
        }
        //2.校验元数据类型
        if (!ruleMap.containsKey(IN_TYPE)) {
            throw new IllegalArgumentException("inType must not be null");
        }
        String inType = ruleMap.get(IN_TYPE);
        MetaDataType convert = MetaDataType.convert(inType);
        TbTagMetadata result = new TbTagMetadata();
        switch (convert) {
            case HDFS:
                result.setInPath(ruleMap.get(IN_PATH));
                result.setSperator(ruleMap.get(SEPARATOR));
                result.setOutPath(ruleMap.get(OUT_PATH));
                break;
            case HBASE:
                result.setZkHosts(ruleMap.get(ZK_HOSTS));
                result.setHbaseNamespace(ruleMap.get(HBASE_NAMESPACE));
                result.setHbaseTable(ruleMap.get(HBASE_TABLE));
                result.setRowKey(ruleMap.get(ROW_KEY));
                result.setFamily(ruleMap.get(FAMILY));
                break;
            case HIVE:
            case MYSQL:
                result.setUrl(ruleMap.get(URL));
                result.setDriver(ruleMap.get(DRIVER));
                result.setUser(ruleMap.get(USER));
                result.setPassword(ruleMap.get(PASSWORD));
                result.setDbTable(ruleMap.get(DB_TABLE));
                result.setQuerySql(ruleMap.get(QUERY_SQL));
                break;
        }
        //3.处理公共字段
        result.setInType(convert.getType());
        result.setSelectFieldNames(ruleMap.get(SELECT_FIELD_NAMES));
        result.setWhereFieldNames(ruleMap.get(WHERE_FIELD_NAMES));
        result.setWhereFieldValues(ruleMap.get(WHERE_FIELD_VALUES));
        result.setOutFields(ruleMap.get(OUT_FIELDS));
        return result;
    }

}
