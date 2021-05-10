package org.tlh.profile.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.tlh.profile.entity.TbBasicTag;
import org.tlh.profile.entity.UserTag;
import org.tlh.profile.service.ITbBasicTagService;
import org.tlh.profile.service.IUserTagSearchService;
import org.tlh.profile.vo.EChartsGraphVo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@Slf4j
@Service
public class UserTagSearchServiceImpl implements IUserTagSearchService {

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
        Optional<UserTag> result = this.solrTemplate.queryForObject("litemall", query, UserTag.class);
        if (result.isPresent()) {
            UserTag userTag = result.get();

            //2. 将标签数据转换为graph数据
            Map<String, String> tags = this.bean2Map(userTag);
            //2.1 删除ID
            tags.remove("id");
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

    private void buildThisGraphNodeAndParent(String userName, int sourceId, EChartsGraphVo graph) {
        // 处理自己
        TbBasicTag basicTag = this.tagService.getById(sourceId);
        if (basicTag != null) {
            // 添加node
            EChartsGraphVo.GraphNode thisNode = new EChartsGraphVo.GraphNode(sourceId, basicTag.getName());
            graph.getNodes().add(thisNode);
            // 处理父级节点
            if (basicTag.getPid() != null) {
                int pId = basicTag.getPid().intValue();
                TbBasicTag parent = this.tagService.getById(pId);
                // 链接父节点
                EChartsGraphVo.GraphLink thisLink = new EChartsGraphVo.GraphLink(basicTag.getName(), parent.getName());
                graph.getLinks().add(thisLink);

                buildThisGraphNodeAndParent(userName, pId, graph);
            } else {
                // 添加关系,顶层节点
                EChartsGraphVo.GraphLink thisLink = new EChartsGraphVo.GraphLink(basicTag.getName(), userName);
                graph.getLinks().add(thisLink);
            }
        }
    }

    private Map<String, String> bean2Map(Object bean) {
        Map<String, String> result = new HashMap<>();
        Class<?> clazz = bean.getClass();
        //1. 获取所有属性
        Field[] fields = clazz.getDeclaredFields();
        //2. 处理属性
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(bean);
                if (value != null) {
                    result.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                log.error("copy field error", e);
            }
        }
        return result;
    }

}
