package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入菜品口味
     * @param flavors 口味集合
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应口味数据
     * @param dishId 菜品id
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteDishById(Long dishId);

    /**
     * 根据菜品id集合批量删除口味数据
     * @param ids 菜品id集合
     */
    void deleteDishByIds(List<Long> ids);

    /**
     * 根据菜品id查询对应口味数据
     * @param id 菜品id
     * @return 口味集合
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long id);


}