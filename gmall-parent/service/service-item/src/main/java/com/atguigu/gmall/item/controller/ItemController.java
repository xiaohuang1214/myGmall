package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 黄梁峰
 */
@RestController
@RequestMapping(value = "/api/item")
public class ItemController {
    @Resource
    private ItemService itemService;

    /**
     * 查询商品详情信息
     *
     * @param skuId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public Map getItemInfo(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getItemInfo(skuId);
    }
}
