package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     */
    TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end);
    /**
     * 用户统计
     */
    UserReportVO getuserStatistics(LocalDate begin, LocalDate end);
    /**
     * 订单统计
     */
    OrderReportVO getordersStatistics(LocalDate begin, LocalDate end);
    /**
     * 销量top10
     */
    SalesTop10ReportVO gettop10(LocalDate begin, LocalDate end);
}
