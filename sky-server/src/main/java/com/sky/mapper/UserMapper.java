package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    /*
    根据 openid 查询用户数据
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /*
    插入数据
     */
    void insert(User user);
}