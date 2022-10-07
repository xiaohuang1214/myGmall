package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄梁峰
 * 秒杀消息队列配置
 */
@Configuration
public class SeckillOrderRabbitConfig {

    /**
     * 秒杀交换机
     *
     * @return
     */
    @Bean("seckillOrderExchange")
    public Exchange seckillOrderExchange(){
        return ExchangeBuilder.directExchange("seckill_order_exchange").build();
    }

    /**
     * 秒杀队列
     *
     * @return
     */
    @Bean("seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return QueueBuilder.durable("seckill_order_queue").build();
    }

    /**
     * 绑定交换机和队列
     *
     * @param seckillOrderExchange
     * @param seckillOrderQueue
     * @return
     */
    @Bean
    public Binding seckillOrderBinding(@Qualifier("seckillOrderExchange") Exchange seckillOrderExchange,
                                       @Qualifier("seckillOrderQueue") Queue seckillOrderQueue){
        return BindingBuilder.bind(seckillOrderQueue).to(seckillOrderExchange).with("seckill.order.add").noargs();
    }

}
