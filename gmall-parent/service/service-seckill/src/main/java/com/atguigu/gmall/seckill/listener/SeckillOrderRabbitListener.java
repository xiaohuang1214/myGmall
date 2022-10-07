package com.atguigu.gmall.seckill.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * 秒杀订单mq监听类
 */
@Component
@Log4j2
public class SeckillOrderRabbitListener {
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = "seckill_order_queue")
    public void seckillOrder(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        //将获取的消息转换成String类型
        String result = new String(body);
        //反序列化参数
        UserRecode userRecode = JSONObject.parseObject(result, UserRecode.class);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //秒杀下单
            seckillOrderService.realSeckillOrderAdd(userRecode);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //秒杀失败统一异常处理
                userRecode.setStatus(3);
                userRecode.setMsg(e.getMessage());
                //更新状态
                redisTemplate.opsForValue().set("user_status_" + userRecode.getUsername(), userRecode);
                redisTemplate.delete("user_order_status_" + userRecode.getUsername());
                //秒杀失败,直接丢弃
                channel.basicReject(deliveryTag, false);
            } catch (Exception e1) {
                log.error("秒杀失败!!!");
            }
        }
    }
}
