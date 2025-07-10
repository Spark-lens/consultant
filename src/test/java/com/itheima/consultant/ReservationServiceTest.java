package com.itheima.consultant;

import com.itheima.consultant.pojo.Reservation;
import com.itheima.consultant.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * Copyright (C), 2021, 北京同创永益科技发展有限公司
 *
 * @author YueMing
 * @version 3.0.0
 * @description
 * @date $ $
 */

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    // 测试添加
    @Test
    public void testInsert(){
        Reservation reservation = new Reservation(null, "张三", "男", "13800000001", LocalDateTime.now(), "北京", 5);
        reservationService.insert(reservation);
    }


    // 测试查询
    @Test
    public void testFindByPhone(){
        String phone = "13800000001";
        Reservation reservation = reservationService.findByPhone(phone);
        System.out.println(reservation);
    }

}
