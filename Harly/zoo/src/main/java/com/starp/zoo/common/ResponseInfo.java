package com.starp.zoo.common;

import lombok.Data;

/**
 *
 * @Author vic
 * @Date 18:11 2018/12/18
 * @param
 * @return
 **/
@Data
public class ResponseInfo<T> {

    /** 错误码 */
    private Integer code;

    /** 提示的信息 */
    private String msg;

    /** 具体的内容 */
    private T data;

}
