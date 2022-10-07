package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * <p>
 * 监听秒杀商品活动结束同步库存数据的消费者
 */
@Component
@Log4j2
public class SeckillGoodsRabbitListener {
    @Resource
    private SeckillGoodsService seckillGoodsService;

    @RabbitListener(queues = "seckill_goods_nomal_queue")
    public void seckillGoodsRabbit(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        //将获取的消息转换成String类型
        String key = new String(body);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //触发数据同步
            seckillGoodsService.mergeSeckillGoodsStockToDb(key);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //秒杀失败,直接丢弃
                channel.basicReject(deliveryTag, false);
            } catch (Exception e1) {
                log.error("秒杀失败,时间段为:" + key);
            }
        }
    }
}
