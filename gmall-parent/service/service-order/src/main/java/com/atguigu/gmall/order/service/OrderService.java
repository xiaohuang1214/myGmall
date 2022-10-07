package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @author 黄梁峰
 */
public interface OrderService {

    /**
     * 生成订单
     *
     * @param orderInfo
     */
    public void getOrderInfo(OrderInfo orderInfo);

    /**
     * 取消订单
     *
     * @param orderId
     */
    public void cancelOrder(Long orderId);

    /**
     * 支付成功修改订单状态
     *
     * @param result
     */
    public void updateOrderStatus(String result);
}
