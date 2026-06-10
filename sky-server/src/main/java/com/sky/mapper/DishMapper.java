package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.controller.admin.ShopController;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 条件查询菜品总数
     * @param dto
     * @return
     */
    Long countByCondition(@Param("dto") DishPageQueryDTO dto);

    /**
     * 分页查询菜品列表
     * @param dto
     * @param start
     * @param pageSize
     * @return
     */
    List<Dish> pageQuery(
            @Param("dto") DishPageQueryDTO dto,
            @Param("start") int start,
            @Param("pageSize") int pageSize
    );

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /*
    * 根据主键查询数据
    * */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /*
    * 根据菜品 id 删除菜品数据
    * */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /*
    * 删除多条菜品数据
    * */
    void deleteByIds(List<Long> ids);

    // 更新菜品主表
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
}

