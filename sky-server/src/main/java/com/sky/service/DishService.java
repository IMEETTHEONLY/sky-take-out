package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {
    //保存菜品以及对应的口味儿
    void saveWithFlaver(DishDTO dishDTO);
}
