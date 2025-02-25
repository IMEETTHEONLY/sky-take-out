package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlaverMapper;
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
    @Autowired
    private DishFlaverMapper dishFlaverMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishFlaverMapper dishFlavorMapper;

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

    @Transactional
    //菜品分页查询
    public PageResult page(DishPageQueryDTO dishDTO) {
        //用插件进行查询
        PageHelper.startPage(dishDTO.getPage(),dishDTO.getPageSize());

        //调用mapper进行查询  用vo接收因为有categoryId字段
        Page<DishVO> result=dishMapper.page(dishDTO);

        return new PageResult(result.getTotal(),result.getResult());
    }

    //根据菜品id删除菜品
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //判断菜品是否是起售状态，起售不能被删除
        for (Long id : ids) {
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //判断菜品是否被关联套餐 被关联的菜品不能被删除
        List<Long> setmealIds=setmealDishMapper.getByIds(ids);
        if(setmealIds!=null&&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        //根据id删除菜品
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//        }
//
//        //删除菜品对应的口味儿
//        for (Long id : ids) {
//            dishFlaverMapper.deleteByDishId(id);
//        }


        //根据ids删除菜品
        dishMapper.deleteByIds(ids);
        //删除ids对应的口味儿
        dishFlaverMapper.deleteByDishIds(ids);
    }

    //修改菜品基本信息和对应的口味儿数据
    @Transactional
    public void updateWithFlavers(DishDTO dishDTO) {
        //修改菜品的基本信息
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.update(dish);

        //修改口味儿数据的时候，采取先删后插


        //根据菜品id删除口味儿数据
        dishFlaverMapper.deleteByDishId(dishDTO.getId());

        //批量插入新的口味儿数据
        List<DishFlavor> flavors = dishDTO.getFlavors(); //口味儿表
        //判断口味是否含有数据
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //这里是插入多条
            dishFlaverMapper.saveBatch(flavors);
        }

    }


    //查询菜品以及对应的口味儿数据
    public DishVO getByIdWithFlavers(Long id) {
        //查询菜品信息
        Dish dish = dishMapper.getById(id);
        //查询口味儿
        List<DishFlavor> flavors = dishFlaverMapper.getByDishId(id);
        //封装到vo返回
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);

        //返回
        return dishVO;
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

}
