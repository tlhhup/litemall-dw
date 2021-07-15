package org.tlh.profile.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.FacetPivotFieldEntry;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.tlh.profile.dto.MergeTagSolrDto;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.entity.solr.FacetEntity;
import org.tlh.profile.entity.solr.UserTag;
import org.tlh.profile.enums.QueryCondition;
import org.tlh.profile.mapper.TbMergeTagMapper;
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

    @Autowired
    private TbMergeTagMapper mergeTagMapper;

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

    @Override
    public List<BasicTagFacetVo> mergeTagFact(int id, Integer page, Integer limit) {
        //1. 查询solr的条件列表
        List<MergeTagSolrDto> conditions = this.mergeTagMapper.queryMergeTagSolr(id);
        if (ObjectUtils.isEmpty(conditions)) {
            throw new IllegalStateException("This tag does not have any merge tags!");
        }

        //2. 构建solr查询条件 及 facet信息
        List<String> pivots = new ArrayList<>();
        List<QueryCondition> queryConditions = new ArrayList<>();
        conditions.forEach(item -> {
            queryConditions.add(new QueryCondition(
                    item.getFiledName(),
                    item.getFiledTagId(),
                    QueryCondition.Operator.convert(item.getCondition())));
            pivots.add(item.getFiledName());
        });

        // 设置条件
        Criteria criteria = QueryCondition.buildCriteria(queryConditions);
        FacetQuery query = new SimpleFacetQuery();
        query.addCriteria(criteria);
        // 设置pivot
        FacetOptions facetOptions = new FacetOptions();
        facetOptions.addFacetOnPivot(pivots.toArray(new String[conditions.size()]));
        query.setFacetOptions(facetOptions);
        // 设置分页参数
        query.setOffset((page - 1L) * limit);
        query.setRows(limit);
        // 设置之返回用户ID
        query.addProjectionOnField(new SimpleField("id"));

        //3. 查询数据
        FacetPage<UserTag> result = this.solrTemplate.queryForFacetPage(COLLECTION, query, UserTag.class);

        //4. 组合数据
        //4.1 解析pivot信息
        List<FacetPivotFieldEntry> pivot = result.getPivot(String.join(",", pivots));
        // 得到所有的tagId
        List<Long> tagIds = new ArrayList<>();
        this.parsePivotTagId(tagIds, pivot);
        // 查询得到标签信息
        Collection<TbBasicTag> tags = this.tagService.listByIds(tagIds);
        Map<Long, String> idWithName = new HashMap<>();
        tags.forEach(item -> idWithName.put(item.getId(), item.getName()));
        List<BasicTagFacetVo> pivotInfo = this.parsePivotInfo(pivot, idWithName);

        return pivotInfo;
    }

    /************************************/

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

    private void parsePivotTagId(List<Long> tagIds, List<FacetPivotFieldEntry> pivot) {
        pivot.forEach(item -> {
            tagIds.add(Long.parseLong(item.getValue()));
            //处理子节点
            List<FacetPivotFieldEntry> child = item.getPivot();
            if (!ObjectUtils.isEmpty(child)) {
                parsePivotTagId(tagIds, child);
            }
        });
    }

    private List<BasicTagFacetVo> parsePivotInfo(List<FacetPivotFieldEntry> pivots, Map<Long, String> tagIdWithMap) {
        List<BasicTagFacetVo> result = new ArrayList<>();
        pivots.forEach(item -> {
            long tagId = Long.parseLong(item.getValue());
            BasicTagFacetVo i = new BasicTagFacetVo(tagId, tagIdWithMap.get(tagId), item.getValueCount());
            result.add(i);

            //处理子节点
            List<FacetPivotFieldEntry> child = item.getPivot();
            if (!ObjectUtils.isEmpty(child)) {
                List<BasicTagFacetVo> children = parsePivotInfo(child, tagIdWithMap);
                i.setChild(children);
            }
        });
        return result;
    }
}
