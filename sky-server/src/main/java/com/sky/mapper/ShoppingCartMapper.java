package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    //条件查询
    List<ShoppingCart> list(ShoppingCart shoppingCart);
    //根据购物车的id 更新number
    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart cart);
    @Insert("INSERT INTO shopping_cart " +
            "(id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "VALUES " +
            "(#{id}, #{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    //清空购物车
    @Delete("delete  from shopping_cart where user_id=#{userId}")
    void deleteByUserId(Long userId);
    //根据购物车的id删除购物车数据
    @Delete("delete  from shopping_cart where id=#{id}")
    void deleteById(ShoppingCart cart);
    //批量插入到购物车
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
