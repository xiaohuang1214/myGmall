package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 */

@RestController
@RequestMapping(value = "/api/list")
public class GoodsController {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    private GoodsService goodsService;

    /**
     * 构建索引和映射
     *
     * @return
     */
    @GetMapping(value = "/createIndexAndMapping")
    public Result createIndexAndMapping() {
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 将上架的商品写入es
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/addGoodsToEs/{skuId}")
    public Boolean addGoodsToEs(@PathVariable(value = "skuId") Long skuId) {
        goodsService.addGoodsToEs(skuId);
        return true;
    }

    /**
     * 将商品从es中删除
     *
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/deleteGoodsToEs/{goodsId}")
    public Boolean deleteGoodsToEs(@PathVariable(value = "goodsId") Long goodsId) {
        goodsService.deleteGoodsToEs(goodsId);
        return true;
    }

    /**
     * 更新热点数据
     *
     * @param goodsId
     * @return
     */
    @GetMapping("/getHotScore/{goodsId}")
    public Boolean getHostScore(@PathVariable("goodsId") Long goodsId) {
        goodsService.getHotScore(goodsId);
        return true;
    }

}
