package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     */
    public TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end) {

        ArrayList<LocalDate> dateArrayList = new ArrayList<>();

        //循环去加直到加到最后一天
        dateArrayList.add(begin);
        //当前最后一天已经加入集合了 就结束
        while (!begin.equals(end)) {
            //begin一天一天的去加
            begin = begin.plusDays(1);
            dateArrayList.add(begin);
        }
        //将集合转为字符串设置给vo
        String s1 = StringUtils.join(dateArrayList, ",");
        //遍历时间集合 去查询每天的营业额
        List<Double> sumList = new ArrayList<>();  //存放金额的列表
        for (LocalDate date : dateArrayList) {
            //将date转换为数据库对应的格式
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//当天的最早时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //当天的最晚时间

            //将查询条件封装到map集合当中 进行条件查询(这里也可以用对象 或者是直接传递过去)
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);  //查询状态为已完成的

            //调用mapper进行查询 再加入结果集合
            Double d = reportMapper.getSumByMap(map);
            //如果说查出来为空就设置为0
            d = d == null ? 0 : d;
            //加入集合
            sumList.add(d);
        }
        //将集合转为字符串设置给vo
        String s2 = StringUtils.join(sumList, ",");

        TurnoverReportVO reportVO = new TurnoverReportVO();
        reportVO.setDateList(s1);
        reportVO.setTurnoverList(s2);

        //返回vo对象
        return reportVO;
    }

    /**
     * 用户统计
     */
    public UserReportVO getuserStatistics(LocalDate begin, LocalDate end) {
        UserReportVO reportVO = new UserReportVO();
        //首先将时间段计算出来
        ArrayList<LocalDate> dateArrayList = new ArrayList<>();

        //循环去加直到加到最后一天
        dateArrayList.add(begin);
        //当前最后一天已经加入集合了 就结束
        while (!begin.equals(end)) {
            //begin一天一天的去加
            begin = begin.plusDays(1);
            dateArrayList.add(begin);
        }
        //将集合转为字符串设置给vo
        String s1 = StringUtils.join(dateArrayList, ",");

        //初始化两个集合
        List<Integer> totalUser = new ArrayList<>();
        List<Integer> newUser = new ArrayList<>();


        for (LocalDate date : dateArrayList) {
            //1.统计截至目前时间的总用户

            //将date转换为数据库对应的格式
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//当天的最早时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //当天的最晚时间

            //将查询条件封装到map集合当中 进行条件查询(这里也可以用对象 或者是直接传递过去)
            Map map = new HashMap();
            map.put("end", endTime);
            Integer total = userMapper.getByMap(map);
            total = total == null ? 0 : total;
            totalUser.add(total);


            //2.统计截至今日的新用户  加上一个大于今日的条件 就是今日新用户
            map.put("begin", beginTime);
            Integer u = userMapper.getByMap(map);
            u = u == null ? 0 : u;
            newUser.add(u);

        }

        String s2 = StringUtils.join(totalUser, ",");
        String s3 = StringUtils.join(newUser, ",");

        reportVO.setDateList(s1);
        reportVO.setTotalUserList(s2);
        reportVO.setNewUserList(s3);


        //返回vo
        return reportVO;

    }

    /**
     * 订单统计
     */
    public OrderReportVO getordersStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO reportVO = new OrderReportVO();

        //首先将时间段计算出来
        ArrayList<LocalDate> dateArrayList = new ArrayList<>();

        //循环去加直到加到最后一天
        dateArrayList.add(begin);
        //当前最后一天已经加入集合了 就结束
        while (!begin.equals(end)) {
            //begin一天一天的去加
            begin = begin.plusDays(1);
            dateArrayList.add(begin);
        }
        //将集合转为字符串设置给vo
        String s1 = StringUtils.join(dateArrayList, ",");


        //创建订单集合 和 有效订单集合
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();


        for (LocalDate date : dateArrayList) {
            //将date转换为数据库对应的格式
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//当天的最早时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //当天的最晚时间

            //1.查询每日订单数
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);

            Integer order=orderMapper.getByMap(map);
            orderCountList.add(order);

            //2.查询每日有效订单数
            map.put("status",Orders.COMPLETED);
            Integer validOrder=orderMapper.getByMap(map);
            validOrderCountList.add(validOrder);

        }
        //将集合转为字符串设置给vo
        String s2 = StringUtils.join(orderCountList, ",");
        String s3= StringUtils.join(validOrderCountList, ",");

        //订单总数 有效订单数 订单完成率
        int totalOrder=0,validOrder=0;
        for (Integer i : orderCountList) {
            totalOrder+=i;
        }
        for (Integer i : validOrderCountList) {
            validOrder+=i;
        }

        Double rate= Double.valueOf(100*validOrder/totalOrder);
        rate=rate/100.0;  //这里/100是因为前端要的是小数 然后会乘以100

        return OrderReportVO.builder()
                .dateList(s1)
                .orderCountList(s2)
                .validOrderCountList(s3)
                .totalOrderCount(totalOrder)
                .validOrderCount(validOrder)
                .orderCompletionRate(rate)
                .build();

    }

    /**
     * 销量top10
     */
    public SalesTop10ReportVO gettop10(LocalDate begin, LocalDate end) {
        //将date转换为数据库对应的格式
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//最早时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);  //最晚时间

        Map map=new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);

        //联表查询在时间范围内的根据菜品分组前十条菜品的名字和数量封装到GoodsSalesDTO
        List<GoodsSalesDTO> dishList=orderMapper.getTop10(map);
        //获取name 字符串
        List<String> names = dishList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String s1 = StringUtils.join(names, ",");

        //获取数量字符串
        List<Integer> numbers = dishList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String s2 = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(s1)
                .numberList(s2)
                .build();
    }

}
