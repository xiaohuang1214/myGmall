package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 黄梁峰
 * <p>
 * 平台属性
 */
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;


    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo getById(Long id) {
        return baseAttrInfoMapper.selectById(id);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAll() {
        return baseAttrInfoMapper.selectList(null);
    }

    /**
     * 新增
     *
     * @param baseAttrInfo
     */
    @Override
    public void add(BaseAttrInfo baseAttrInfo) {
        if (StringUtils.isEmpty(baseAttrInfo.getAttrName())) {
            throw new RuntimeException("新增失败,属性名称不能为空");
        }
        int id = baseAttrInfoMapper.insert(baseAttrInfo);
        if (id <= 0) {
            throw new RuntimeException("新增失败,请重试");
        }
    }

    /**
     * 修改
     *
     * @param baseAttrInfo
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        if (StringUtils.isEmpty(baseAttrInfo.getAttrName())) {
            throw new RuntimeException("修改失败,属性不能为空");
        }
        int id = baseAttrInfoMapper.updateById(baseAttrInfo);
        if (id < 0) {
            throw new RuntimeException("修改失败,请重试");
        }
    }

    /**
     * 根据Id删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        int i = baseAttrInfoMapper.deleteById(id);
        if (i < 0) {
            throw new RuntimeException("删除失败,请重试");
        }
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo == null){
            return baseAttrInfoMapper.selectList(null);
        }
        //条件判断
        LambdaQueryWrapper wrapper = queryParam(baseAttrInfo);
        //调用并返回结果
        return baseAttrInfoMapper.selectList(wrapper);
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
        return baseAttrInfoMapper.selectPage(new Page<>(current, size), null);
    }

    /**
     * 条件分页查询
     *
     * @param current
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @Override
    public IPage searchPage(Long current,
                            Long size,
                            BaseAttrInfo baseAttrInfo) {
        return baseAttrInfoMapper.selectPage(new Page<>(current, size), queryParam(baseAttrInfo));
    }


    /**
     * 公共的查询条件
     *
     * @param baseAttrInfo
     * @return
     */
    private LambdaQueryWrapper queryParam(BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        //拼接条件
        if (!StringUtils.isEmpty(baseAttrInfo.getId())) {
            wrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
        }
        if (!StringUtils.isEmpty(baseAttrInfo.getAttrName())) {
            wrapper.like(BaseAttrInfo::getAttrName, baseAttrInfo.getAttrName());
        }
        if (baseAttrInfo.getCategoryId() != null) {
            wrapper.eq(BaseAttrInfo::getCategoryId, baseAttrInfo.getCategoryId());
        }
        if (baseAttrInfo.getCategoryLevel() != null) {
            wrapper.eq(BaseAttrInfo::getCategoryLevel, baseAttrInfo.getCategoryLevel());
        }
        return wrapper;
    }

}
