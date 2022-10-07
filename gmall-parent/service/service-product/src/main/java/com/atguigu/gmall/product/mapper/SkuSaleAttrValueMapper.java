package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 黄梁峰
 * <p>
 * Sku销售属性值
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 获取跳转页面键值对
     *
     * @param skuId
     * @return
     */
    public List<Map> selectSaleAttrKeyValueBySpuId(@Param(value = "skuId") Long skuId);
}
