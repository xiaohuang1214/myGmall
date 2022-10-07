package com.atguigu.gmall.cart.contorller;

import com.atguigu.gmall.cart.service.CartInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 黄梁峰
 * 订单相关购物车信息管理
 */
@RestController
@RequestMapping(value = "/api/cart")
public class CartOrderController {
    @Resource
    private CartInfoService cartInfoService;

    /**
     * 获取购物车信息和用户地址
     * @return
     */
    @GetMapping(value = "/getCartMessage")
    public Map<String, Object> getCartMessage(){
        return cartInfoService.getCartMessage();
    }

    /**
     * 清空购物车
     */
    @GetMapping(value = "/removeAll")
    public void removeAll(){
        cartInfoService.removeAll();
    }

}
