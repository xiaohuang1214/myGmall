package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 黄梁峰
 *
 * 前端管理启动类
 */

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atguigu.gmall.item.feign",
                        "com.atguigu.gmall.product.feign",
                    "com.atguigu.gmall.list.feign"})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
