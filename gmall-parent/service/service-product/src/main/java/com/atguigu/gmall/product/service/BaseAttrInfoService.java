package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author 黄梁峰
 * <p>
 * 平台属性
 */
public interface BaseAttrInfoService {

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public BaseAttrInfo getById(Long id);

    /**
     * 查询所有
     *
     * @return
     */
    public List<BaseAttrInfo> getAll();

    /**
     * 新增
     *
     * @param baseAttrInfo
     */
    public void add(BaseAttrInfo baseAttrInfo);

    /**
     * 修改
     *
     * @param baseAttrInfo
     */
    public void update(BaseAttrInfo baseAttrInfo);

    /**
     * 根据Id删除
     *
     * @param id
     */
    public void delete(Long id);

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);

    /**
     * 分页
     *
     * @param current
     * @param size
     * @return
     */
    public IPage page(Long current, Long size);

    /**
     * 条件分页查询
     *
     * @param current
     * @param size
     * @param baseAttrInfo
     * @return
     */
    public IPage searchPage(Long current,
                            Long size,
                            BaseAttrInfo baseAttrInfo);
}
