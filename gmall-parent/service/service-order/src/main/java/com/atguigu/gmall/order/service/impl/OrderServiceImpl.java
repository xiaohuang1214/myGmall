package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.payment.feign.PaymentFeign;
import com.atguigu.gmall.payment.feign.ZfbPaymentFeign;
import com.atguigu.gmall.product.feign.ItemFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄梁峰
 * 订单管理接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private CartFeign cartFeign;
    @Resource
    private ItemFeign itemFeign;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private PaymentFeign paymentFeign;
    @Resource
    private ZfbPaymentFeign zfbPaymentFeign;


    /**
     * 生成订单
     *
     * @param orderInfo
     */
    @Override
    public void getOrderInfo(OrderInfo orderInfo) {
        //参数校验
        if (orderInfo == null) {
            throw new RuntimeException("生成订单失败!!");
        }
        //防止多端同时下单
        Long num = redisTemplate.opsForValue().increment("order_message_num" + OrderThreadLocalUtil.getUsername(), 1);
        //设置过期时间,防止死锁
        redisTemplate.expire("order_message_num" + OrderThreadLocalUtil.getUsername(), 10, TimeUnit.SECONDS);

        if (num > 1) {
            throw new RuntimeException("重复下单,生成订单失败");
        }
        try {
            //获取购物车信息
            Map<String, Object> cartMap = cartFeign.getCartMessage();
            if (cartMap == null) {
                throw new RuntimeException("购物车没有数据生成订单失败");
            }
            //生成订单信息
            String price = cartMap.get("totalPrice").toString();
            BigDecimal totalPrice = new BigDecimal(price);
            orderInfo.setUserId(OrderThreadLocalUtil.getUsername());
            orderInfo.setTotalAmount(totalPrice);
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setCreateTime(new Date());
            orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1800000));
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            int insert = orderInfoMapper.insert(orderInfo);
            if (insert <= 0) {
                throw new RuntimeException("生成订单失败");
            }
            //保存订单详情信息
            List cartInfoList = (List) cartMap.get("cartInfoList");
            Map<String, Object> skuParam = savaOrderDetail(orderInfo.getId(), cartInfoList);
            //减库存
            itemFeign.decountStock(skuParam);
            //清空购物车
//            cartFeign.removeAll();
            //超时取消订单
            rabbitTemplate.convertAndSend("order_nomal_exchange",
                    "order.dead",
                    orderInfo.getId() + "",
                    (message -> {
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置超时时间
                        messageProperties.setExpiration(1800000 + "");
                        return message;
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("生成订单失败");
        } finally {
            //删除redis
            redisTemplate.delete("order_message_num" + OrderThreadLocalUtil.getUsername());
        }
    }


    /**
     * 取消订单
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
        //非空校验
        if (orderId == null) {
            throw new RuntimeException("没有这个商品!");
        }
        //获取用户名
        String username = OrderThreadLocalUtil.getUsername();

//        //实际开发需判断用户使用的支付方式--
//
//        //判断订单是否已经支付---支付宝--0
//        String payResult = zfbPaymentFeign.getPayResult(orderId + "");
//        Map<String, String> resultMapToZfb = JSONObject.parseObject(payResult, Map.class);
//        String tradeNo = resultMapToZfb.get("trade_no");
//        String tradeStatus = resultMapToZfb.get("trade_status");
//        //判断订单是否已经支付
//        if ("WAIT_BUYER_PAY".equals(tradeStatus)){
//            zfbPaymentFeign.closeOrder(tradeNo);
//        }

        //判断订单是否已经支付---微信--1
        String result = paymentFeign.closeOrder(orderId + "");
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        String resultCode = resultMap.get("result_code");
        //判断订单是否支付
        if ("FAIL".equals(resultCode)) {
            return;
        }

        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment())
                .eq(OrderInfo::getId, orderId));
        if (orderInfo == null || orderInfo.getId() == null) {
            throw new RuntimeException("取消订单失败");
        }
        //用username作为标识来判断是否为用户手动删除
        if (StringUtils.isEmpty(username)) {
            //超时取消
            orderInfo.setOrderStatus(OrderStatus.TIMEOUT.getComment());
            orderInfo.setProcessStatus(ProcessStatus.TIMEOUT.getComment());
        } else {
            //手动取消
            orderInfo.setOrderStatus(OrderStatus.CANCEL.getComment());
            orderInfo.setProcessStatus(ProcessStatus.CANCEL.getComment());
        }

        //修改订单状态
        int i = orderInfoMapper.updateById(orderInfo);
        if (i <= 0) {
            throw new RuntimeException("取消订单失败");
        }
        //库存回滚
        rollbackStock(orderId);

    }


    /**
     * 回滚库存
     *
     * @param orderId
     */
    private void rollbackStock(Long orderId) {
        Map<String, Object> skuParam = new ConcurrentHashMap<>();
        //查询订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId));

        orderDetails.stream().forEach(orderDetail -> {
            skuParam.put(orderDetail.getSkuId() + "", orderDetail.getSkuNum());
        });
        //远程调用回滚
        itemFeign.rollBackStock(skuParam);
    }

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 保存订单详情信息并返回库存系统参数
     *
     * @param orderId
     * @param cartInfoList
     * @return
     */
    private Map<String, Object> savaOrderDetail(Long orderId, List cartInfoList) {
        //初始化库存系统参数
        Map<String, Object> skuParam = new ConcurrentHashMap<>();
        cartInfoList.stream().forEach(o -> {
            //序列化
            String s = JSONObject.toJSONString(o);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);
            if (cartInfo == null) {
                throw new RuntimeException("购物车没有数据");
            }
            //初始化订单明细对象
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            int insert = orderDetailMapper.insert(orderDetail);
            if (insert <= 0) {
                throw new RuntimeException("新增订单详情失败");
            }
            skuParam.put(cartInfo.getSkuId().toString(), cartInfo.getSkuNum());
        });
        return skuParam;
    }

    /**
     * 支付成功修改订单状态
     *
     * @param result
     */
    @Override
    public void updateOrderStatus(String result) {
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        //获取订单Id
        String orderId = resultMap.get("out_trade_no");
        //根据订单Id查询订单信息
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, Long.parseLong(orderId))
                .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment()));
        if (orderInfo == null || orderInfo.getId() == null) {
            return;
        }
        //修改订单状态
        orderInfo.setOrderStatus(result);
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        //判断交易类型
        if ("1".equals(resultMap.get("payway"))) {
            //微信支付
            orderInfo.setPaymentWay("微信支付");
            //设置交易流水号
            orderInfo.setOutTradeNo(resultMap.get("transaction_id"));
        } else {
            //支付宝
            orderInfo.setPaymentWay("支付宝支付");
            //设置交易流水号
            orderInfo.setOutTradeNo(resultMap.get("trade_no"));
        }
        int i = orderInfoMapper.updateById(orderInfo);
        if (i <= 0) {
            throw new RuntimeException("修改订单支付结果失败");
        }
        //实战----仓库管理系统----发送发货的消息--生产者---TODO

    }
}
