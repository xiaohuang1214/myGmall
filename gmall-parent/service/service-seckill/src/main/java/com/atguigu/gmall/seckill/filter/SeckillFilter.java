package com.atguigu.gmall.seckill.filter;

import com.atguigu.gmall.seckill.util.SeckillThreadLocalUtil;
import com.atguigu.gmall.seckill.util.TokenUtil;
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
 * @author 黄梁峰
 * <p>
 * 自定义过滤器
 */
@WebFilter(filterName = "seckillFilter", urlPatterns = "/*")
@Order(1)
public class SeckillFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取token
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //解析token
        Map<String, String> map = TokenUtil.dcodeToken(token);
        //非空校验
        if (map.isEmpty()) {
            return;
        }
        //将用户名存入本地线程对象
        SeckillThreadLocalUtil.setUsername(map.get("username"));
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
