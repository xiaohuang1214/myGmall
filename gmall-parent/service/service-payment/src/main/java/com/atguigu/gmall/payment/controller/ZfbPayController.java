package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.ZfbPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 黄梁峰
 * 支付宝支付管理控制
 */
@RestController
@RequestMapping(value = "/zfb/pay")
public class ZfbPayController {
    @Resource
    private ZfbPayService zfbPayService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 跳转支付页面支付
     *
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    @GetMapping("/getPayPage")
    public String getPayPage(String desc, String orderId, String money) {
        return zfbPayService.getPayOrder(desc, orderId, money);
    }

    /**
     * 获取支付结果
     *
     * @param orderId
     * @return
     */
    @GetMapping("/getPayResult/{orderId}")
    public String getPayResult(@PathVariable("orderId") String orderId) {
        return JSONObject.toJSONString(zfbPayService.getPayResult(orderId));
    }

    /**
     * 同步回调
     *
     * @return
     */
    @RequestMapping("/callback/return")
    public String callbackReturn(@RequestParam Map<String, String> params) {
        System.out.println(JSONObject.toJSONString(params));
        return "回调成功";
    }

    /**
     * 异步回调
     *
     * @return
     */
    @RequestMapping("/callback/notify")
    public String callbackNotify(@RequestParam Map<String, String> params) {
        params.put("payway", "0");
        rabbitTemplate.convertAndSend("pay_exchange", "order.pay", JSONObject.toJSONString(params));
        return "success";
    }

    /**
     * 关闭订单
     *
     * @param tradeNo
     * @return
     */
    @GetMapping("/closeOrder/{tradeNo}")
    public String closeOrder(@PathVariable("tradeNo") String tradeNo){
        return zfbPayService.closeOrder(tradeNo);
    }
}
