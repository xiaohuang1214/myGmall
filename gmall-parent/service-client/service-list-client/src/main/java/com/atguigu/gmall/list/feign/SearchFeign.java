package com.atguigu.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author 黄梁峰
 *
 * 搜索feign接口
 */
@FeignClient(name = "service-list", path = "/api/search", contextId = "searchFeign")
public interface SearchFeign {
    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String, String> searchData);
}
