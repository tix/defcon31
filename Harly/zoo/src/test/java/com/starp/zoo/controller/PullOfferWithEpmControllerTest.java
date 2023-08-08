package com.starp.zoo.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Charles
 * @date 2019/1/21
 * @description :
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PullOfferWithEpmControllerTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void removeRedisList() {
        stringRedisTemplate.opsForList().remove("list-test", 0,  "aaa");
    }
}