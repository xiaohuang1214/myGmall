package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置类
 *
 * @author 黄梁峰
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        //cors跨域配置
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //所有请求头信息
        corsConfiguration.addAllowedHeader("*");
        //所有请求方法
        corsConfiguration.addAllowedMethod("*");
        //设置允许访问的网络
        corsConfiguration.addAllowedOrigin("*");
        //设置是否从服务获取cookie
        corsConfiguration.setAllowCredentials(true);

        //配置源对象
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",
                corsConfiguration);

        //cors过滤器对象
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }

}
