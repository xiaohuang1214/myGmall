package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 黄梁峰
 * 订单明细的mapper接口
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
