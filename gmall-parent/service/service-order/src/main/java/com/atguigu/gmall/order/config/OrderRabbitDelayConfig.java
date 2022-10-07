package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄梁峰
 * 订单延迟消息的配置类
 */
@Configuration
public class OrderRabbitDelayConfig {


    /**
     * 正常交换机
     *
     * @return
     */
    @Bean("orderNomalExchange")
    public Exchange orderNomalExchange(){
        return ExchangeBuilder.directExchange("order_nomal_exchange").build();
    }

    /**
     * 死信队列
     *
     * @return
     */
    @Bean("orderDeadQueue")
    public Queue orderDeadQueue(){
        return QueueBuilder
                .durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange","order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","order.nomal")
                .build();
    }


    /**
     * 正常交换机绑定死信队列
     *
     * @param orderNomalExchange
     * @param orderDeadQueue
     * @return
     */
    @Bean
    public Binding orderDeadBinding(@Qualifier("orderNomalExchange") Exchange orderNomalExchange,
                                    @Qualifier("orderDeadQueue") Queue orderDeadQueue){
        return BindingBuilder.bind(orderDeadQueue).to(orderNomalExchange).with("order.dead").noargs();
    }


    /**
     * 死信交换机
     *
     * @return
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){
        return ExchangeBuilder.directExchange("order_dead_exchange").build();
    }

    /**
     * 正常队列
     *
     * @return
     */
    @Bean("orderNomalQueue")
    public Queue orderNomalQueue(){
        return QueueBuilder.durable("order_nomal_queue").build();
    }

    /**
     * 死信交换机绑定正常队列
     *
     * @param orderDeadExchange
     * @param orderNomalQueue
     * @return
     */
    @Bean
    public Binding orderNomalBinding(@Qualifier("orderDeadExchange") Exchange orderDeadExchange,
                                    @Qualifier("orderNomalQueue") Queue orderNomalQueue){
        return BindingBuilder.bind(orderNomalQueue).to(orderDeadExchange).with("order.nomal").noargs();
    }


}
