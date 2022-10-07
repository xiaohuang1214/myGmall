package com.atguigu.gmall.seckill.task;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄梁峰
 * <p>
 * 将秒杀商品写入redis的定时任务
 */
@Component
public class SeckillGoodsFromDbToRedisTask {

    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * cron表达式:秒 分 时 日 月 周 年
     */
    @Scheduled(cron = "1/20 * * * * *")
    public void SeckillGoodsFromDbToRedis() {
        //当前时段以及往后的获取秒杀时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历5个时间段,并将每个时间段的商品写入redis
        dateMenus.stream().forEach(dateMenu -> {
            //修改时间格式--秒杀开始时间
            String startTime = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //秒杀结束时间
            Date end = DateUtil.addDateHour(dateMenu, 2);
            String endTime = DateUtil.data2str(end, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //获取存活时间
            Long liveTime = end.getTime() - System.currentTimeMillis();
            //初始化条件表达式
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            //拼接条件--审核通过
            wrapper.eq(SeckillGoods::getStatus, "1");
            //开始时间
            wrapper.ge(SeckillGoods::getStartTime, startTime);
            //结束时间
            wrapper.le(SeckillGoods::getEndTime, endTime);
            //库存大于0
            wrapper.gt(SeckillGoods::getStockCount, 0);
            //判断redis中有没有数据
            String key = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
            Set keys = redisTemplate.opsForHash().keys(key);
            if (!keys.isEmpty()) {
                wrapper.notIn(SeckillGoods::getId, keys);
            }
            //查询商品
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(wrapper);
            //遍历秒杀商品并存入redis
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.opsForHash().put(key, seckillGoods.getId() + "", seckillGoods);
                //将商品库存转换为每个商品Id转成list集合存储在redis--库存扣减
                String[] ids = seckillGoodsStock(seckillGoods.getId() + "", seckillGoods.getStockCount());
                redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(), ids);
                redisTemplate.expire("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(), liveTime, TimeUnit.MILLISECONDS);
                //构建商品库存自增值统计商品的库存
                redisTemplate.opsForHash().increment("SeckillStockCount" + key, seckillGoods.getId() + "", seckillGoods.getStockCount());
            }
            setSeckillGoodsExpireTime(liveTime, key);
        });

    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 设置过期时间
     *
     * @param liveTime
     * @param key
     */
    private void setSeckillGoodsExpireTime(Long liveTime, String key) {
        //判断每个字段只能设置一次
        Long increment = redisTemplate.opsForHash().increment("seckillGoodsExpireTime", key, 1);
        if (increment > 1) {
            return;
        }
        //设置商品过期时间
        redisTemplate.expire(key, liveTime, TimeUnit.MILLISECONDS);
        //延迟消息触发数据同步
        rabbitTemplate.convertAndSend(
                "seckill_goods_nomal_exchange",
                "seckill.goods.dead",
                key,
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setExpiration(300000+"");
                    return message;
                });


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
