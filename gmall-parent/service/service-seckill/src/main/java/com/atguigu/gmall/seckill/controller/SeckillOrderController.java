package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * 秒杀订单管理控制层
 */
@RestController
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {
    @Resource
    private SeckillOrderService seckillOrderService;

    /**
     * 添加秒杀订单
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @GetMapping(value = "/addSeckillOrder")
    public Result addSeckillOrder(String time, String goodsId, Integer num) {
        return Result.ok(seckillOrderService.addSeckillOrder(time, goodsId, num));
    }

    /**
     * 查询用户排队状态
     *
     * @return
     */
    @GetMapping("/getUserStatus")
    public Result getUserStatus() {
        return Result.ok(seckillOrderService.getUserStatus());
    }


}
