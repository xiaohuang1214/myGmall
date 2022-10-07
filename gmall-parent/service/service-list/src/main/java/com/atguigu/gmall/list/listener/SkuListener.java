package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.GoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * 商品上下架
 */
@Component
@Log4j2
public class SkuListener {
    @Resource
    private GoodsService goodsService;

    /**
     * 将商品信息写入es
     * @param message
     */
    @RabbitListener(queues = "sku_upper_queue")
    public void skuUpper(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        String skuId = new String(body);
        //获取消息数量
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            System.out.println("商品上架成功,id为:"+skuId);
            //上架同步
            goodsService.addGoodsToEs(Long.parseLong(skuId));
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                if (messageProperties.getRedelivered()) {
                    log.error("连续两次上架失败,商品的id为:"+skuId);
                    channel.basicReject(deliveryTag, false);
                } else {
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("拒绝消息失败,商品上架消息同步失败, 商品的id为:" + skuId);
            }
        }
    }

    /**
     * 从es中删除商品信息
     * @param message
     */
    @RabbitListener(queues = "sku_down_queue")
    public void skuDown(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        String skuId = new String(body);
        //获取消息数量
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            System.out.println("商品下架成功,id为:"+skuId);
            //下架同步
            goodsService.deleteGoodsToEs(Long.parseLong(skuId));
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                if (messageProperties.getRedelivered()) {
                    log.error("连续两次下架失败,商品的id为:"+skuId);
                    channel.basicReject(deliveryTag, false);
                } else {
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("拒绝消息失败,商品下架消息同步失败, 商品的id为:" + skuId);
            }
        }
    }
}
