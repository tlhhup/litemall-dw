package org.tlh.profile.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.entity.solr.FacetEntity;
import org.tlh.profile.service.ITbBasicTagService;
import org.tlh.profile.service.IUserTagSearchService;
import org.tlh.profile.vo.BasicTagFacetVo;
import org.tlh.profile.vo.EChartsGraphVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@Slf4j
@Service
public class UserTagSearchServiceImpl implements IUserTagSearchService {

    private static final String COLLECTION = "litemall";

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ITbBasicTagService tagService;

    @Override
    public EChartsGraphVo searchUserTagById(int id) {
        EChartsGraphVo graphVo = new EChartsGraphVo();

        //1. 查询用户标签
        SimpleQuery query = new SimpleQuery();
        query.addCriteria(Criteria.where("id").is(id));

        Optional<HashMap> result = this.solrTemplate.queryForObject(COLLECTION, query, HashMap.class);

        if (result.isPresent()) {
            HashMap<String, String> tags = result.get();

            //2. 将标签数据转换为graph数据
            //2.1 删除ID
            tags.remove("id");
            tags.remove("_version_");
            //2.2 处理标签
            for (String tagId : tags.values()) {
                if (StringUtils.hasText(tagId)) {
                    TbBasicTag basicTag = this.tagService.getById(tagId);
                    if (!ObjectUtils.isEmpty(basicTag)) {
                        buildThisGraphNodeAndParent("用户", basicTag.getId().intValue(), graphVo);
                    }
                }
            }
            //2.3 添加用户node
            graphVo.getNodes().add(new EChartsGraphVo.GraphNode(id, "用户"));
        }
        return graphVo;
    }

    @Override
    public List<BasicTagFacetVo> basicTagFacet(int id) {
        //0. 获取标签对应的hbase中的字段名
        TbBasicTag basicTag = this.tagService.getById(id);
        String filed = basicTag.getHbaseFields();
        if (StringUtils.isEmpty(filed)) {
            throw new IllegalStateException("This tag does not have any filed!");
        }

        //1. 查询统计信息
        FacetQuery query = new SimpleFacetQuery();
        query.addCriteria(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD));
        query.setFacetOptions(new FacetOptions(filed));
        FacetPage<FacetEntity> facets = this.solrTemplate.queryForFacetPage(COLLECTION, query, FacetEntity.class);

        List<FacetFieldEntry> content = facets.getFacetResultPage(filed).getContent();

        //2. 构建放回数据
        if (!ObjectUtils.isEmpty(content)) {
            //2.1 转化为map
            Map<Long, Long> itemCount = new HashMap<>();
            content.forEach(item -> itemCount.put(Long.parseLong(item.getValue()), item.getValueCount()));
            //2.2 查询tag信息
            Collection<TbBasicTag> tags = this.tagService.listByIds(itemCount.keySet());
            //2.3 转化数据
            List<BasicTagFacetVo> result = tags.stream()
                    .map(item -> new BasicTagFacetVo(item.getId(), item.getName(), itemCount.get(item.getId())))
                    .collect(Collectors.toList());
            return result;
        }

        return null;
    }

    private void buildThisGraphNodeAndParent(String userName, int sourceId, EChartsGraphVo graph) {
        // 处理自己
        TbBasicTag basicTag = this.tagService.getById(sourceId);
        if (basicTag != null) {
            // 添加node
            EChartsGraphVo.GraphNode thisNode = new EChartsGraphVo.GraphNode(sourceId, buildNodeName(basicTag));
            graph.getNodes().add(thisNode);
            // 处理父级节点
            if (basicTag.getPid() != null) {
                int pId = basicTag.getPid().intValue();
                TbBasicTag parent = this.tagService.getById(pId);
                // 链接父节点
                EChartsGraphVo.GraphLink thisLink = new EChartsGraphVo.GraphLink(buildNodeName(basicTag), buildNodeName(parent));
                graph.getLinks().add(thisLink);

                buildThisGraphNodeAndParent(userName, pId, graph);
            } else {
                // 添加关系,顶层节点
                EChartsGraphVo.GraphLink thisLink = new EChartsGraphVo.GraphLink(buildNodeName(basicTag), userName);
                graph.getLinks().add(thisLink);
            }
        }
    }

    private String buildNodeName(TbBasicTag basicTag) {
        StringBuilder builder = new StringBuilder();
        builder.append(basicTag.getName())
                .append(":")
                .append(basicTag.getId());

        return builder.toString();
    }

}
