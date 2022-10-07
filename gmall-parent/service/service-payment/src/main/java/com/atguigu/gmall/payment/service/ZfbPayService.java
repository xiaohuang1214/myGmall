package com.atguigu.gmall.payment.service;

import com.alipay.api.AlipayApiException;

/**
 * @author 黄梁峰
 *
 * 支付宝支付相关接口
 */
public interface ZfbPayService {

    /**
     * 支付宝支付订单
     *
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    public String getPayOrder(String desc, String orderId, String money);


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
     * @param tradeNo
     * @return
     */
    public String closeOrder(String tradeNo);
}
