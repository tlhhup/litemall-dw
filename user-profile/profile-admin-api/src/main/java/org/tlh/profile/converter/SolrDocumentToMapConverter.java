package org.tlh.profile.converter;

import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocument;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.HashMap;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-07-04
 */
@ReadingConverter // 改注解必须添加 否则在solr注册converter的时候会被过滤掉
public class SolrDocumentToMapConverter implements Converter<SolrDocument, HashMap> {

    private DocumentObjectBinder documentObjectBinder;

    public SolrDocumentToMapConverter() {
        this.documentObjectBinder = new DocumentObjectBinder();
    }

    @Override
    public HashMap convert(SolrDocument source) {
        if (source == null) {
            return null;
        }

        return documentObjectBinder.getBean(HashMap.class, source);
    }

}