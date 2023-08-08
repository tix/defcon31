package com.starp.zoo.common;
import com.alibaba.fastjson.JSON;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;

import java.util.*;


/**
 *
 * @Author vic
 * @Date 18:11 2018/12/18
 * @param
 * @return
 **/
public class ResponseInfoUtil {

    public static ResponseInfo success(Object obj){
        ResponseInfo msg = new ResponseInfo();
        msg.setCode(ResponseInfoEnum.SUCCESS.getCode());
        msg.setMsg(ResponseInfoEnum.SUCCESS.getMsg());
        msg.setData(obj);
        return msg;
    }

    public static ResponseInfo success(){
        return success(null);
    }

    public static ResponseInfo ok(Object obj){
        ResponseInfo msg = new ResponseInfo();
        msg.setCode(ResponseInfoEnum.OK.getCode());
        msg.setMsg(ResponseInfoEnum.OK.getMsg());
        msg.setData(obj);
        return msg;
    }

    public static ResponseInfo ok(){
        return ok(null);
    }

    public static ResponseInfo error(Integer code, String msg){
        ResponseInfo returnMsg = new ResponseInfo();
        returnMsg.setCode(code);
        returnMsg.setMsg(msg);
        return returnMsg;
    }

    public static ResponseInfo error(Object obj){
        ResponseInfo msg = new ResponseInfo();
        msg.setCode(ResponseInfoEnum.UNKNOWN_ERROR.getCode());
        msg.setMsg(ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
        msg.setData(obj);
        return msg;
    }

    public static ResponseInfo error(){
        ResponseInfo returnMsg = new ResponseInfo();
        returnMsg.setCode(ResponseInfoEnum.UNKNOWN_ERROR.getCode());
        returnMsg.setMsg(ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
        return returnMsg;
    }

    public static ResponseInfo wrong(Object obj){
        ResponseInfo returnMsg = new ResponseInfo();
        returnMsg.setCode(ResponseInfoEnum.BAD_REQUEST.getCode());
        returnMsg.setMsg(ResponseInfoEnum.BAD_REQUEST.getMsg());
        returnMsg.setData(obj);
        return returnMsg;
    }

    public static String errorMsg(String errorData, DesUtil desUtil) throws Exception {
        String returnMsg = desUtil.encode(errorData);
        return returnMsg;
    }

    public static byte[] errorMsgByte(String errorData, AesUtil desUtil, int encodeType) throws Exception {
        return desUtil.encode(errorData, encodeType);
    }

    public static byte[] successByte(String data, AesUtil desUtil, int encodeType) throws Exception {
        return desUtil.encode(data, encodeType);
    }

    public static String success(String data, DesUtil desUtil) throws Exception {
        String returnMsg = desUtil.encode(data);
        return returnMsg;
    }



    public static String errorMsgWithoutDesc(String errorData)throws Exception{
        return errorData;
    }

    public static String successWithoutDesc(String data) throws Exception {
        return data;
    }
}
