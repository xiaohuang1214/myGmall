package com.atguigu.gmall.item.service;


import java.util.Map;

/**
 * @author 黄梁峰
 */
public interface ItemService {

    /**
     * 查询商品详情信息
     * @param skuId
     * @return
     * @throws Exception
     */
    public Map getItemInfo(Long skuId);

}
