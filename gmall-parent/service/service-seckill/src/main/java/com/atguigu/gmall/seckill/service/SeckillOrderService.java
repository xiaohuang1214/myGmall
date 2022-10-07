package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.seckill.pojo.UserRecode;

/**
 * @author 黄梁峰
 * 秒杀订单相关接口
 */
public interface SeckillOrderService {

    /**
     * 添加秒杀订单
     *  @param time
     * @param goodsId
     * @param num
     * @return
     */
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num);

    /**
     * 查询用户排队状态
     * @return
     */
    public UserRecode getUserStatus();

    /**
     * 秒杀真实下单
     *
     * @param userRecode
     */
    public void realSeckillOrderAdd(UserRecode userRecode) throws Exception;

    /**
     * 取消订单
     *
     * @param userRecode
     */
    public void cancelSeckillOrder(UserRecode userRecode);

    /**
     * 支付成功修改订单状态
     *
     * @param result
     */
    public void updateSeckillOrderStatus(String result);
}
