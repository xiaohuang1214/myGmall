package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.order.service.OrderService;
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
 * 订单超时取消
 */
@Component
@Log4j2
public class OrderTimeOutListener {
    @Resource
    private OrderService orderService;

    @RabbitListener(queues = "order_nomal_queue")
    public void cancelTimeOutOrder(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        //获取订单Id
        long orderId = Long.parseLong(new String(body));
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //超时取消订单
            orderService.cancelOrder(orderId);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (messageProperties.getRedelivered()) {
                    log.error("连续两次消费超时,取消订单失败,商品id为:" + orderId);
                    //第二次
                    channel.basicReject(deliveryTag, false);
                } else {
                    //第一次,再来一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("超时取消订单失败,商品id为:" + orderId);
            }
        }
    }
}
