package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    //增加购物车
    void add(ShoppingCartDTO shoppingCartDTO);
    //查看购物车数据
    List<ShoppingCart> showShoppingCart();
    //清空购物车
    void cleanShoppingCart();
    //减少购物车
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
