package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 黄梁峰
 * 订单管理mapper接口
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
