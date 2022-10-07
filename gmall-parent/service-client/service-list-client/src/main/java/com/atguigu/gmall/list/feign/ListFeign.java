package com.atguigu.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 黄梁峰
 *
 * 商品搜索feign接口管理
 */
@FeignClient(name = "service-list", path = "/api/list", contextId = "listFeign")
public interface ListFeign {
    /**
     * 将上架的商品写入es
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/addGoodsToEs/{skuId}")
    public Boolean addGoodsToEs(@PathVariable(value = "skuId")Long skuId);

    /**
     * 将商品从es中删除
     *
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/deleteGoodsToEs/{goodsId}")
    public Boolean deleteGoodsToEs(@PathVariable(value = "goodsId")Long goodsId);

    /**
     * 更新热点数据
     *
     * @param goodsId
     * @return
     */
    @GetMapping("/getHotScore/{goodsId}")
    public Boolean getHostScore(@PathVariable("goodsId") Long goodsId);
}
