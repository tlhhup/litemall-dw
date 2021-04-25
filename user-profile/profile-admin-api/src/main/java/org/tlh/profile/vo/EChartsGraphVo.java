package org.tlh.profile.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-25
 */
@Data
public class EChartsGraphVo {

    private Set<GraphNode> nodes = new HashSet<>();
    private Set<GraphLink> links = new HashSet<>();

    @Data
    @NoArgsConstructor
    public static final class GraphNode {

        @JsonIgnore
        private int id; // 只作 set中的数据对比用  不返回前端(会有问题)

        private String name;
        private int symbolSize= RandomUtils.nextInt(10,30);

        public GraphNode(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GraphNode graphNode = (GraphNode) o;
            return id == graphNode.id &&
                    Objects.equals(name, graphNode.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class GraphLink {

        // 表示数据节点的名称    如果是数字类型的 则表示的是数据的索引
        private String source;
        private String target;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GraphLink graphLink = (GraphLink) o;
            return source.equals(graphLink.source) &&
                    target.equals(graphLink.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target);
        }
    }

}
