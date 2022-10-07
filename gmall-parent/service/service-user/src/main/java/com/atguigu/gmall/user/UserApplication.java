package com.atguigu.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 黄梁峰
 * 用户微服务启动类
 *
 */
@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@EnableDiscoveryClient
@ServletComponentScan("com.atguigu.gmall.user.filter")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
