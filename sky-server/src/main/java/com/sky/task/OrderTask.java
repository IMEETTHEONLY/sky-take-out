package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
//    @Scheduled(cron = "0/5 * * * * *")
//    public void executeTask(){
//        log.info("定时任务开始:{}", LocalDateTime.now());
//    }

    @Autowired
    private OrderMapper orderMapper;

    //处理订单15分钟后不付款的订单 自动取消
    @Scheduled(cron = "0 * * * * ? ")  //每分钟执行
    public void dealTimeoutOrder(){
        log.info("处理超时的订单:{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);  //当前时间-15
        //订单状态为未支付 并且以当前时间15分钟之前的全部订单
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLF(Orders.PENDING_PAYMENT, time);//根据状态和小于某个时间来查询

        for (Orders order : orders) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("订单超时，自动取消");
            orderMapper.update(order);  //更新订单
        }
    }
    //处理商家不点击完成 自动完成
    @Scheduled(cron = "0 0 1 * * ? ")  //每天凌晨1点自动触发
    public void dealDeliveryOrder(){
        log.info("定时处理派送中的订单{}",LocalDateTime.now());
        //每天1点 减去1个小时 就是前一天整个工作日
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        //查询状态为派送中时间是前一个工作日以前的所有订单
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLF(Orders.DELIVERY_IN_PROGRESS, time);

        for (Orders order : orders) {
            //设置已完成
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        }
    }
}
