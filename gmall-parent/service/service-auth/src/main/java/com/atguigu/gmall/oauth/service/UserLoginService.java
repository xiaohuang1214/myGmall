package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * @author 黄梁峰
 *
 * 用户登录的接口
 */
public interface UserLoginService {
    /**
     * 登录并获取token
     * @param username
     * @param password
     * @return
     */
    public AuthToken login(String username, String password);
}
