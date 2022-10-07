package com.atguigu.gmall.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author 黄梁峰
 *
 * 商品详情远程调用接口
 */
@FeignClient(name = "service-item", path = "/api/item")
public interface ItemFeign {

    /**
     * 查询商品详情信息
     *
     * @param skuId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public Map getItemInfo(@PathVariable(value = "skuId") Long skuId);
}
