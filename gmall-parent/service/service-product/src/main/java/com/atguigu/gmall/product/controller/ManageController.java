package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 */
@RestController
@RequestMapping(value = "/admin/product")
public class ManageController {

    @Resource
    private ManageService manageService;

    /**
     * 查询所有一级分类
     *
     * @return
     */
    @GetMapping(value = "/getCategory1")
    public Result getCategory1() {
        return Result.ok(manageService.getBaseCategory1());
    }

    /**
     * 查询二级分类
     *
     * @param c1Id
     * @return
     */
    @GetMapping(value = "/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable(value = "c1Id") Long c1Id) {
        return Result.ok(manageService.getBaseCategory2(c1Id));
    }

    /**
     * 查询三级分类
     *
     * @param c2Id
     * @return
     */
    @GetMapping(value = "/getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable(value = "c2Id") Long c2Id) {
        return Result.ok(manageService.getBaseCategory3(c2Id));
    }

    /**
     * 保存功能
     *
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 查询所有平台属性和属性值
     *
     * @param cateagory1Id
     * @param cateagory2Id
     * @param cateagory3Id
     * @return
     */
    @GetMapping(value = "/attrInfoList/{cateagory1Id}/{cateagory2Id}/{cateagory3Id}")
    public Result attrInfoList(@PathVariable("cateagory1Id") Long cateagory1Id,
                               @PathVariable("cateagory2Id") Long cateagory2Id,
                               @PathVariable("cateagory3Id") Long cateagory3Id) {
        return Result.ok(manageService.selectAllByAttrValueList(cateagory1Id, cateagory2Id, cateagory3Id));
    }

    /**
     * 点击修改查询属性值
     *
     * @param attrId
     * @return
     */
    @GetMapping(value = "/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable(value = "attrId") Long attrId) {
        return Result.ok(manageService.getAttrValueList(attrId));
    }


    /**
     * 分页查询所有品牌
     *
     * @param current
     * @param size
     * @return
     */
    @GetMapping(value = "/baseTrademark/{current}/{size}")
    public Result getBaseTrademark(@PathVariable(value = "current") Long current,
                                   @PathVariable(value = "size") Long size) {
        return Result.ok(manageService.getBaseTrademark(current, size));
    }

    /**
     * 新增品牌
     *
     * @param baseTrademark
     * @return
     */
    @PostMapping(value = "/baseTrademark/save")
    public Result save(@RequestBody BaseTrademark baseTrademark) {
        manageService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 删除品牌
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/baseTrademark/remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id) {
        manageService.remove(id);
        return Result.ok();
    }

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/baseTrademark/get/{id}")
    public Result get(@PathVariable(value = "id") Long id) {
        return Result.ok(manageService.get(id));
    }

    /**
     * 修改品牌
     *
     * @param baseTrademark
     * @return
     */
    @PutMapping(value = "/baseTrademark/update")
    public Result update(@RequestBody BaseTrademark baseTrademark) {
        manageService.update(baseTrademark);
        return Result.ok();
    }


    /**
     * 获取品牌属性
     *
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList() {
        return Result.ok(manageService.getTrademarkList());
    }


    /**
     * 商品属性SPU分页
     *
     * @param current
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/{current}/{size}")
    public Result page(@PathVariable(value = "current") Long current,
                       @PathVariable(value = "size") Long size,
                       @RequestParam(value = "category3Id", required = false) Long category3Id) {
        IPage page = manageService.page(current, size, category3Id);
        return Result.ok(page);
    }

    /**
     * 获取销售属性
     *
     * @return
     */
    @GetMapping(value = "/baseSaleAttrList")
    public Result baseSaleAttrList() {
        return Result.ok(manageService.baseSaleAttrList());
    }

    /**
     * 保存销售属性
     *
     * @param spuInfo
     * @return
     */
    @PostMapping(value = "/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 根据spuId获取销售属性
     *
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable(value = "spuId") Long spuId) {
        return Result.ok(manageService.spuSaleAttrList(spuId));
    }

    /**
     * 根据spuId获取图片列表
     *
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable(value = "spuId") Long spuId) {
        return Result.ok(manageService.spuImageList(spuId));
    }

    /**
     * 商品属性SKU分页
     *
     * @param current
     * @param size
     * @return
     */
    @GetMapping(value = "/list/{current}/{size}")
    public Result list(@PathVariable(value = "current") Long current,
                       @PathVariable(value = "size") Long size) {
        return Result.ok(manageService.list(current, size));
    }


    /**
     * 新增sku属性
     *
     * @param skuInfo
     * @return
     */
    @PostMapping(value = "/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) throws Exception {
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 上架
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/onSale/{skuId}")
    public Result onSale(@PathVariable(value = "skuId") Long skuId) {
        manageService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 下架
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable(value = "skuId") Long skuId) {
        manageService.cancelSale(skuId);
        return Result.ok();
    }
}
