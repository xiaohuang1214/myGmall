package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄梁峰
 * <p>
 * 数据同步延迟队列
 */
@Configuration
public class SeckillGoodsRabbitConfig {

    /**
     * 正常交换机
     *
     * @return
     */
    @Bean("seckillGoodsNomalExchange")
    public Exchange seckillGoodsNomalExchange() {
        return ExchangeBuilder.directExchange("seckill_goods_nomal_exchange").build();
    }

    /**
     * 死信队列
     *
     * @return
     */
    @Bean("seckillGoodsDeadQueue")
    public Queue seckillGoodsDeadQueue() {
        return QueueBuilder
                .durable("seckill_goods_dead_queue")
                .withArgument("x-dead-letter-exchange", "seckill_goods_dead_exchange")
                .withArgument("x-dead-letter-routing-key", "seckill.goods.nomal")
                .build();
    }

    /**
     * 正常交换机绑定死信队列
     *
     * @param seckillGoodsNomalExchange
     * @param seckillGoodsDeadQueue
     * @return
     */
    @Bean
    public Binding seckillGoodsDeadBinding(@Qualifier("seckillGoodsNomalExchange") Exchange seckillGoodsNomalExchange,
                                           @Qualifier("seckillGoodsDeadQueue") Queue seckillGoodsDeadQueue) {
        return BindingBuilder.bind(seckillGoodsDeadQueue).to(seckillGoodsNomalExchange).with("seckill.goods.dead").noargs();
    }

    /**
     * 死信交换机
     *
     * @return
     */
    @Bean("seckillGoodsDeadExchange")
    public Exchange seckillGoodsDeadExchange() {
        return ExchangeBuilder.directExchange("seckill_goods_dead_exchange").build();
    }

    /**
     * 正常队列
     *
     * @return
     */
    @Bean("seckillGoodsNomalQueue")
    public Queue seckillGoodsNomalQueue() {
        return QueueBuilder.durable("seckill_goods_nomal_queue").build();
    }

    /**
     * 死信交换机绑定正常队列
     *
     * @param seckillGoodsDeadExchange
     * @param seckillGoodsNomalQueue
     * @return
     */
    @Bean
    public Binding seckillGoodsNomalBinding(@Qualifier("seckillGoodsDeadExchange") Exchange seckillGoodsDeadExchange,
                                            @Qualifier("seckillGoodsNomalQueue") Queue seckillGoodsNomalQueue) {
        return BindingBuilder.bind(seckillGoodsNomalQueue).to(seckillGoodsDeadExchange).with("seckill.goods.nomal").noargs();
    }

}
