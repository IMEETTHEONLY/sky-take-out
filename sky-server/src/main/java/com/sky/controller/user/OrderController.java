package com.sky.controller.user;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单,{}",ordersSubmitDTO);
        OrderSubmitVO orderVO=orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderVO);

    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }
    //订单详细查询
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("订单详细查询")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单详情:{}",id);
        OrderVO orderVO= orderService.getOrderDetailById(id);
        return Result.success(orderVO);
    }


    //用户催单
    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminder(@PathVariable Long id){
        log.info("用户催单的订单{}",id);
        orderService.reminder(id);
        return Result.success();
    }

    //用户历史订单
    @GetMapping("historyOrders")
    @ApiOperation("查询用户历史订单")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询历史订单:{}",ordersPageQueryDTO);
        PageResult pageResult=orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(pageResult);

    }


    /**
     * 再来一单
     *
     * @param id
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单:{}",id);
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * 取消订单
     */
@PutMapping("cancel/{id}")
@ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) throws Exception {
    log.info("取消订单:{}",id);
    OrdersCancelDTO ordersCancelDTO=new OrdersCancelDTO();
    ordersCancelDTO.setId(id);
    orderService.cancel(ordersCancelDTO);
    return Result.success();
}

}
