package com.atguigu.gmall.item.config;

import org.springframework.beans.factory.annotation.Value;
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
        @Value("${thread.pool.coreSize}")
        Integer coreSize;
        @Value("${thread.pool.maxSize}")
        Integer maxSize;
        @Value("${thread.pool.keepalive}")
        Integer keepalive;
        @Value("${thread.pool.blockQueueSize}")
        Integer blockQueueSize;


        @Bean
        public ThreadPoolExecutor threadPoolExecutor() {
            return new ThreadPoolExecutor(coreSize,
                    maxSize,
                    keepalive,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(blockQueueSize));

        }
    }
