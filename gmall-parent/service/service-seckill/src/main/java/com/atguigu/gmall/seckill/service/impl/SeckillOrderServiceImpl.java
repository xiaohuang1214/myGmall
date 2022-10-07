package com.atguigu.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.mapper.SeckillOrderMapper;
import com.atguigu.gmall.seckill.pojo.SeckillOrder;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.atguigu.gmall.seckill.util.SeckillThreadLocalUtil;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄梁峰
 * 秒杀订单相关接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 添加秒杀订单/伪添加:排队
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num) {
        UserRecode userRecode = new UserRecode();
        String username = "liuyingjun";
        //防止重复排队
        Long increment = redisTemplate.opsForValue().increment("user_order_status_" + username, 1);
        //设置过期时间
        redisTemplate.expire("user_order_status_" + username, 300, TimeUnit.SECONDS);
        if (increment > 1) {
            userRecode.setUsername(username);
            userRecode.setMsg("重复排队");
            return userRecode;
        }

        //排队记录要买 哪个时间段的 那个商品 买几个
        userRecode.setUsername(username);
        userRecode.setCreateTime(new Date());
        userRecode.setStatus(1);
        userRecode.setGoodsId(goodsId);
        userRecode.setTime(time);
        userRecode.setNum(num);
        userRecode.setMsg("排队中");
        //异步下单
        CompletableFuture.runAsync(() -> {
            //将排队状态写入redis
            redisTemplate.opsForValue().set("user_status_" + username, userRecode);
            //消息队列
            rabbitTemplate.convertAndSend("seckill_order_exchange", "seckill.order.add", JSONObject.toJSONString(userRecode));
        }, threadPoolExecutor).whenCompleteAsync((a, b) -> {
            if (b != null) {
                //秒杀失败
                userRecode.setStatus(3);
                userRecode.setMsg("秒杀失败");
                //更新redis排队状态
                redisTemplate.opsForValue().set("user_status_" + username, userRecode);
            }
        }, threadPoolExecutor);
        //结束
        return userRecode;
    }

    /**
     * 查询用户排队状态
     *
     * @return
     */
    @Override
    public UserRecode getUserStatus() {
        String username = "liuyingjun";
        return (UserRecode) redisTemplate.opsForValue().get("user_status_" + username);
    }


    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 秒杀真实下单
     *
     * @param userRecode
     */
    @Override
    public void realSeckillOrderAdd(UserRecode userRecode) throws Exception {
        String username = userRecode.getUsername();
        Date createTime = userRecode.getCreateTime();
        String goodsId = userRecode.getGoodsId();
        Integer num = userRecode.getNum();
        String time = userRecode.getTime();
        //判断商品是否存在
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        if (seckillGoods == null || seckillGoods.getId() == null) {
            throw new RuntimeException("商品不存在,秒杀失败");
        }
        //是否在活动时间以内
        //获取当前时间段
        Date date = DateUtil.getDateMenus().get(0);
        //格式转换
        String timeNow = DateUtil.data2str(date, DateUtil.PATTERN_YYYYMMDDHH);
        if (!time.equals(timeNow)) {
            throw new RuntimeException("不在当前时间段,秒杀失败");
        }
        //判断是否超过限购
        if (num <= 0 || num > seckillGoods.getSeckillLimit()) {
            throw new RuntimeException("商品数量超过限购,秒杀失败");
        }

        for (Integer i = 0; i < num; i++) {
            //扣除redis库存
            Object o = redisTemplate.opsForList().rightPop("Seckill_Goods_Stock_Queue_" + goodsId);
            //判断o是否为空
            if (o == null) {
                if (i > 0) {
                    //回滚redis库存
                    redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + goodsId, seckillGoodsStock(goodsId, i));
                }
                //修改状态
                throw new RuntimeException("商品库存不足,秒杀失败");
            }
        }

        //获取redis库存
        Long increment =
                redisTemplate.opsForHash().increment("SeckillStockCount" + time, goodsId, -num);

        //设置库存
        seckillGoods.setStockCount(increment.intValue());
        seckillGoods.setNum(increment.intValue());
        //更新redis库存数据
        redisTemplate.opsForHash().put(time, goodsId, seckillGoods);
        //下单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(UUID.randomUUID().toString().replace("-", ""));
        seckillOrder.setGoodsId(goodsId);
        seckillOrder.setNum(num);
        seckillOrder.setMoney(seckillGoods.getPrice().multiply(new BigDecimal(num)).toString());
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(createTime);
        seckillOrder.setStatus("0");

        //保存订单到数据库
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            seckillOrderMapper.insert(seckillOrder);
            return true;
        }, threadPoolExecutor).exceptionally(a -> {
            return false;
        });
        //保存信息订单到redis
        CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(() -> {
            redisTemplate.opsForHash().put(time + "_order", seckillOrder.getId(), seckillOrder);
            return true;
        }, threadPoolExecutor).exceptionally(a -> {
            return false;
        });
        //判断数据库和redis同时崩
        if (!future.get() && !future1.get()) {
            redisTemplate.opsForList().leftPush("Seckill_Goods_Stock_Queue_" + goodsId, goodsId);
            throw new RuntimeException("数据库和redis同时失败");
        }
        //秒杀下单成功
        userRecode.setStatus(2);
        userRecode.setMoney(seckillOrder.getMoney());
        userRecode.setMsg("秒杀成功,订单待支付");
        userRecode.setOrderId(seckillOrder.getId());
        //更新redis中用户状态
        redisTemplate.opsForValue().set("user_status_" + username, userRecode);

        //TODO--订单后续处理:1.取消 2.超时取消 3.支付
        rabbitTemplate.convertAndSend(
                "seckill_order_nomal_exchange",
                "seckill.order.dead",
                JSONObject.toJSONString(userRecode),
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setExpiration(3000 + "");
                    return message;
                });


        //TODO--支付微服务通用化改造


    }

    /**
     * 取消订单
     *
     * @param userRecode
     */
    @Override
    public void cancelSeckillOrder(UserRecode userRecode) {
        //非空校验
        if (userRecode == null) {
            throw new RuntimeException("没有这个商品!");
        }
        //获取用户名
        String username = SeckillThreadLocalUtil.getUsername();

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
//        String result = paymentFeign.closeOrder(orderId + "");
//        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
//        String resultCode = resultMap.get("result_code");
//        //判断订单是否支付
//        if ("FAIL".equals(resultCode)) {
//            return;
//        }

        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getStatus, "0")
                .eq(SeckillOrder::getUserId, username));
        //判断数据库是否有数据
        if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())) {
            //从redis中获取数据
            seckillOrder = (SeckillOrder) redisTemplate.opsForHash().get(userRecode.getTime() + "_order", userRecode.getOrderId());
            if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())) {
                return;
            }
        }
        //用username作为标识来判断是否为用户手动删除
        if (StringUtils.isEmpty(username)) {
            //超时取消
            seckillOrder.setStatus("超时取消");
        } else {
            //手动取消
            seckillOrder.setStatus("手动取消");
        }

        //修改订单状态
        int i = seckillOrderMapper.updateById(seckillOrder);
        if (i <= 0) {
            throw new RuntimeException("取消订单失败");
        }
        //库存回滚
        rollbackSeckillGoodsStock(userRecode);
        //删除标识位
        //排队状态
        redisTemplate.delete("user_status_" + userRecode.getUsername());
        //排队标识
        redisTemplate.delete("user_order_status_" + userRecode.getUsername());
        //删除临时订单
        redisTemplate.delete(userRecode.getTime() + "_order");
    }

    /**
     * 支付成功修改订单状态
     *
     * @param result
     */
    @Override
    public void updateSeckillOrderStatus(String result) {
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        //获取订单Id
        String orderId = resultMap.get("out_trade_no");
        //获取附加参数
        String attach = resultMap.get("attach");
        //反序列化
        Map<String, String> attachMap = JSONObject.parseObject(attach, Map.class);
        String username = attachMap.get("username");
        //获取用户排队状态
        UserRecode userRecode = (UserRecode) redisTemplate.opsForValue().get("user_status_" + username);
        //根据订单Id查询订单信息
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getId, orderId)
                .eq(SeckillOrder::getStatus, "0"));
        //判断数据库中是否有
        if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())) {
            //从redis中取
            seckillOrder= (SeckillOrder) redisTemplate.opsForHash().get(userRecode.getTime() + "_order", userRecode.getOrderId());
            if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())){
                return;
            }
        }
        //修改订单状态
        seckillOrder.setStatus("已支付");
        //判断交易类型
        if ("1".equals(resultMap.get("payway"))) {
            //微信支付
            //设置交易流水号
            seckillOrder.setOutTradeNo(resultMap.get("transaction_id"));
        } else {
            //支付宝
            //设置交易流水号
            seckillOrder.setOutTradeNo(resultMap.get("trade_no"));
        }
        int i = seckillOrderMapper.updateById(seckillOrder);
        if (i <= 0) {
            throw new RuntimeException("修改订单支付结果失败");
        }
        //删除标识位
        //排队状态
        redisTemplate.delete("user_status_" + userRecode.getUsername());
        //排队标识
        redisTemplate.delete("user_order_status_" + userRecode.getUsername());
        //删除临时订单
        redisTemplate.delete(userRecode.getTime() + "_order");
    }

    /**
     * 回滚库存
     *
     * @param userRecode
     */
    private void rollbackSeckillGoodsStock(UserRecode userRecode) {
        //回滚商品库存的自增值,并且回滚到库存
        Long increment =
                redisTemplate.opsForHash().increment("SeckillStockCount" + userRecode.getTime(), userRecode.getGoodsId(), userRecode.getNum());
        //从redis中获取商品数据
        SeckillGoods seckillGoods =
                (SeckillGoods) redisTemplate.opsForHash().get(userRecode.getTime(), userRecode.getGoodsId());
        //判断活动是否结束,没结束更新商品数据,更新队列数据
        if (seckillGoods != null) {
            //设置库存
            seckillGoods.setStockCount(increment.intValue());
            seckillGoods.setNum(increment.intValue());
            //更新redis库存数据
            redisTemplate.opsForHash().put(userRecode.getTime(), userRecode.getGoodsId(), seckillGoods);
            String[] ids = seckillGoodsStock(userRecode.getGoodsId(), userRecode.getNum());
            redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + userRecode.getGoodsId(), ids);

        }


    }

    /**
     * 存储库存的集合
     *
     * @param id
     * @param stockCount
     * @return
     */
    private String[] seckillGoodsStock(String id, Integer stockCount) {
        String[] ids = new String[stockCount];
        for (int i = 0; i < stockCount; i++) {
            ids[i] = id;
        }
        return ids;
    }

}
