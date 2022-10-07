package com.atguigu.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author 黄梁峰
 * 购物车相关feign接口
 */
@FeignClient(name = "service-cart", path = "/api/cart", contextId = "cartFeign")
public interface CartFeign {

    /**
     * 获取购物车信息和用户地址
     * @return
     */
    @GetMapping(value = "/getCartMessage")
    public Map<String, Object> getCartMessage();

    /**
     * 清空购物车
     */
    @GetMapping(value = "/removeAll")
    public void removeAll();
}
