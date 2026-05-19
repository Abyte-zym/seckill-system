package com.seckill.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 — 企业级API的分页标准返回体
 * 面试考点：分页字段设计 / total vs hasMore / 游标分页vs偏移分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /** 当前页码(从1开始) */
    private int page;
    
    /** 每页条数 */
    private int size;
    
    /** 总条数 */
    private long total;
    
    /** 总页数 */
    private int totalPages;
    
    /** 数据列表 */
    private List<T> records;

    public static <T> PageResult<T> of(int page, int size, long total, List<T> records) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResult<>(page, size, total, totalPages, records);
    }
}
