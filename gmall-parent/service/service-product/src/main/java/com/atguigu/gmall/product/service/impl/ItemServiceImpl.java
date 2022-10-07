package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.cache.Java0217Cache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄梁峰
 * <p>
 * 查询商品详情
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private SkuInfoMapper skuInfoMapper;

    /**
     * 查询sku基本信息
     *
     * @param skuId
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getSkuInfo")
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    @Resource
    private SkuImageMapper skuImageMapper;

    /**
     * 查询sku图片列表
     *
     * @param skuId
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getSkuImage")
    public List<SkuImage> getSkuImage(Long skuId) {
        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 查询sku价格属性
     *
     * @param skuId
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getSkuPrice")
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 获取一二三级分类
     *
     * @param category3Id
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getCategory")
    public BaseCategoryView getCategory(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;

    /**
     * 根据spuId和skuId获取销售属性并选定默认属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getSaleAttrListBySkuIdAndSpuId")
    public List<SpuSaleAttr> getSaleAttrListBySkuIdAndSpuId(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSaleAttrListBySkuIdAndSpuId(skuId, spuId);
    }

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    /**
     * 获取跳转页面键值对
     *
     * @param skuId
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getSkuIdAndValues")
    public Map getSkuIdAndValues(Long skuId) {
        List<Map> maps = skuSaleAttrValueMapper.selectSaleAttrKeyValueBySpuId(skuId);
        Map keyAndValue = new ConcurrentHashMap<>();
        maps.stream().forEach(value -> {
            Object sku_id = value.get("sku_id");
            Object address = value.get("address");
            keyAndValue.put(address.toString(), sku_id);
        });
        return keyAndValue;
    }

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    /**
     * 获取品牌属性
     *
     * @param id
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getBaseTrademark")
    public BaseTrademark getBaseTrademark(Long id) {
        return baseTrademarkMapper.selectById(id);
    }

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 查询指定sku的平台属性信息
     *
     * @param skuId
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getBaseAttrInfo")
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }


    /**
     * 库存管理
     */
    @Override
    public void decountStock(Map<String, Object> skuParam) {
        skuParam.entrySet().stream().forEach(param -> {
            String skuId = param.getKey();
            Integer skuNum = Integer.parseInt(param.getValue().toString());
            int i = skuInfoMapper.decountStock(Long.parseLong(skuId), skuNum);
            if (i <= 0) {
                throw new RuntimeException("库存修改失败");
            }
        });
    }

    /**
     * 回滚库存
     *
     * @param skuParam
     */
    @Override
    public void rollBackStock(Map<String, Object> skuParam) {
        skuParam.entrySet().stream().forEach(entry -> {
            //获取skuId
            String skuId = entry.getKey();
            //获取回滚的库存数
            Object skuNum = entry.getValue();
            int i = skuInfoMapper.rollBackStock(Long.parseLong(skuId), Integer.parseInt(skuNum.toString()));
            if (i < 0) {
                throw new RuntimeException("回滚库存失败");
            }
        });

    }
}
