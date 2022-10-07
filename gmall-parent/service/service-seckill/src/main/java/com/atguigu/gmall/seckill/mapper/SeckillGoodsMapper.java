package com.atguigu.gmall.seckill.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author 黄梁峰
 * 秒杀商品的mapper接口
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {


    /**
     * 跟新库存
     *
     * @param stock
     * @param goodsId
     */
    @Update("update seckill_goods set stock_count=#{stock},num=#{stock} where id = #{goodsId}")
    public int updateSeckillGoodsStock(@Param("stock") Integer stock,@Param("goodsId") Long goodsId);
}
