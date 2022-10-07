package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author 黄梁峰
 * <p>
 * 商品详情页
 */
@Controller
@RequestMapping(value = "/page/item")
public class ItemController {
    @Resource
    private ItemFeign itemFeign;

    @GetMapping(value = "/{skuId}")
    public String item(@PathVariable("skuId") Long skuId, Model model) {
        Map map = itemFeign.getItemInfo(skuId);
        model.addAllAttributes(map);
        return "item1";
    }


    @Resource
    private TemplateEngine templateEngine;

    @GetMapping("/createHtml/{skuId}")
    @ResponseBody
    public String createHtml(@PathVariable("skuId") Long skuId) throws Exception {
        Map itemInfo = itemFeign.getItemInfo(skuId);
        Context context = new Context();
        context.setVariables(itemInfo);
        PrintWriter printWriter = new PrintWriter(new File("G:\\html", skuId + ".html"), "UTF-8");
        templateEngine.process("item2", context, printWriter);
        return "success";
    }


}
