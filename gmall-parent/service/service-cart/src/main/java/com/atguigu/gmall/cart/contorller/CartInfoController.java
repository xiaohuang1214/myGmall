package com.atguigu.gmall.cart.contorller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 黄梁峰
 * <p>
 * 购物车相关控制层
 */
@RestController
@RequestMapping(value = "/api/cart")
public class CartInfoController {
    @Resource
    private CartInfoService cartInfoService;

    /**
     * 添加购物车
     *
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping(value = "/addCartInfo")
    public Result addCartInfo(Long skuId, Integer num) {
        cartInfoService.addCart(skuId, num);
        return Result.ok();
    }

    /**
     * 查询购物车信息
     *
     * @return
     */
    @GetMapping("/getCartInfoList")
    public Result getCartInfoList() {
        return Result.ok(cartInfoService.getCartInfoList());
    }


    /**
     * 选中
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/checked")
    public Result checked(Long skuId) {
        cartInfoService.checked(skuId, CartConst.ON_CHECKED);
        return Result.ok();
    }

    /**
     * 未选中
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/unchecked")
    public Result unchecked(Long skuId) {
        cartInfoService.checked(skuId, CartConst.UN_CHECKED);
        return Result.ok();
    }


    /**
     * 删除购物车商品
     *
     * @param skuId
     * @return
     */
    @GetMapping(value = "/removeCartInfo")
    public Result removeCartInfo(Long skuId) {
        cartInfoService.removeCartInfo(skuId);
        return Result.ok();
    }


    /**
     * 获取购物车结算信息
     *
     * @return
     */
    @GetMapping(value = "/getCartInfo")
    public Result getCartInfo(){
        return Result.ok(cartInfoService.getCartMessage());
    }


}
