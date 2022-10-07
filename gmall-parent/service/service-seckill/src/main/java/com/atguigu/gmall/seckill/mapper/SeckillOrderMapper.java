package com.atguigu.gmall.seckill.mapper;

import com.atguigu.gmall.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 黄梁峰
 * 秒杀订单的mapper映射
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
}
