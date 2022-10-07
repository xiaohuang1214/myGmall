package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.util.CartThreadLoaclUtil;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ItemFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 黄梁峰
 * <p>
 * 购物车相关接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {
    @Resource
    private CartInfoMapper cartInfoMapper;
    @Resource
    private ItemFeign itemFeign;

    /**
     * 加入购物车
     *
     * @param skuId
     * @param num
     */
    @Override
    public void addCart(Long skuId, Integer num) {
        //参数校验
        if (skuId == null || num == null) {
            throw new RuntimeException("参数错误,添加购物车失败");
        }
        //获取商品信息
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        //获取商品价格
        BigDecimal skuPrice = itemFeign.getSkuPrice(skuInfo.getId());
        //非空校验
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("商品不存在,添加购物车失败");
        }
        //获取用户名
        String username = CartThreadLoaclUtil.getUsername();
        //判断购物车中有没有这个商品
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username)
                .eq(CartInfo::getSkuId, skuInfo.getId()));
        if (cartInfo == null || cartInfo.getId() == null) {
            //构建购物车对象
            cartInfo = new CartInfo();
            cartInfo.setUserId(username);
            cartInfo.setSkuId(skuInfo.getId());
            cartInfo.setCartPrice(skuPrice);
            cartInfo.setSkuNum(num);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            //保存购物车
            int insert = cartInfoMapper.insert(cartInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增购物车失败");
            }
        } else {
            //若果有合并商品数量
            int skuNum = cartInfo.getSkuNum() + num;
            //判断商品数量是否小于等于0,是则删除商品
            if (skuNum <= 0) {
                int delete = cartInfoMapper.deleteById(cartInfo);
                if (delete < 0) {
                    throw new RuntimeException("新增购物车失败");
                }
            } else {
                //若合并后商品数量大于0,则修改商品数量
                cartInfo.setSkuNum(skuNum);
                //重新选中该商品
                cartInfo.setIsChecked(Integer.valueOf(CartConst.ON_CHECKED));
                int update = cartInfoMapper.updateById(cartInfo);
                if (update < 0) {
                    throw new RuntimeException("新增购物车失败");
                }
            }
        }

    }

    /**
     * 查询所有购物车
     *
     * @return
     */
    @Override
    public List<CartInfo> getCartInfoList() {
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLoaclUtil.getUsername()));
        return cartInfoList;
    }

    /**
     * 选中/未选中
     *
     * @param skuId
     * @param status
     */
    @Override
    public void checked(Long skuId, Short status) {
        //判断若skuId为null则是多选
        if (skuId == null) {
            int checkAll = cartInfoMapper.checkAll(CartThreadLoaclUtil.getUsername(), status);
            if (checkAll < 0) {
                throw new RuntimeException("错误请重试");
            }
        }
        //skuId有值为单选
        int checkOne = cartInfoMapper.checkOne(CartThreadLoaclUtil.getUsername(), skuId, status);
        if (checkOne < 0) {
            throw new RuntimeException("错误请重试!!");
        }

    }


    /**
     * 删除商品
     *
     * @param skuId
     */
    @Override
    public void removeCartInfo(Long skuId) {
        //若skuId为null则删除所有选中的商品
        if (skuId == null) {
            int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                    .eq(CartInfo::getIsChecked, CartConst.ON_CHECKED)
                    .eq(CartInfo::getUserId, CartThreadLoaclUtil.getUsername()));
            if (delete < 0) {
                throw new RuntimeException("删除失败!!");
            }
        } else {
            //若skuId有值则删除单个商品
            int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                    .eq(CartInfo::getSkuId, skuId)
                    .eq(CartInfo::getUserId, CartThreadLoaclUtil.getUsername()));
            if (delete < 0) {
                throw new RuntimeException("删除失败!!");
            }
        }
    }



    /**
     * 获取购物车信息
     * @return
     */
    @Override
    public Map<String, Object> getCartMessage() {
        //初始化返回结果
        Map<String, Object> result = new HashMap();
        //获取购物车信息
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLoaclUtil.getUsername())
                .eq(CartInfo::getIsChecked, CartConst.ON_CHECKED));
        if (cartInfoList == null){
            throw new RuntimeException("购物车中没有商品");
        }
        //总数量
        AtomicInteger totalNum = new AtomicInteger(0);
        //总金额
        AtomicDouble totalPrice = new AtomicDouble(0);
        List<CartInfo> cartInfoListNew = cartInfoList.stream().map(cartInfo -> {
            totalNum.addAndGet(cartInfo.getSkuNum());
            BigDecimal skuPrice = itemFeign.getSkuPrice(cartInfo.getSkuId());
            totalPrice.addAndGet(cartInfo.getSkuNum() * Double.parseDouble(skuPrice.toString()));
            cartInfo.setSkuPrice(skuPrice);
            return cartInfo;
        }).collect(Collectors.toList());

        result.put("totalNum",totalNum);
        result.put("totalPrice",totalPrice);
        result.put("cartInfoList", cartInfoListNew);

        return result;
    }

    /**
     * 清空购物车
     */
    @Override
    public void removeAll() {
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLoaclUtil.getUsername())
                .eq(CartInfo::getIsChecked, CartConst.ON_CHECKED));
        if (delete <= 0){
            throw new RuntimeException("清空购物车失败");
        }
    }
}
