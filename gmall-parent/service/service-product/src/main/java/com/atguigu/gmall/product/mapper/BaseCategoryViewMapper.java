package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 黄梁峰
 *
 * 一二三级分类视图表
 */
@Mapper
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {
}
