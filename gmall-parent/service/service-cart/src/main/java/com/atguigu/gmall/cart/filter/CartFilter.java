package com.atguigu.gmall.cart.filter;


import com.atguigu.gmall.cart.util.CartThreadLoaclUtil;
import com.atguigu.gmall.cart.util.TokenUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义过滤器
 * @author 黄梁峰
 */
@WebFilter(filterName = "cartFilter", urlPatterns = "/*")
@Order(1)
public class CartFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取token
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //解析token
        Map<String, String> map = TokenUtil.dcodeToken(token);
        //非空校验
        if (map.isEmpty()){
            return;
        }
        //获取并设置用户名
        CartThreadLoaclUtil.setUsername(map.get("username"));
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
