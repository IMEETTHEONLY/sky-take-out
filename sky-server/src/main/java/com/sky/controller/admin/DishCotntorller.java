package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishCotntorller {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        String key="dish_"+dishDTO.getCategoryId();
        log.info("新增菜品,{}",dishDTO);
        dishService.saveWithFlaver(dishDTO);
        cleanCache(key);
        return Result.success();
    }


    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishDTO){
        log.info("菜品分页查询,{}",dishDTO);
        PageResult pageResult= dishService.page(dishDTO);

        return Result.success(pageResult);
    }


    @DeleteMapping()
    @ApiOperation("菜品删除")
    public Result deleteByIds(@RequestParam List<Long> ids){
        log.info("删除菜品,{}",ids);

        dishService.deleteByIds(ids);

        //删除菜品牵扯到批量删除 直接删除全部即可
        cleanCache("dish*");
        return Result.success();
    }

    //根据id查询菜品
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> update(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
      DishVO dishVO= dishService.getByIdWithFlavers(id);
        return Result.success(dishVO);
    }

    //修改菜品
    @PutMapping()
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品<,{}",dishDTO);
        dishService.updateWithFlavers(dishDTO);
        //修改菜品可能会修改分类 直接全部删除
        cleanCache("dish*");
        return Result.success();
    }


    //起售停售
    @PostMapping("/status/{status}")
    @ApiOperation("起售/停售菜品")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("菜品:{}",status==1?"起售":"停售");
        dishService.startOrStop(status,id);
        //这里还需要查询 分类id 不如直接全删
        cleanCache("dish*");
        return Result.success();
    }


    private void cleanCache(String partner){
        Set keys = redisTemplate.keys(partner);
        //删除缓存
        redisTemplate.delete(keys);
    }



}
