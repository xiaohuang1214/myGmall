package com.atguigu.gmall.user.feign;

import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author 黄梁峰
 * 用户地址管理feign接口
 */
@FeignClient(name = "service-user", path = "/api/user/address", contextId = "userFeign")
public interface UserFeign {
    /**
     * 获取用户地址
     *
     * @return
     */
    @GetMapping("/getUserAddress")
    public List<UserAddress> getUserAddress();
}
