package com.atguigu.gmall.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄梁峰
 */
@Configuration
public class ThreadPoolConfig {


        @Bean
        public ThreadPoolExecutor threadPoolExecutor() {
            return new ThreadPoolExecutor(8,
                    8,
                    60,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(10000));

        }
    }
