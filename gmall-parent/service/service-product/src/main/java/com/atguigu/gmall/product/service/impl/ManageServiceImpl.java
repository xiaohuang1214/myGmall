package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author 黄梁峰
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    /**
     * 查询所有一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getBaseCategory1() {
        return baseCategory1Mapper.selectList(null);
    }


    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;

    /**
     * 查询二级分类
     *
     * @param c1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getBaseCategory2(Long c1Id) {
        return baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>()
                .eq(BaseCategory2::getCategory1Id, c1Id));
    }


    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;

    /**
     * 查询三级分类
     *
     * @param c2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getBaseCategory3(Long c2Id) {
        return baseCategory3Mapper.selectList(new LambdaQueryWrapper<BaseCategory3>()
                .eq(BaseCategory3::getCategory2Id, c2Id));
    }


    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 保存功能
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断参数是否为空
        if (baseAttrInfo == null ||
                StringUtils.isEmpty(baseAttrInfo.getAttrName())) {
            throw new RuntimeException("参数错误!!!");
        }

        //修改
        if (baseAttrInfo.getId() != null) {
            //修改
            int updateId = baseAttrInfoMapper.updateById(baseAttrInfo);

            if (updateId < 0) {
                throw new RuntimeException("修改失败!!!");
            }
            //删除原有平台属性值
            int deleteId = baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId,
                            baseAttrInfo.getId()));
            if (deleteId < 0) {
                throw new RuntimeException("修改失败!!!");
            }
        }
        //新增
        else {
            //保存平台属性
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增保存失败!!!");
            }
        }
        //平台属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //保存平台属性值
        attrValueList.stream().forEach(baseAttrValue -> {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        });

    }

    /**
     * 查询所有平台属性和属性值
     *
     * @param cateagory1Id
     * @param cateagory2Id
     * @param cateagory3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> selectAllByAttrValueList(Long cateagory1Id,
                                                       Long cateagory2Id,
                                                       Long cateagory3Id) {
        return baseAttrInfoMapper.selectAllByAttrValueList(cateagory1Id, cateagory2Id, cateagory3Id);
    }

    /**
     * 点击修改查询属性值
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        return baseAttrValueMapper.selectList(new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId,
                        attrId));
    }


    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    /**
     * 分页查询所有品牌
     *
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage getBaseTrademark(Long current,
                                  Long size) {
        return baseTrademarkMapper.selectPage(new Page<>(current, size), null);
    }

    /**
     * 新增品牌
     *
     * @param baseTrademark
     */
    @Override
    public void save(BaseTrademark baseTrademark) {
        //判断参数是否为空
        if (baseTrademark == null ||
                StringUtils.isEmpty(baseTrademark.getTmName())) {
            throw new RuntimeException("参数错误!!!");
        }
        //新增
        int insert = baseTrademarkMapper.insert(baseTrademark);
        if (insert <= 0) {
            throw new RuntimeException("新增失败!!!");
        }


    }

    /**
     * 删除品牌
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        int num = baseTrademarkMapper.deleteById(id);
        if (num < 0) {
            throw new RuntimeException("删除失败!!!");
        }
    }

    /**
     * 根据Id查询品牌
     *
     * @param id
     * @return
     */
    @Override
    public BaseTrademark get(Long id) {
        return baseTrademarkMapper.selectById(id);
    }

    /**
     * 修改品牌
     *
     * @param baseTrademark
     */
    @Override
    public void update(BaseTrademark baseTrademark) {
        //判断参数是否为空
        if (baseTrademark == null ||
                StringUtils.isEmpty(baseTrademark.getTmName())) {
            throw new RuntimeException("参数错误!!!");
        }
        //修改
        int num = baseTrademarkMapper.updateById(baseTrademark);
        if (num < 0) {
            throw new RuntimeException("修改失败!!!");
        }
    }

    /**
     * 获取品牌属性
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }


    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;

    /**
     * 获取销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }


    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    /**
     * 商品属性SPU分页
     *
     * @param current
     * @param size
     * @param c3Id
     * @return
     */
    @Override
    public IPage page(Long current,
                      Long size,
                      Long c3Id) {
        return spuInfoMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, c3Id));
    }

    /**
     * 新增商品属性
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        CompletableFuture<Long> spuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //判断参数是否为空
            if (spuInfo == null) {
                throw new RuntimeException("参数错误!!!");
            }
            //保存商品属性
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
            Long spuId = spuInfo.getId();
            return spuId;
        });
        CompletableFuture<Void> spuImageCompletableFuture = spuInfoCompletableFuture.thenAcceptAsync(spuId -> {
            //保存图片信息
            saveSpuImage(spuInfo.getSpuImageList(), spuId);
        });
        CompletableFuture<Void> spuSaleAttrCompletableFuture = spuInfoCompletableFuture.thenAcceptAsync(spuId -> {
            //保存Spu销售属性和Spu销售属性值
            saveSpuSaleAttr(spuInfo.getSpuSaleAttrList(), spuId);
        });
        CompletableFuture.allOf(spuInfoCompletableFuture,
                spuImageCompletableFuture,
                spuSaleAttrCompletableFuture).join();
    }

    /**
     * 根据spuId获取销售属性
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        if (spuId == 0) {
            throw new RuntimeException("错误!!!");
        }
        return spuSaleAttrMapper.spuSaleAttrList(spuId);
    }

    /**
     * 根据spuId获取图片列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        if (spuId == 0) {
            throw new RuntimeException("错误!!!");
        }
        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
    }


    @Resource
    private SkuInfoMapper skuInfoMapper;

    /**
     * 商品属性SKU分页
     *
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage list(Long current, Long size) {
        return skuInfoMapper.selectPage(new Page<>(current, size), null);
    }


    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    /**
     * 保存Sku属性
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) throws Exception {
        if (skuInfo == null) {
            throw new RuntimeException("参数错误!!!");
        }
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            //新增sku属性
            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
            //获取skuId
            Long skuId = skuInfo.getId();
            return skuId;
        });
        CompletableFuture<Void> future1 = future.thenAcceptAsync(skuId -> {
            //新增sku图片
            saveSkuImage(skuId, skuInfo.getSkuImageList());
        });
        CompletableFuture<Void> future2 = future.thenAcceptAsync(skuId -> {
            //新增Sku平台属性值
            saveSkuAttrValue(skuId, skuInfo.getSkuAttrValueList());
        });
        CompletableFuture<Void> future3 = future.thenAcceptAsync(skuId -> {
            //新增Sku销售属性值
            saveSkuSaleAttrValue(skuId, skuInfo.getSpuId(), skuInfo.getSkuSaleAttrValueList());
        });

        CompletableFuture.allOf(future, future1, future2, future3).join();
    }


    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        if (skuId == null) {
            throw new RuntimeException("错误!!!");
        }
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("错误!!!");
        }
        skuInfo.setIsSale(ProductConst.On_Sale);
        int i = skuInfoMapper.updateById(skuInfo);
        //计入上架错误日志
        skuLog();
        rabbitTemplate.convertAndSend("sku_exchange", "sku.upper", skuInfo.getId() + "");

        if (i < 0) {
            throw new RuntimeException("上架失败!!!");
        }
    }

    /**
     * 下架
     *
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        if (skuId == null) {
            throw new RuntimeException("错误!!!");
        }
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("错误!!!");
        }
        skuInfo.setIsSale(ProductConst.Cancel_Sale);
        int i = skuInfoMapper.updateById(skuInfo);
        //计入下架错误日志
        skuLog();
        rabbitTemplate.convertAndSend("sku_exchange", "sku.down", skuInfo.getId() + "");
        if (i < 0) {
            throw new RuntimeException("下架失败!!!");
        }
    }


    /**
     * 保存Spu销售属性
     *
     * @param spuSaleAttrList
     * @param spuId
     */
    private void saveSpuSaleAttr(List<SpuSaleAttr> spuSaleAttrList,
                                 Long spuId) {
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            spuSaleAttr.setSpuId(spuId);
            int insert1 = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert1 <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
            //保存spu销售属性值
            saveSpuSaleAttrValue(spuSaleAttr.getSpuSaleAttrValueList(), spuId, spuSaleAttr.getSaleAttrName());
        });
    }

    /**
     * 保存spu销售属性值
     *
     * @param spuSaleAttrValueList
     * @param spuId
     * @param saleAttrName
     */
    private void saveSpuSaleAttrValue(List<SpuSaleAttrValue> spuSaleAttrValueList,
                                      Long spuId,
                                      String saleAttrName) {
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            int insert2 = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert2 <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
        });
    }

    /**
     * 保存spu图片信息
     *
     * @param spuImageList
     * @param spuId
     */
    private void saveSpuImage(List<SpuImage> spuImageList,
                              Long spuId) {
        spuImageList.stream().forEach(spuImage -> {
            spuImage.setSpuId(spuId);
            int insert1 = spuImageMapper.insert(spuImage);
            if (insert1 <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
        });
    }


    /**
     * 新增Sku销售属性值
     *
     * @param skuId
     * @param spuId
     * @param skuSaleAttrValueList
     */
    private void saveSkuSaleAttrValue(Long skuId,
                                      Long spuId,
                                      List<SkuSaleAttrValue> skuSaleAttrValueList) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(spuId);
            int insert2 = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if (insert2 <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
        });
    }

    /**
     * 新增Sku平台属性值
     *
     * @param skuId
     * @param skuAttrValueList
     */
    private void saveSkuAttrValue(Long skuId,
                                  List<SkuAttrValue> skuAttrValueList) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            skuAttrValue.setSkuId(skuId);
            int insert1 = skuAttrValueMapper.insert(skuAttrValue);
            if (insert1 <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
        });
    }

    /**
     * 新增sku图片
     *
     * @param skuId
     * @param skuImageList
     */
    private void saveSkuImage(Long skuId,
                              List<SkuImage> skuImageList) {
        skuImageList.stream().forEach(skuImage -> {
            skuImage.setSkuId(skuId);
            int insert1 = skuImageMapper.insert(skuImage);
            if (insert1 <= 0) {
                throw new RuntimeException("新增失败!!!");
            }
        });
    }

    /**
     * 商品上下架失败日志记录
     */
    private void skuLog() {
        rabbitTemplate.setReturnCallback((a, b, c, d, e) -> {
            log.error("商品上下架消息发送失败,消息没有抵达队列,商品的id为:" + new String(a.getBody()));
            log.error("商品上下架消息发送失败,状态码为:" + b);
            log.error("商品上下架消息发送失败,错误的内容为:" + c);
            log.error("商品上下架消息发送失败,消息发送时指定的交换机为:" + d);
            log.error("商品上下架消息发送失败,消息发送时指定的routing key为:" + e);
        });
    }
}
