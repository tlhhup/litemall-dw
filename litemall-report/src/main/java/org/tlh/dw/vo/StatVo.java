package org.tlh.dw.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 折线图vo
 *
 * @author 离歌笑
 * @desc
 * @date 2020-12-09
 */
public class StatVo {

    private String[] columns = new String[0];
    private List<Map> rows = new ArrayList<>();

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public List<Map> getRows() {
        return rows;
    }

    public void setRows(List<Map> rows) {
        this.rows = rows;
    }

    public void add(Map... r) {
        rows.addAll(Arrays.asList(r));
    }

}
