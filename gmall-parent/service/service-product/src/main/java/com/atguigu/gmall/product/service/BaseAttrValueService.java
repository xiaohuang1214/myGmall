package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author 黄梁峰
 * <p>
 * 平台属性值
 */
public interface BaseAttrValueService {

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public BaseAttrValue getById(Long id);

    /**
     * 查询所有
     *
     * @return
     */
    public List<BaseAttrValue> getALl();

    /**
     * 新增
     *
     * @param baseAttrValue
     */
    public void add(BaseAttrValue baseAttrValue);

    /**
     * 修改
     *
     * @param baseAttrValue
     */
    public void update(BaseAttrValue baseAttrValue);

    /**
     * 根据Id删除
     *
     * @param id
     */
    public void delete(Long id);

    /**
     * 条件查询
     *
     * @param baseAttrValue
     * @return
     */
    public List<BaseAttrValue> search(BaseAttrValue baseAttrValue);

    /**
     * 分页
     *
     * @param current
     * @param size
     * @return
     */
    public IPage page(Long current, Long size);

    /**
     * 条件分页
     *
     * @param current
     * @param size
     * @param baseAttrValue
     * @return
     */
    public IPage searchPage(Long current,
                            Long size,
                            BaseAttrValue baseAttrValue);
}
