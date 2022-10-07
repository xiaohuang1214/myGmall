package com.atguigu.gmall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.feign.IndexFeign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 黄梁峰
 *
 * 首页管理
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    @Resource
    private IndexFeign indexFeign;

    @GetMapping
    public String index(Model model){
        List<JSONObject> list = indexFeign.getIndexCategory();
        model.addAttribute("list", list);
        return "index1";
    }
}
