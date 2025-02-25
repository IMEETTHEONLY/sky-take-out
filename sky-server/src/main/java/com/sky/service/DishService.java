package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    //保存菜品以及对应的口味儿
    void saveWithFlaver(DishDTO dishDTO);

    //菜品分页查询
    PageResult page(DishPageQueryDTO dishDTO);

    //根据菜品id删除菜品
    void deleteByIds(List<Long> ids);

    //修改菜品基本信息和对应的口味儿数据
    void updateWithFlavers(DishDTO dishDTO);

    DishVO getByIdWithFlavers(Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

}
