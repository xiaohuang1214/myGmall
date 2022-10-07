package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 黄梁峰
 *
 * 平台属性值
 */
@Service
public class BaseAttrValueServiceImpl implements BaseAttrValueService {

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrValue getById(Long id) {
        if (id == null) {
            throw new RuntimeException("id不能为空");
        }
        return baseAttrValueMapper.selectById(id);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BaseAttrValue> getALl() {
        return baseAttrValueMapper.selectList(null);
    }

    /**
     * 新增
     *
     * @param baseAttrValue
     */
    @Override
    public void add(BaseAttrValue baseAttrValue) {
        if (StringUtils.isEmpty(baseAttrValue.getValueName())) {
            throw new RuntimeException("属性值名称不能为空");
        }
        int insert = baseAttrValueMapper.insert(baseAttrValue);
        if (insert <= 0) {
            throw new RuntimeException("新增失败");
        }
    }

    /**
     * 修改
     *
     * @param baseAttrValue
     */
    @Override
    public void update(BaseAttrValue baseAttrValue) {
        if (StringUtils.isEmpty(baseAttrValue.getValueName())) {
            throw new RuntimeException("属性值名称不能为空");
        }
        int i = baseAttrValueMapper.updateById(baseAttrValue);
        if (i < 0) {
            throw new RuntimeException("修改失败");
        }

    }

    /**
     * 根据Id删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("id不能为空");
        }
        int i = baseAttrValueMapper.deleteById(id);
        if (i < 0) {
            throw new RuntimeException("删除失败");
        }
    }

    /**
     * 条件查询
     *
     * @param baseAttrValue
     * @return
     */
    @Override
    public List<BaseAttrValue> search(BaseAttrValue baseAttrValue) {
        //判断参数是否为空
        if (baseAttrValue == null) {
            return baseAttrValueMapper.selectList(null);
        }
        //拼接条件
        LambdaQueryWrapper<BaseAttrValue> wrapper = getQueryWrapper(baseAttrValue);
        //返回结果
        return baseAttrValueMapper.selectList(wrapper);
    }


    /**
     * 分页
     *
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage page(Long current, Long size) {
        return baseAttrValueMapper.selectPage(new Page<>(current, size), null);
    }

    /**
     * 条件分页
     *
     * @param current
     * @param size
     * @param baseAttrValue
     * @return
     */
    @Override
    public IPage searchPage(Long current, Long size, BaseAttrValue baseAttrValue) {

        return baseAttrValueMapper.selectPage(new Page<>(current, size), getQueryWrapper(baseAttrValue));
    }

    /**
     * 条件拼接
     *
     * @param baseAttrValue
     * @return
     */
    private LambdaQueryWrapper<BaseAttrValue> getQueryWrapper(BaseAttrValue baseAttrValue) {
        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(baseAttrValue.getValueName())) {
            wrapper.like(BaseAttrValue::getValueName, baseAttrValue.getValueName());
        }
        if (baseAttrValue.getAttrId() != null) {
            wrapper.eq(BaseAttrValue::getAttrId, baseAttrValue.getAttrId());
        }
        return wrapper;
    }
}
