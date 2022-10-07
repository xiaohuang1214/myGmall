package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ItemFeign;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 黄梁峰
 * <p>
 * es中商品相关的接口的实现类
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private static final Integer NUMBER_COUNT = 10;
    @Resource
    private GoodsDao goodsDao;
    @Resource
    private ItemFeign itemFeign;

    /**
     * 将上架的商品写入es
     *
     * @param skuId
     */
    @Override
    public void addGoodsToEs(Long skuId) {
        //参数校验
        if (skuId == null) {
            throw new RuntimeException("错误信息");
        }
        //设置goods属性
        //参数校验
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("错误信息");
        }
        Goods goods = new Goods();
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuDesc());
        BigDecimal price = itemFeign.getSkuPrice(skuInfo.getId());
        goods.setPrice(price.doubleValue());
        goods.setCreateTime(new Date());
        BaseTrademark trademark = itemFeign.getBaseTrademark(skuInfo.getTmId());
        goods.setTmId(trademark.getId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());
        BaseCategoryView category = itemFeign.getCategory(skuInfo.getCategory3Id());
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        List<BaseAttrInfo> baseAttrInfoList = itemFeign.getBaseAttrInfo(skuInfo.getId());
        List<SearchAttr> attrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(attrs);
        //保存
        goodsDao.save(goods);
    }


    /**
     * 将商品从es中删除
     *
     * @param goodsId
     */
    @Override
    public void deleteGoodsToEs(Long goodsId) {
        goodsDao.deleteById(goodsId);
    }

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 热点数据
     *
     * @param goodsId
     */
    @Override
    public void getHotScore(Long goodsId) {
        Double hotScore = redisTemplate.opsForZSet().incrementScore("hotScore", goodsId, 1);
        if (hotScore == null){
            return;
        }
        Optional<Goods> optional = goodsDao.findById(goodsId);
        if (optional.isPresent()) {
            if (hotScore % NUMBER_COUNT == 0) {
                Goods goods = optional.get();
                goods.setHotScore(hotScore.longValue());
                goodsDao.save(goods);
            }
        }
    }
//    @Override
//    public void getHotScore(Long skuId) {
//        Double hotScore = redisTemplate.opsForZSet().incrementScore("hotScore", "goods" + skuId, 1);
//        if (hotScore % NUMBER_COUNT == 0) {
//            if (skuId == null) {
//                return;
//            }
//            Goods goods = getGoods(skuId);
//            if (goods == null || goods.getId() == null) {
//                return;
//            }
//            goods.setHotScore(hotScore.longValue());
//            goodsDao.save(goods);
//        }
//    }
}
