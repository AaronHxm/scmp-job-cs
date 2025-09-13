package com.scmp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 查询响应类，用于封装API返回的数据
 * @param <T> 响应数据类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResponse<T> {
    private List<T> rows; // 数据列表
    private int pagecount; // 总页数
    private int total; // 总条数
    private int pageindex; // 当前页码
    private int pagesize; // 每页条数
    
    // getter和setter方法
    public List<T> getRows() {
        return rows;
    }
    
    public void setRows(List<T> rows) {
        this.rows = rows;
    }
    
    public int getPagecount() {
        return pagecount;
    }
    
    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public int getPageindex() {
        return pageindex;
    }
    
    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }
    
    public int getPagesize() {
        return pagesize;
    }
    
    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }
}