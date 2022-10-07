package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.util.PageNumUtil;
import com.atguigu.gmall.list.feign.SearchFeign;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 黄梁峰
 * 搜索页面
 */
@Controller
@RequestMapping(value = "/page/list")
public class SearchController {

    @Value("${item.url}")
    private String itemUrl;

    @Resource
    private SearchFeign searchFeign;

    @GetMapping
    public String search(@RequestParam Map<String, String> searchData, Model model){
        //搜索结果
        Map<String, Object> searchResult = searchFeign.search(searchData);
        model.addAllAttributes(searchResult);
        //用于回显
        model.addAttribute("searchData", searchData);
        //获取当前Url
        model.addAttribute("url", getUrl(searchData));
        //获取排序的url
        model.addAttribute("sortUrl", getSortUrl(searchData));
        //获取总数据量
        Object totalHits = searchResult.get("totalHits");
        //获取页码
        String pageNum = searchData.get("pageNum");
        //初始化分页工具
        Page pageInfo = new Page(Long.parseLong(totalHits.toString()),
                PageNumUtil.getPage(pageNum),
                50);
        model.addAttribute("pageInfo", pageInfo);
        //存放商品详情页的前缀域名
        model.addAttribute("itemUrl", itemUrl);
        return "list";
    }

    /**
     * 排序的url
     * @param searchData
     * @return
     */
    private String getSortUrl(Map<String, String> searchData) {
        StringBuilder url = new StringBuilder("/page/list?");
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取参数的名字
            String key = entry.getKey();
            //不拼接页码和排序
            if (!"pageNum".equals(key) && !"sort".equals(key)){
                String value = entry.getValue();
                url.append(key).append("=").append(value).append("&");
            }

        }
        return url.substring(0,url.length()-1);
    }

    /**
     * url
     * @param searchData
     * @return
     */
    private String getUrl(Map<String, String> searchData) {
        StringBuilder url = new StringBuilder("/page/list?");
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取参数的名字
            String key = entry.getKey();
            //不拼接页码
            if (!"pageNum".equals(key)){
                String value = entry.getValue();
                url.append(key).append("=").append(value).append("&");
            }
        }
        //去掉最后一个&
        return url.substring(0,url.length()-1);
    }
}
