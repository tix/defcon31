package com.starp.zoo.controller;


import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.util.DesUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author david
 */
@RestController
public class DecideDescController {

    private static final String KEY_14 = "20200414";


    /**
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/mt/zoo/encode")
    public String encodeResponse(@RequestBody String param) throws Exception {
        String encodeStr = encodeDesc(param);
        return encodeStr;
    }


    @RequestMapping(value = "/mt/zoo/decode")
    public String decodeResponse(@RequestBody String param) throws Exception {
        String decodeStr = decodeDesc(param);
        return decodeStr;
    }



    public static String encodeDesc(String data) throws Exception {
        DesUtil desUtil =  new DesUtil(KEY_14, KEY_14);
        String encodeStr = desUtil.encode(data);
        return encodeStr;
    }

    public static String decodeDesc(String data) throws Exception {
        DesUtil desUtil =  new DesUtil(KEY_14, KEY_14);
        String encodeStr = desUtil.decode(data);
        return encodeStr;
    }

}
