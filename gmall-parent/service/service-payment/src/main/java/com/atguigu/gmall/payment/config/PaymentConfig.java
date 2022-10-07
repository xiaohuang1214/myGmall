package com.atguigu.gmall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄梁峰
 *
 */
@Configuration
public class PaymentConfig {

    /**
     * 定义支付交换机
     *
     * @return
     */
    @Bean("payExchange")
    public Exchange payExchange(){
        return ExchangeBuilder.directExchange("pay_exchange").build();
    }

    /**
     * 定义支付消息队列
     *
     * @return
     */
    @Bean("orderPayQueue")
    public Queue orderPayQueue(){
        return QueueBuilder.durable("order_pay_queue").build();
    }

    /**
     * 绑定交换机和队列
     *
     * @param payExchange
     * @param orderPayQueue
     * @return
     */
    @Bean
    public Binding payBinding(@Qualifier("payExchange") Exchange payExchange,
                              @Qualifier("orderPayQueue") Queue orderPayQueue){
        return BindingBuilder.bind(orderPayQueue).to(payExchange).with("order.pay").noargs();
    }

    /**
     * 定义秒杀支付交换机
     *
     * @return
     */
    @Bean("seckillPayExchange")
    public Exchange seckillPayExchange(){
        return ExchangeBuilder.directExchange("seckill_pay_exchange").build();
    }

    /**
     * 定义秒杀支付消息队列
     *
     * @return
     */
    @Bean("seckillOrderPayQueue")
    public Queue seckillOrderPayQueue(){
        return QueueBuilder.durable("seckill_order_pay_queue").build();
    }

    /**
     * 绑定秒杀交换机和秒杀队列
     *
     * @param seckillPayExchange
     * @param seckillOrderPayQueue
     * @return
     */
    @Bean
    public Binding seckillPayBinding(@Qualifier("seckillPayExchange") Exchange seckillPayExchange,
                              @Qualifier("seckillOrderPayQueue") Queue seckillOrderPayQueue){
        return BindingBuilder.bind(seckillOrderPayQueue).to(seckillPayExchange).with("seckill.order.pay").noargs();
    }

}
