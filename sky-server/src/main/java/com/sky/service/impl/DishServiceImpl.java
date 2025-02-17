package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlaverMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlaverMapper dishFlaverMapper;

    //保存菜品以及对应的口味儿
    @Transactional  //这里要操作两个表，所以说要加上事务
    public void saveWithFlaver(DishDTO dishDTO) {

        Dish dish=new Dish();
        //将dto的属性拷贝到dish当中
        BeanUtils.copyProperties(dishDTO,dish);


        dishMapper.save(dish); //插入一条菜品数据

        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors(); //口味儿表
        //判断口味是否含有数据
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            //这里是插入多条
            dishFlaverMapper.saveBatch(flavors);
        }



    }
}
