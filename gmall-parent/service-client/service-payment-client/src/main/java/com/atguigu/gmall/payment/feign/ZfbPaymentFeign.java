package com.atguigu.gmall.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 黄梁峰
 *
 * 支付宝支付feign接口
 */
@FeignClient(name = "service-payment", path = "/zfb/pay", contextId = "zfbPaymentFeign")
public interface ZfbPaymentFeign {

    /**
     * 获取支付结果
     *
     * @param orderId
     * @return
     */
    @GetMapping("/getPayResult/{orderId}")
    public String getPayResult(@PathVariable("orderId") String orderId);


    /**
     * 关闭订单
     *
     * @param tradeNo
     * @return
     */
    @GetMapping("/closeOrder/{tradeNo}")
    public String closeOrder(@PathVariable("tradeNo") String tradeNo);
}
