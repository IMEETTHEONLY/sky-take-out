package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlaverMapper {
    //批量保存
    void saveBatch(List<DishFlavor> flavors);

    //根据dishId删除口味儿数据
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteByDishId(Long dishId);

    //根据dishId批量删除口味儿
    void deleteByDishIds(List<Long> dishIds);

    //根据dishId查询口味儿数据
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
