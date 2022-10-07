package com.atguigu.gmall.cart.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 购物车管理拦截器
 *
 * @author 黄梁峰
 */
@Component
public class CartInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取令牌
        String authorization = request.getHeader("Authorization");
        //将令牌放入requestTemplate
        requestTemplate.header("Authorization", authorization);
    }
}
