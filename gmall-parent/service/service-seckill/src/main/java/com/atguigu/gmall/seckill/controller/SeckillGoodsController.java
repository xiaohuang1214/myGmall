package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.util.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * 秒杀商品控制层
 */
@RestController
@RequestMapping(value = "/seckill/goods")
public class SeckillGoodsController {
    @Resource
    private SeckillGoodsService seckillGoodsService;

    /**
     * 获取时间段
     *
     * @return
     */
    @GetMapping("/getDateMenus")
    public Result getDateMenus() {
        return Result.ok(DateUtil.getDateMenus());
    }

    /**
     * 获取商品列表
     *
     * @param time
     * @return
     */
    @GetMapping("/getSeckillGoodsList")
    public Result getSeckillGoodsList(String time) {
        return Result.ok(seckillGoodsService.getSeckillGoodsList(time));
    }

    /**
     * 获取商品
     *
     * @param time
     * @return
     */
    @GetMapping("/getSeckillGoods")
    public Result getSeckillGoodsList(String time, String goodsId) {
        return Result.ok(seckillGoodsService.getSeckillGoods(time, goodsId));
    }


}
