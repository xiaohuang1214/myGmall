package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * @author 黄梁峰
 * <p>
 * 购物车相关接口
 */

public interface CartInfoService {
    /**
     * 加入购物车
     *
     * @param skuId
     * @param num
     */
    public void addCart(Long skuId, Integer num);

    /**
     * 查询所有购物车
     *
     * @return
     */
    public List<CartInfo> getCartInfoList();


    /**
     * 选中/未选中
     * @param skuId
     * @param status
     */
    public void checked(Long skuId, Short status);


    /**
     * 删除商品
     *
     * @param skuId
     */
    public void removeCartInfo(Long skuId);

    /**
     * 获取购物车信息和用户地址
     * @return
     */
    public Map<String, Object> getCartMessage();

    /**
     * 清空购物车
     */
    public void removeAll();


}
