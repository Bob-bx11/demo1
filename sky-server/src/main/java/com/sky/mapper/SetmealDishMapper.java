package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /*
    * 根据菜品 id 查询对应套餐 id
    * */
    List<Long> getSetmealIdesByDishId(List<Long> ids);


}
