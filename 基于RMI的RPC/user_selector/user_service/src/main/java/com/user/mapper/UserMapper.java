package com.user.mapper;

import com.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select id,name,age,gender from tb_user where name like concat('%',#{name},'%')")
    List<User> selectByName(String name);
}
