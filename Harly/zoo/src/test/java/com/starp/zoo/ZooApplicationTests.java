package com.starp.zoo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZooApplicationTests {

    @Test
    public void contextLoads() {
        String url = "http://localhost:8080/test?msisdn=18592070108";
        String param = "msisdn";
        Pattern pattern = Pattern.compile("(\\?|&+)"+ param +"=([^&]*)");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()){
            String msisdn = matcher.group(2);
            System.out.println(msisdn);
        }
    }

}
