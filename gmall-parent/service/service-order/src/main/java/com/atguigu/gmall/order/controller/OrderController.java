package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄梁峰
 * 订单管理相关控制层
 */
@RestController
@RequestMapping(value = "/api/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 生成订单
     *
     * @param orderInfo
     * @return
     */
    @PostMapping(value = "/getOrderInfo")
    public Result getOrderInfo(@RequestBody OrderInfo orderInfo) {
        orderService.getOrderInfo(orderInfo);
        return Result.ok();
    }


    /**
     * 取消订单
     *
     * @param orderId
     * @return
     */
    @GetMapping("/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable(value = "orderId") Long orderId) {
        Long increment = redisTemplate.opsForValue().increment(orderId + "", 1);
        //设置过期时间
        redisTemplate.expire(orderId + "", 5, TimeUnit.SECONDS);
        if (increment > 1) {
            return Result.ok();
        }
        try {
            orderService.cancelOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisTemplate.delete(orderId + "");
        }
        return Result.ok();
    }
}
