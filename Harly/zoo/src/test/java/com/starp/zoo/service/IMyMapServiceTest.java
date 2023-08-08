package com.starp.zoo.service;

import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.EncodeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;

public class IMyMapServiceTest {

    @Autowired
    OfferRepo offerRepo;

    public static void main(String[] args) throws Exception {
        String url = "www.pay.flygemobi.com/test.html";
        String encodeUrl = encodeURL(url);
        System.out.println("return encodeUr==="+encodeUrl);
    }

    private static String encodeURL(String url) throws Exception {
        String returnURL = EncodeUtil.encode(url);
        return returnURL;
    }

    @Test
    public void test() {
//        Date begin = DateUtil.getTimeByTimeZone((DateUtil.getHourByTimeZoneDay(new Date(), ZooConstant.GMT_8) + " 00:00:00"), ZooConstant.GMT_0);
//        Date end = DateUtil.getTimeByTimeZone((DateUtil.getHourByTimeZoneDay(new Date(), ZooConstant.GMT_8) + " 23:59:59"), ZooConstant.GMT_0);
//        System.out.println();
        String dayTime = DateUtil.formatDay(DateUtil.getDateByTimeZone(System.currentTimeMillis(), ZooConstant.GMT_8));
        String begin = DateUtil.foreDay(dayTime) + "-16";
        String end = dayTime + "-15";
        System.out.println();
    }

}
