package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    //保存新菜品
    @AutoFill(value = OperationType.INSERT)
    void save(Dish dish);
    //分页查询菜品
    Page<DishVO> page(DishPageQueryDTO dishDTO);

    //根据id查询
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    //根据id删除菜品
    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);
    //根据ids批量删除dish
    void deleteByIds(List<Long> ids);
    //修改菜品
    @AutoFill(OperationType.UPDATE)  //自动填充
    void update(Dish dish);

    //根据分类id和查询菜品数据
    List<Dish> list(Dish dish);
}
