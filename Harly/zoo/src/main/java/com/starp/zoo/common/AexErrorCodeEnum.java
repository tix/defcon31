package com.starp.zoo.common;

import lombok.Getter;

/**
 * @author david
 */
@Getter
public enum AexErrorCodeEnum {

    /**
     * 解密KEY错误
     */
    DECODE_ENCRYPT_ERROR(500101,"DECODE ERROR , ENCRYPT KEY ERROR"),

    /**
     * 解密错误
     */
    DECODE_ERROR(5001,"DECODE ERROR"),


    /**
     * 运营商不匹配错误
     */
    OPERATOR_MATCH_ERROR(5002," OPERATOR MATCH ERROR"),


    /**
     * redis 中未包含该offer的破解步骤
     */
    OFFER_STEP_NULL(5003," NOT MATCH OFFER CRACK STEP"),

    /**
     * url 未匹配offer正则
     */
    OFFER_URL_NOT_MATCH(5004," URL NOT MATCH REGEX"),

    /**
     * 利刃该国家下的JS库为空
     */
    SWORD_COUNTRY_JS_NULL(5005," SWORD COUNTRY NOT MATCH JS"),


    /**
     * 利刃URL 未匹配JS
     */
    SWORD_URL_NOT_MATCH(5006," SWORD URL NOT MATCH JS"),

    /**
     * 战斧执行JS 错误
     */
    AEX_HANDLE_JS_ERROR(5007," AEX HANDLE JS ERROR"),


    /**
     * 战斧执行JS结果为空
     */
    AEX_HANDLE_JS_RESPONSE_NULL(5008," AEX HANDEL JS RESPONSE NULL"),


    /**
     * ReadPage JS 执行错误
     */
    READ_PAGE_HANDLE_JS_ERROR(5009," READPAGE HANDLE JS ERROR"),


    /**
     * ReadPage JS执行为空
     */
    READ_PAGE_HANDLE_JS_RESPONSE_NULL(5010," READPAGE HANDLE JS RESPONSE NULL"),


    /**
     * SavePage Nodejs执行错误
     */
    SAVE_PAGE_HANDLE_NODEJS_ERROR(5011," SAVE PAGE HANDLE NODE JS ERROR"),


    /**
     * SavePage JS执行错误
     */
    SAVE_PAGE_HANDLE_JS_ERROR(5012," SAVE PAGE HANDLE JS ERROR"),


    /**
     * SavePage JS执行结果为空
     */
    SAVE_PAGE_HANDLE_JS_RESPONSE_NULL(5013," HANDLE JS RESPONSE NULL"),


    /**
     * 加密内容为空
     */
    ENCODE_CONTENT_NULL(5014," ENCODE CONTENT NULL"),


    /**
     * 战斧offer step 的JS 为空
     */
    AEX_OFFER_STEP_SCRIPT_NULL(5015," AEX OFFER STEP SCRIPT NULL"),

    /**
     * 执行JS 错误
     */
    HANDLE_JS_ERROR(5016," HANDLE JS ERROR"),

    /**
     * offer step script 为空
     */
    OFFER_STEP_SCRIPT_NULL(5017," OFFER STEP SCRIPT NULL");


    private Integer code;

    private String msg;

    AexErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
