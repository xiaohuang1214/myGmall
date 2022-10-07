package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 首页三级分类导航
 * @author 黄梁峰
 */
public interface IndexService {

    /**
     * 查询首页三级分类导航
     * @return
     */
    public List<JSONObject> getIndexCategory();


}
