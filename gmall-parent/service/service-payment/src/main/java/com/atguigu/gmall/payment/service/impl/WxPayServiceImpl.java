package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.util.HttpClient;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄梁峰
 * 微信支付相关接口的实现类
 *
 */
@Service
public class WxPayServiceImpl implements WxPayService {
    @Value("${weixin.pay.appid}")
    private String appid;

    @Value("${weixin.pay.partner}")
    private String partner;

    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;


    /**
     * 微信支付订单
     *
     * @param param
     * @return
     */
    @Override
    public String getPayOrder(Map<String, String> param) {
        //封装参数
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("appid",appid);
        resultMap.put("mch_id",partner);
        resultMap.put("nonce_str",WXPayUtil.generateNonceStr());
        resultMap.put("body",param.get("desc"));
        resultMap.put("out_trade_no",param.get("orederId"));
        resultMap.put("total_fee",param.get("money"));
        resultMap.put("spbill_create_ip","127.0.0.1");
        resultMap.put("notify_url",notifyUrl);
        resultMap.put("trade_type","NATIVE");
        //定义附加参数
        HashMap<String, String> attachMap = new HashMap<>();
        attachMap.put("exchange",param.get("exchange"));
        attachMap.put("routingKey",param.get("routingKey"));
        attachMap.put("username",param.get("username"));
        //添加附加参数
        resultMap.put("attach",JSONObject.toJSONString(attachMap));

        try {
            //将map类型转换为xml同时生成签名
            String xmlParam = WXPayUtil.generateSignedXml(resultMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            //发起post请求
            httpClient.post();
            //获得响应数据
            String content = httpClient.getContent();
            //解析响应数据
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            //返回结果
            return JSONObject.toJSONString(result);
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
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("appid",appid);
        resultMap.put("mch_id",partner);
        resultMap.put("nonce_str",WXPayUtil.generateNonceStr());
        resultMap.put("out_trade_no",orderId);
        try {
            //将map类型转换为xml同时生成签名
            String xmlParam = WXPayUtil.generateSignedXml(resultMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            //发起post请求
            httpClient.post();
            //获得响应数据
            String content = httpClient.getContent();
            //解析响应数据
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            //返回结果
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    @Override
    public String closeOrder(String orderId) {
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("appid",appid);
        resultMap.put("mch_id",partner);
        resultMap.put("nonce_str",WXPayUtil.generateNonceStr());
        resultMap.put("out_trade_no",orderId);
        try{
            //将map类型转换为xml同时生成签名
            String xmlParam = WXPayUtil.generateSignedXml(resultMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            //设置参数
            httpClient.setXmlParam(xmlParam);
            //是否为https方式
            httpClient.setHttps(true);
            //发起post请求
            httpClient.post();
            //获得响应数据
            String content = httpClient.getContent();
            //解析响应数据
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            //返回
            return JSONObject.toJSONString(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
