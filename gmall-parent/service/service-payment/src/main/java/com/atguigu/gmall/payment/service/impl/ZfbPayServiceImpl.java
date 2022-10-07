package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.payment.service.ZfbPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author 黄梁峰
 * <p>
 * 支付宝支付相关接口的实现类
 */
@Service
public class ZfbPayServiceImpl implements ZfbPayService {

    @Value("${ali.alipayUrl}")
    private String alipayUrl;

    @Value("${ali.appId}")
    private String appId;

    @Value("${ali.appPrivateKey}")
    private String appPrivateKey;

    @Value("${ali.alipayPublicKey}")
    private String alipayPublicKey;

    @Value("${ali.returnPaymentUrl}")
    private String returnPaymentUrl;

    @Value("${ali.notifyPaymentUrl}")
    private String notifyPaymentUrl;


    /**
     * 支付宝支付订单
     *
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    @Override
    public String getPayOrder(String desc, String orderId, String money) {
        //初始化
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayUrl,
                appId,
                appPrivateKey,
                "json",
                "utf-8",
                alipayPublicKey,
                "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyPaymentUrl);
        request.setReturnUrl(returnPaymentUrl);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", money);
        bizContent.put("subject", desc);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(bizContent.toString());
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据订单号查询支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {

        //初始化
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayUrl,
                appId,
                appPrivateKey,
                "json",
                "utf-8",
                alipayPublicKey,
                "RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭订单
     *
     * @param tradeNo
     * @return
     */
    @Override
    public String closeOrder(String tradeNo) {
        //初始化
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayUrl,
                appId,
                appPrivateKey,
                "json",
                "utf-8",
                alipayPublicKey,
                "RSA2");
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", tradeNo);
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
