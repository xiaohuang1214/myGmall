package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @author 黄梁峰
 *
 * 用户地址的接口
 */
public interface UserAddressService {

    /**
     * 获取用户地址
     *
     * @return
     */
    public List<UserAddress> getUserAddress();
}
