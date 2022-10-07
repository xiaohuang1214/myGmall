package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author 黄梁峰
 */
public interface ManageService {

    /**
     * 查询所有一级分类
     *
     * @return
     */
    public List<BaseCategory1> getBaseCategory1();

    /**
     * 查询二级分类
     *
     * @param c1Id
     * @return
     */
    public List<BaseCategory2> getBaseCategory2(Long c1Id);

    /**
     * 查询三级分类
     *
     * @param c2Id
     * @return
     */
    public List<BaseCategory3> getBaseCategory3(Long c2Id);

    /**
     * 保存功能
     *
     * @param baseAttrInfo
     */
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 查询所有平台属性和属性值
     *
     * @param cateagory1Id
     * @param cateagory2Id
     * @param cateagory3Id
     * @return
     */
    public List<BaseAttrInfo> selectAllByAttrValueList(Long cateagory1Id,
                                                       Long cateagory2Id,
                                                       Long cateagory3Id);

    /**
     * 点击修改查询属性值
     *
     * @param attrId
     * @return
     */
    public List<BaseAttrValue> getAttrValueList(Long attrId);


    /**
     * 获取销售属性
     *
     * @return
     */
    public List<BaseSaleAttr> baseSaleAttrList();


    /**
     * 分页查询所有品牌
     *
     * @param current
     * @param size
     * @return
     */
    public IPage getBaseTrademark(Long current, Long size);

    /**
     * 新增品牌
     *
     * @param baseTrademark
     */
    public void save(BaseTrademark baseTrademark);

    /**
     * 删除品牌
     *
     * @param id
     */
    public void remove(Long id);

    /**
     * 根据Id查询品牌
     *
     * @param id
     * @return
     */
    public BaseTrademark get(Long id);

    /**
     * 修改品牌
     *
     * @param baseTrademark
     */
    public void update(BaseTrademark baseTrademark);

    /**
     * 获取品牌属性
     *
     * @return
     */
    public List<BaseTrademark> getTrademarkList();


    /**
     * 商品属性SPU分页
     *
     * @param current
     * @param size
     * @param c3Id
     * @return
     */
    public IPage page(Long current,
                      Long size,
                      Long c3Id);


    /**
     * 新增商品属性
     *
     * @param spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);


    /**
     * 根据spuId获取销售属性
     *
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    /**
     * 根据spuId获取图片列表
     *
     * @param spuId
     * @return
     */
    public List<SpuImage> spuImageList(Long spuId);

    /**
     * 商品属性SKU分页
     *
     * @param current
     * @param size
     * @return
     */
    public IPage list(Long current, Long size);

    /**
     * 保存Sku属性
     *
     * @param skuInfo
     */
    public void saveSkuInfo(SkuInfo skuInfo) throws Exception;

    /**
     * 上架
     *
     * @param skuId
     */
    public void onSale(Long skuId);

    /**
     * 下架
     *
     * @param skuId
     */
    public void cancelSale(Long skuId);
}
