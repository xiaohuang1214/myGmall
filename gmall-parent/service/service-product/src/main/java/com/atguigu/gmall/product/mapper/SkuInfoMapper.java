package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author 黄梁峰
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 库存管理,防止并发异常
     *
     * @param skuId
     * @param skuNum
     */
    @Update("update sku_info set stock=stock-#{skuNum} where id=#{skuId} and stock>=#{skuNum}")
    public int decountStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 回滚库存
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Update("update sku_info set stock=stock+#{skuNum} where id=#{skuId}")
    public int rollBackStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
