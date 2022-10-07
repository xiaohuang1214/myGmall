package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 */

@RestController
@RequestMapping(value = "/api/baseAttrValue")
public class BaseAttrValueController {
    @Resource
    private BaseAttrValueService baseAttrValueService;

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getById/{id}")
    public Result getById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseAttrValueService.getById(id));
    }

    /**
     * 查询所有
     *
     * @return
     */
    @GetMapping(value = "/getAll")
    public Result getAll() {
        return Result.ok(baseAttrValueService.getALl());
    }

    /**
     * 新增
     *
     * @param baseAttrValue
     * @return
     */
    @PostMapping(value = "/add")
    public Result add(@RequestBody BaseAttrValue baseAttrValue) {
        baseAttrValueService.add(baseAttrValue);
        return Result.ok();
    }

    /**
     * 修改
     *
     * @param baseAttrValue
     * @return
     */
    @PutMapping(value = "/update")
    public Result update(@RequestBody BaseAttrValue baseAttrValue) {
        baseAttrValueService.update(baseAttrValue);
        return Result.ok();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable("id") Long id) {
        baseAttrValueService.delete(id);
        return Result.ok();
    }

    /**
     * 条件查询
     *
     * @param baseAttrValue
     * @return
     */
    @PostMapping(value = "/search")
    public Result search(@RequestBody BaseAttrValue baseAttrValue) {
        return Result.ok(baseAttrValueService.search(baseAttrValue));
    }

    /**
     * 分页
     *
     * @param current
     * @param size
     * @return
     */
    @PostMapping("/page/{current}/{size}")
    public Result page(@PathVariable("current") Long current,
                       @PathVariable("size") Long size) {
        return Result.ok(baseAttrValueService.page(current, size));
    }

    /**
     * 条件分页
     *
     * @param current
     * @param size
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/searchPage/{current}/{size}")
    public Result searchPage(@PathVariable("current") Long current,
                             @PathVariable("size") Long size,
                             @RequestBody BaseAttrValue baseAttrValue) {
        return Result.ok(baseAttrValueService.searchPage(current, size, baseAttrValue));
    }

}
