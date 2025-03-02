package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    //根据id查询用户
    @Select("select * from user where id=#{userId}")

    User getById(Long userId);

    //根据map条件查询用户
    Integer getByMap(Map map);
}
