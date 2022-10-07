package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 黄梁峰
 * <p>
 * 销售属性
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * 根据spuId获取销售属性
     *
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> spuSaleAttrList(@Param(value = "spuId") Long spuId);

    /**
     * 根据spuId和skuId获取销售属性并选定默认属性
     * @param skuId
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSaleAttrListBySkuIdAndSpuId(@Param(value = "skuId")Long skuId,
                                                            @Param(value = "spuId")Long spuId);
}
