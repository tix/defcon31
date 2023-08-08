package com.starp.zoo.common;

/**
 *
 * @Author Vic
 * @Date 18:08 2018/12/18
 * @param
 * @return
 **/
public class BadRequestException extends Exception{
    public BadRequestException(){
        super();
    }
    
    public BadRequestException(String message) {
        super(message);
    }
}
