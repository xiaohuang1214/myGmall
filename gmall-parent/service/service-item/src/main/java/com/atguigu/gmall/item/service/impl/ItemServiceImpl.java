package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ItemFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 黄梁峰
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private ItemFeign itemFeign;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private ListFeign listFeign;

    /**
     * 查询商品详情信息
     *
     * @param skuId
     * @return
     * @throws Exception
     */
    @Override
    public Map getItemInfo(@NotNull(message = "商品id不能为空") Long skuId){

        Map map = new HashMap<>();
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //查询sku基本信息
            SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
            if (skuInfo == null || skuInfo.getId() == null) {
                return null;
            }
            map.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);
        CompletableFuture<Void> categoryCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            //查询分类属性信息
            BaseCategoryView category = itemFeign.getCategory(skuInfo.getCategory3Id());
            map.put("category", category);
        }, threadPoolExecutor);
        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            //查询sku销售属性
            List<SpuSaleAttr> spuSaleAttr = itemFeign.getSpuSaleAttr(skuInfo.getId(), skuInfo.getSpuId());
            map.put("spuSaleAttr", spuSaleAttr);
        }, threadPoolExecutor);
        CompletableFuture<Void> skuImageCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            //查询sku图片信息
            List<SkuImage> skuImage = itemFeign.getSkuImage(skuInfo.getId());
            map.put("skuImage", skuImage);
        }, threadPoolExecutor);
        CompletableFuture<Void> priceCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            //查询sku价格信息
            BigDecimal price = itemFeign.getSkuPrice(skuInfo.getId());
            map.put("price", price);
        }, threadPoolExecutor);
        CompletableFuture<Void> skuIdAndValuesCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            //获取跳转页面键值对
            Map skuIdAndValues = itemFeign.getSkuIdAndValues(skuInfo.getId());
            map.put("skuIdAndValues", skuIdAndValues);
        }, threadPoolExecutor);
        CompletableFuture.allOf(skuInfoCompletableFuture,
                categoryCompletableFuture,
                spuSaleAttrCompletableFuture,
                skuImageCompletableFuture,
                priceCompletableFuture,
                skuIdAndValuesCompletableFuture).join();

        //异步更新热点数据
        CompletableFuture.runAsync(() -> {
            listFeign.getHostScore(skuId);
        }, threadPoolExecutor);

        //返回结果
        return map;

    }
}
