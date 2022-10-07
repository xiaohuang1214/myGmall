package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * @author 黄梁峰
 *
 * 搜索相关接口
 *
 */
public interface SearchService {

    /**
     * 搜索
     *
     * @param searchData
     * @return
     */
    public Map<String, Object> search(Map<String, String> searchData);
}
