package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    // 👇 新增：注入口味Mapper
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        Long total = dishMapper.countByCondition(dishPageQueryDTO);

        int start = (dishPageQueryDTO.getPage() - 1) * dishPageQueryDTO.getPageSize();
        int pageSize = dishPageQueryDTO.getPageSize();

        List<Dish> list = dishMapper.pageQuery(dishPageQueryDTO, start, pageSize);

        return new PageResult(total, list);
    }

    /**
     * 新增菜品和对应的口味（事务控制）
     * @param dishDTO
     */
    @Override
    @Transactional //事务注解
    public void saveWithFlavor(DishDTO dishDTO) {
        // 1. 菜品主表（使用切面自动填充）
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish); // @AutoFill 自动填充，@Options 自动回填主键 id

        // 2. 关键：获取回填后的菜品 ID
        Long dishId = dish.getId();
        if (dishId == null) {
            throw new RuntimeException("菜品保存失败，主键未回填");
        }

        // 3. 口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId); // 这里必须用回填后的 dishId
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional //事务注解
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够被删除 -- 是否存在起售中的菜品
        for (Long id : ids) {
            if (dishMapper.getById(id).getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够被删除 -- 是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdesByDishId(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //当前菜品被套餐关联,无法删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        //删除菜品表中的菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteDishById(id);
//        }

        //删除多条数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteDishByIds(ids);

    }

    /*
     * 根据菜品 id 查询菜品和对应口味数据
     * */
    @Transactional //事务注解
    public DishVO getByIdWithFlavor(Long id) {
        //根据 id 查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品 id 查询口味数据
        List<DishFlavor> dishflavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装到 VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishflavors);

        return dishVO;
    }

    /*
     * 根据 id 修改菜品信息
     * */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 1. 将 DTO 转为 Dish 实体，更新菜品主表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 菜品主表更新：@AutoFill 会自动更新 updateTime、updateUser
        dishMapper.update(dish);

        // 2. 先删除该菜品原有的所有口味数据（避免重复/冗余）
        dishFlavorMapper.deleteDishById(dishDTO.getId());

        // 3. 再批量插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                // 关联菜品 id
                flavor.setDishId(dishDTO.getId());

                // 如果你的 DishFlavor 表有公共字段，需要手动填充（否则可以省略）
                // flavor.setCreateTime(LocalDateTime.now());
                // flavor.setUpdateTime(LocalDateTime.now());
                // flavor.setCreateUser(BaseContext.getCurrentId());
                // flavor.setUpdateUser(BaseContext.getCurrentId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品启售/停售
     */
    @Override
    @Transactional
    public void startOrStop(Integer status, Long id) {
        // 1. 构建更新对象
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);

        // 2. 更新状态（@AutoFill 会自动更新 updateTime、updateUser）
        dishMapper.update(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}