package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @author 黄梁峰
 *
 * 秒杀商品相关接口
 */
public interface SeckillGoodsService {

    /**
     * 获取当前时段的商品
     *
     * @param time
     * @return
     */
    public List<SeckillGoods> getSeckillGoodsList(String time);

    /**
     * 查询指定商品
     *
     * @param time
     * @param goodsId
     * @return
     */
    public SeckillGoods getSeckillGoods(String time, String goodsId);

    /**
     * 数据同步
     *
     * @param key
     */
    public void mergeSeckillGoodsStockToDb(String key);
}
