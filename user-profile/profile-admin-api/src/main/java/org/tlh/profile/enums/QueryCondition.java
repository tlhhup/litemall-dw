package org.tlh.profile.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.solr.core.query.Criteria;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-04-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryCondition {

    private String field;
    private Object fieldValue;
    private Operator operator = Operator.NONE;

    @Getter
    public enum Operator {
        AND(1,"AND"), OR(2,"OR"), NONE(3,"");

        private int type;
        private String operator;

        Operator(int type, String operator) {
            this.type = type;
            this.operator = operator;
        }

        public static Operator convert(String operator){
            Optional<Operator> first = Arrays.stream(values()).filter(item -> item.operator.equalsIgnoreCase(operator)).findFirst();
            if (first.isPresent()) {
                return first.get();
            } else {
                throw new IllegalArgumentException("Not Support Operator:" + operator);
            }
        }
    }

    public QueryCondition(String field, Object fieldValue) {
        this.field = field;
        this.fieldValue = fieldValue;
    }

    public Criteria toCriteria() {
        Criteria criteria = Criteria.where(this.field).is(this.fieldValue);
        return criteria;
    }

    public static Criteria buildCriteria(List<QueryCondition> conditions) {
        if (conditions == null || conditions.size() == 0) {
            throw new IllegalArgumentException("conditions must at least one!");
        }
        QueryCondition head = conditions.get(0);
        Criteria criteria = head.toCriteria();
        Operator operator = head.getOperator();
        for (int i = 1; i < conditions.size(); i++) {
            QueryCondition other = conditions.get(i);
            switch (operator) {
                case AND:
                    criteria = criteria.and(other.toCriteria());
                    break;
                case OR:
                    criteria = criteria.or(other.toCriteria());
                    break;
                default:
                    break;
            }
            operator = other.operator;
        }
        return criteria;
    }

}
