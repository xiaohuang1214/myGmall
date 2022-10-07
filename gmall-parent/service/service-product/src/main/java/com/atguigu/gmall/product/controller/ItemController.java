package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.Java0217Cache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 黄梁峰
 * <p>
 * 查询商品详情
 */
@RestController
@RequestMapping(value = "/api/item")
public class ItemController {
    @Resource
    private ItemService itemService;

    /**
     * 获取sku基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 获取sku图片列表
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuImage/{skuId}")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getSkuImage(skuId);
    }

    /**
     * 获取sku价格
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getSkuPrice(skuId);
    }

    /**
     * 获取一二三级分类
     *
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id) {
        return itemService.getCategory(category3Id);
    }

    /**
     * 根据spuId和skuId获取销售属性并选定默认属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId) {
        return itemService.getSaleAttrListBySkuIdAndSpuId(skuId, spuId);
    }

    /**
     * 获取跳转页面键值对
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuIdAndValues/{skuId}")
    public Map getSkuIdAndValues(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getSkuIdAndValues(skuId);
    }

    /**
     * 获取品牌
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id")Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * 查询指定sku的平台属性信息
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId")Long skuId){
        return itemService.getBaseAttrInfo(skuId);
    }

    /**
     * 库存管理
     *
     * @param skuParam
     */
    @GetMapping(value = "/decountStock")
    public void decountStock(@RequestParam Map<String, Object> skuParam){
        itemService.decountStock(skuParam);
    }

    /**
     * 回滚库存
     *
     * @param skuParam
     */
    @GetMapping("/rollBackStock")
    public void rollBackStock(@RequestParam Map<String, Object> skuParam){
        itemService.rollBackStock(skuParam);
    }
}
