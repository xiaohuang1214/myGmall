package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 黄梁峰
 */
@FeignClient(name = "service-product", path = "/api/item", contextId = "itemFeign")
public interface ItemFeign {

    /**
     * 获取sku基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId);

    /**
     * 获取sku图片列表
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuImage/{skuId}")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId);

    /**
     * 获取sku价格
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 获取一二三级分类
     *
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id);

    /**
     * 根据spuId和skuId获取销售属性并选定默认属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId);

    /**
     * 获取跳转页面键值对
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuIdAndValues/{skuId}")
    public Map getSkuIdAndValues(@PathVariable(value = "skuId") Long skuId);

    /**
     * 获取品牌
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id")Long id);

    /**
     * 查询指定sku的平台属性信息
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId")Long skuId);

    /**
     * 库存管理
     *
     * @param skuParam
     */
    @GetMapping(value = "/decountStock")
    public void decountStock(@RequestParam Map<String, Object> skuParam);

    /**
     * 回滚库存
     *
     * @param skuParam
     */
    @GetMapping("/rollBackStock")
    public void rollBackStock(@RequestParam Map<String, Object> skuParam);
}

