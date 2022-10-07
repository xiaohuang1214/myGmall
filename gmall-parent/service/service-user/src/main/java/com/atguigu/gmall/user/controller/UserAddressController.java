package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 黄梁峰
 * <p>
 * 获取用户的地址
 */
@RestController
@RequestMapping(value = "/api/user/address")
public class UserAddressController {
    @Resource
    private UserAddressService userAddressService;

    /**
     * 获取用户地址
     *
     * @return
     */
    @GetMapping("/getUserAddress")
    public List<UserAddress> getUserAddress() {
        return userAddressService.getUserAddress();
    }

}
