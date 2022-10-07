package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 *
 * 秒杀支付
 *
 */
@Component
@Log4j2
public class SeckillOrderPaymentListener {
    @Resource
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = "seckill_order_pay_queue")
    public void seckillGoodsRabbit(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        //将获取的消息转换成String类型
        String result = new String(body);

        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //修改订单状态
            seckillOrderService.updateSeckillOrderStatus(result);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //秒杀失败,直接丢弃
                channel.basicReject(deliveryTag, false);
            } catch (Exception e1) {
                log.error("秒杀失败,响应报文为:" + result);
            }
        }
    }
}
