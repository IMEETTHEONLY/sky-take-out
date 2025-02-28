package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMappper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    //增加购物车
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //1.先查询数据库里面有没有该加入的数据
        ShoppingCart shoppingCart=new ShoppingCart();
        //将dto的数据拷贝进cart对象并赋值用户id 查询数据库
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId= BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //条件查询 这里只可能返回null 或者是一条数据 因为同一条数据 占一行 只是number不同
        //根据userId和dishId,setmealId ,口味来查询  这四个任何一个不一样都一条新数据
        List<ShoppingCart> list = shoppingCartMappper.list(shoppingCart);


        //如果说有就将number数量+1
        if(list!=null&&list.size()>0){
            ShoppingCart cart=list.get(0);
            //获取原来的number
            Integer number = cart.getNumber();
            number++;
            //将新number赋值给cart然后更新数据库
            cart.setNumber(number);

            //更新数据库
            shoppingCartMappper.updateNumberById(cart);
        }
        //如果说没有 就新插入
        else{
            //插入新的数据

            //如果说dishId不为空(setmealId为空) 就则插入的是菜品数据
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            if(dishId!=null){
                //查询菜品的数据 封装在购物车当中
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());

            }
            else{
                //插入的是套餐数据
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());

            }

            //统一设置 初始数量 和创建时间
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            //将shoppingCart插入到数据库
            shoppingCartMappper.insert(shoppingCart);
        }
    }

    //查看购物车数据
    public List<ShoppingCart> showShoppingCart() {
        //构造shoppingcart进行查询
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .id(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> list=shoppingCartMappper.list(shoppingCart);
        return list;
    }

    //清空购物车
    public void cleanShoppingCart() {
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCartMappper.deleteByUserId(userId);
    }

    //减少购物车
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //先查询这条购物车的记录

        //获取用户id
        Long userId = BaseContext.getCurrentId();

        //将dto的数据拷贝到ShoppingCart
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置userid
        shoppingCart.setUserId(userId);


        //调用list查询 套餐/菜品
        //查询购物车的数据
        List<ShoppingCart> list = shoppingCartMappper.list(shoppingCart);
        //此时肯定有一条数据 获取出来即可
        ShoppingCart cart = list.get(0);

        //如果说此时的number大于1 则将number
        Integer number = cart.getNumber();
        if(number>1){
            //将数量减一 然后重新加入
            number--;
            cart.setNumber(number);
            shoppingCartMappper.updateNumberById(cart);
        }

        //此时的number=1就将这条记录删掉
        else{
            shoppingCartMappper.deleteById(cart);
        }
    }
}
