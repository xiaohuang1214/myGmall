package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 黄梁峰
 *
 * 查询首页三级分类导航
 */
@RestController
@RequestMapping("/admin/product")
public class IndexController {
    @Resource
    private IndexService indexService;

    /**
     * 查询首页三级分类导航
     *
     * @return
     */
    @GetMapping("/getIndexCategory")
    public List<JSONObject> getIndexCategory(){
        return indexService.getIndexCategory();
    }
}
