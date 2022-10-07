package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author 黄梁峰
 * <p>
 * 购物车的mapper
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {

    /**
     * 单选
     *
     * @param username
     * @param skuId
     * @param status
     * @return
     */
    @Update("UPDATE cart_info SET is_checked = #{status} WHERE user_id = #{username} AND sku_id = #{skuId}")
    public int checkOne(@Param("username") String username,
                        @Param("skuId") Long skuId,
                        @Param("status") Short status);


    /**
     * 全选
     *
     * @param username
     * @param status
     * @return
     */
    @Update("UPDATE cart_info SET is_checked = #{status} WHERE user_id = #{username}")
    public int checkAll(@Param("username") String username,
                        @Param("status") Short status);
}
