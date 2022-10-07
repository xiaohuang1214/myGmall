package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * <p>
 * 平台属性
 */

@RestController
@RequestMapping(value = "/api/baseAttrInfo")
public class BaseAttrInfoController {
    @Resource
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseAttrInfoService.getById(id));
    }


    /**
     * 查询所有
     *
     * @return
     */
    @GetMapping("/getAll")
    public Result getAll() {
        return Result.ok(baseAttrInfoService.getAll());
    }

    /**
     * 新增
     *
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.add(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 修改
     *
     * @param baseAttrInfo
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable(value = "id") Long id) {
        baseAttrInfoService.delete(id);
        return Result.ok();
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo) {
        return Result.ok(baseAttrInfoService.search(baseAttrInfo));
    }

    /**
     * 分页
     *
     * @param current
     * @param size
     * @return
     */
    @PostMapping("/page/{current}/{size}")
    public Result page(@PathVariable(value = "current") Long current,
                       @PathVariable(value = "size") Long size) {
        return Result.ok(baseAttrInfoService.page(current, size));
    }

    /**
     * 条件分页查询
     *
     * @param current
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/searchPage/{current}/{size}")
    public Result searchPage(@PathVariable(value = "current") Long current,
                             @PathVariable(value = "size") Long size,
                             @RequestBody BaseAttrInfo baseAttrInfo) {
        return Result.ok(baseAttrInfoService.searchPage(current, size, baseAttrInfo));
    }


}
