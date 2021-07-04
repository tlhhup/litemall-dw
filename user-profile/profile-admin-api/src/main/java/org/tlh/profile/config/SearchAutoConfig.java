package org.tlh.profile.config;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.convert.MappingSolrConverter;
import org.springframework.data.solr.core.convert.SolrCustomConversions;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.tlh.profile.converter.SolrDocumentToMapConverter;

import java.util.Arrays;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@Configuration
public class SearchAutoConfig {

    @Bean
    @ConditionalOnMissingBean(SolrTemplate.class)
    public SolrTemplate solrTemplate(SolrClient solrClient) {

        SolrCustomConversions customConversions = new SolrCustomConversions(Arrays.asList(new SolrDocumentToMapConverter()));

        MappingSolrConverter converter = new MappingSolrConverter(new SimpleSolrMappingContext());
        converter.setCustomConversions(customConversions);

        SolrTemplate solrTemplate = new SolrTemplate(solrClient);
        solrTemplate.setSolrConverter(converter);

        return solrTemplate;
    }

}
