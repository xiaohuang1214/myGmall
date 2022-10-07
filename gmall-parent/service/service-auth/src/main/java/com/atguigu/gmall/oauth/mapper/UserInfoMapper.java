package com.atguigu.gmall.oauth.mapper;

import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 黄梁峰
 *
 * 存储用户的mapper
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
