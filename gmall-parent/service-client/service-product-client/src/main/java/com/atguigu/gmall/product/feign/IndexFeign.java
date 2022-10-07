package com.atguigu.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author 黄梁峰
 *
 * 查询首页三级分类导航
 */

@FeignClient(name = "service-product", path = "/admin/product", contextId = "indexFeign")
public interface IndexFeign {

    /**
     * 查询首页三级分类导航
     *
     * @return
     */
    @GetMapping("/getIndexCategory")
    public List<JSONObject> getIndexCategory();
}
