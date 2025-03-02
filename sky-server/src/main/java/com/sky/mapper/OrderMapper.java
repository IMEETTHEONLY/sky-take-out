package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    //插入订单数据
    void insert(Orders orders);

    @Select("select * from orders where status=#{status} and order_time<#{time}")
    List<Orders> getByStatusAndOrderTimeLF(int status, LocalDateTime time);




    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    //根据id查询订单
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    //分页查询历史数据
    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);
    //根究map来条件查询
    Integer getByMap(Map map);
   //查询前十条
    List<GoodsSalesDTO> getTop10(Map map);
}
