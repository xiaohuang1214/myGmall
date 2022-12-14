package com.atguigu.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 黄梁峰
 * 订单管理微服务
 */
@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atguigu.gmall.cart.feign", "com.atguigu.gmall.product.feign", "com.atguigu.gmall.payment.feign"})
@ServletComponentScan("com.atguigu.gmall.order.filter")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
