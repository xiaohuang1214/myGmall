package com.atguigu.gmall.gateway.config;

import com.atguigu.gmall.gateway.util.IpUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * <p>
 * 自定义全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求体
        ServerHttpRequest request = exchange.getRequest();
        //获取响应体
        ServerHttpResponse response = exchange.getResponse();
        //从用户请求的url中获取token参数
        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            //若url中没有,则从head中取token
            token = request.getHeaders().getFirst("token");
            if (StringUtils.isEmpty(token)) {
                //从cookie中获取token
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if (cookies.size() > 0) {
                    HttpCookie httpCookie = cookies.getFirst("token");
                    if (httpCookie != null) {
                        token = httpCookie.getValue();
                    }
                }
            }
        }
        //若全部没有,拒绝用户请求
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //获取ip地址
        String ipAddress = IpUtil.getGatwayIpAddress(request);
        //从redis中获取token
        String redisToken = stringRedisTemplate.opsForValue().get(ipAddress);
        //如果token为null说明用户没登陆
        if (redisToken == null) {
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //判断redis中的token是否等于传入的token
        if (!token.equals(redisToken)) {
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //将token以固定的格式存储到request中
        request.mutate().header("Authorization", "bearer " + token);
        //放行
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
