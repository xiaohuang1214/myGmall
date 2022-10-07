package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 黄梁峰
 * <p>
 * 平台属性
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 查询所有平台属性和属性值
     *
     * @param cateagory1Id
     * @param cateagory2Id
     * @param cateagory3Id
     * @return
     */
    public List<BaseAttrInfo> selectAllByAttrValueList(@Param("cateagory1Id") Long cateagory1Id,
                                                       @Param("cateagory2Id") Long cateagory2Id,
                                                       @Param("cateagory3Id") Long cateagory3Id);

    /**
     * 查询指定sku的平台属性信息
     *
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);
}
