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
 *
 * 订单支付监听
 */
@Component
@Log4j2
public class OrderPayListener {

    @Resource
    private OrderService orderService;

    @RabbitListener(queues = "order_pay_queue")
    public void orderPay(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        //获取订单Id
        String result = new String(body);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //修改订单支付状态
            orderService.updateOrderStatus(result);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (messageProperties.getRedelivered()) {
                    log.error("连续两次消费超时,订单支付失败,订单的报文为:" + result);
                    //第二次
                    channel.basicReject(deliveryTag, false);
                } else {
                    //第一次,再来一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("超时支付订单失败,订单的报文为:" + result);
            }
        }
    }
}
