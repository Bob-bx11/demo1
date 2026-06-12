package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /*
    * 新增菜品和对应的口味数据
    * */
    void saveWithFlavor(DishDTO dishDTO);

    /*
     * 菜品批量删除
     * */
    void deleteBatch(List<Long> ids);

    /*
    * 根据菜品 id 查询菜品和对应口味数据
    * */
    DishVO getByIdWithFlavor(Long id);

    /*
     * 根据 id 修改菜品信息
     * */
    void updateWithFlavor(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);
}

