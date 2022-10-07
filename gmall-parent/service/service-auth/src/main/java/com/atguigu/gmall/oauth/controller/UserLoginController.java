package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.UserLoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 黄梁峰
 *
 * 自定义登录
 */
@RestController
@RequestMapping(value = "/user/login")
public class UserLoginController {

    @Resource
    private UserLoginService userLoginService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 登录获取token
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result login(String username, String password, HttpServletRequest request){
        //登录
        AuthToken authToken = userLoginService.login(username, password);
        //获取ip地址
        String ipAddress = IpUtil.getIpAddress(request);
        //将ip地址和token存储到redis
        stringRedisTemplate.opsForValue().set(ipAddress, authToken.getAccessToken());

        return Result.ok(authToken);
    }
}
