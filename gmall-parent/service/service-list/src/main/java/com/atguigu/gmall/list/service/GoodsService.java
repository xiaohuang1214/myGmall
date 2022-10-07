package com.atguigu.gmall.list.service;

/**
 * @author 黄梁峰
 * <p>
 * es中商品相关的接口
 */
public interface GoodsService {

    /**
     * 将上架的商品写入es
     *
     * @param skuId
     */
    public void addGoodsToEs(Long skuId);

    /**
     * 将商品从es中删除
     *
     * @param goodsId
     */
    public void deleteGoodsToEs(Long goodsId);

    /**
     * 热点数据
     *
     * @param goodsId
     */
    public void getHotScore(Long goodsId);
}
