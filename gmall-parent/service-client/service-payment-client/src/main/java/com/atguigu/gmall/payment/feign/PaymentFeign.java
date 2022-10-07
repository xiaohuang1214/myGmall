package com.atguigu.gmall.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 微信支付feign接口
 *
 * @author 黄梁峰
 */
@FeignClient(name = "service-payment", path = "/wx/pay", contextId = "paymentFeign")
public interface PaymentFeign {

    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    @GetMapping("/closeOrder/{orderId}")
    public String closeOrder(@PathVariable("orderId") String orderId);

}
