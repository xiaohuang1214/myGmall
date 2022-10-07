package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * @author 黄梁峰
 *
 * 微信支付相关接口
 */
public interface WxPayService {

    /**
     * 微信支付订单
     *
     * @param param
     * @return
     */
    public String getPayOrder(Map<String, String> param);


    /**
     * 根据订单号查询支付结果
     *
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);

    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    public String closeOrder(String orderId);
}
