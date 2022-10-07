package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 黄梁峰
 * <p>
 * 查询商品详情
 */
public interface ItemService {
    /**
     * 查询sku基本信息
     *
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfo(Long skuId);

    /**
     * 查询sku图片列表
     *
     * @param skuId
     * @return
     */
    public List<SkuImage> getSkuImage(Long skuId);

    /**
     * 查询sku价格属性
     *
     * @param skuId
     * @return
     */
    public BigDecimal getSkuPrice(Long skuId);

    /**
     * 获取一二三级分类
     *
     * @param category3Id
     * @return
     */
    public BaseCategoryView getCategory(Long category3Id);

    /**
     * 根据spuId和skuId获取销售属性并选定默认属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSaleAttrListBySkuIdAndSpuId(Long skuId, Long spuId);

    /**
     * 获取跳转页面键值对
     *
     * @param skuId
     * @return
     */
    public Map getSkuIdAndValues(Long skuId);

    /**
     * 获取品牌属性
     *
     * @param id
     * @return
     */
    public BaseTrademark getBaseTrademark(Long id);

    /**
     * 查询指定sku的平台属性信息
     *
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId);

    /**
     * 库存管理
     */
    public void decountStock(Map<String, Object> skuParam);

    /**
     * 回滚库存
     *
     * @param skuParam
     */
    public void rollBackStock(Map<String, Object> skuParam);

}
