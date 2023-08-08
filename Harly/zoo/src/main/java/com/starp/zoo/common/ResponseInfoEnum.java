package com.starp.zoo.common;

import lombok.Getter;

/**
 *
 * @Author vic
 * @Date 18:11 2018/12/18
 * @param
 * @return
 **/
@Getter
public enum ResponseInfoEnum {

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(-1,"error"),
    /**
     * 成功 200，success
     */
    SUCCESS(200,"success"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "system error"),
    /**
     * 成功 200，ok
     */
    OK(200,"OK"),
    /**
     * 未找到链接
     */
    NOT_FOUND(404,"OK"),
    /**
     * 错误请求
     */
    BAD_REQUEST(400,"OK"),

    /**
     * mnc 错误
     */
    MNC_ERROR(333,"mnc mismatch"),
    /**
     * 获取不到script
     */
    SCRIPT_ERROR(334,"script mismatch"),
    /**
     * url 为空
     */
    URL_ERROR(335,"url is null"),
    /**
     * VALUE is null or empty
     */
    VALUE_ERROR(336,"The all params are null or empty"),
    /**
     * APP_EVENT_MSG
     */
    APP_EVENT_MSG(337,"app event msg :"),
    /**
     * OFFERID_NULL
     */
    OFFERID_NULL(338,"offerId is null"),
    /**
     * OFFERID_NULL
     */
    APPID_NULL(339,"appId is null"),
    /**
     * MNC_NULL
     */
    MNC_NULL(340,"mnc is null"),
    /**
     * APP_NULL
     */
    APP_NULL(341,"app does not exist"),
    /**
     * APP_CLOSE
     */
    APP_CLOSE(342,"app is closed"),
    /**
     * TOKEN_NULL
     */
    TOKEN_NULL(343,"token is null"),


    /**
     * 拉取offer ,appId 为空
     */
    NULL_APPID(411,"AppId is null"),


    /**
     * 拉取offer时，app是否开启
     */
    CLOSE_APP(412,"App is closed"),


    /**
     * App拉取次数超过最大转化次数
     */
    OVER_APP_MAX_TRANS_NUM(413,"App over max trans number"),


    /**
     * 没有可用的offer
     */
    NULL_ENABLE_OFFER(414,"have no enable offer"),

    /**
     * 没有可用的测试offer
     */
    NULL_ENABLE_TEST_OFFER(445,"have no enable auto-test-offer"),


    /**
     * offer拉取次数超过最大允许拉取次数
     */
    OVER_OFFER_MAX_PULL_NUM(415,"offer over max pull number"),


    /**
     * 已转化用户一周之内再次拉取到
     */
    TRANSFER_USER_PULL_WEEK(416,"transfer user pull offer in week"),


    /**
     * 一个用户一天之内拉到相同的offer
     */
    ONE_USER_PUFF_OFFER_PER_DAY(417,"one user pull same offer per day"),


    /**
     * 该用户已经拉取到该offer
     */
    ONE_USER_PUFF_OFFER_IN_USERLIST(418,"one user have pull offer in user list"),


    /**
     * 该用户已经拉取到任务栈下的offer
     */
    ONE_USER_PUFF_OFFER_IN_USERTAG(419,"one user have pull offer in user tag"),


    /**
     * offer超cap
     */
    OFFER_OVER_CAP(420,"offer over cap"),


    /**
     * 24小时内转化的offer
     */
    TRANSFER_OFFER_PER_DAY(421,"transfer offer in 24 hour"),


    /**
     * offer不在可跑时间范围段内
     */
    OFFER_IN_PULL_TIME(422,"offer not in pull time"),


    /**
     * 拉取offer 的请求为空
     */
    NULL_OFFER_REQUEST(423,"null request about pull offer"),


    /**
     * redis中未找到破解战斧的step
     */
    NULL_ALEX_STEP_REDIS(424,"alex crack have no step in redis"),


    /**
     * 战斧破解执行redirect step未空
     *
     */
    NULL_ALEX_REDIRECT_STEP(425,"alex crack execute redirect error"),


    /**
     * 战斧破解执行html为空
     *
     */
    NULL_ALEX_HTML_STEP(426,"alex crack execute html null"),


    /**
     * 战斧破解转化页面完成未配置
     *
     */
    NULL_ALEX_TRANSFER_PAGE(427,"haven't find transfer page config"),


    /**
     * 战斧执行破解方案请求体为空
     */
    NULL_ALEX_REQUEST_BODY(428,"alex crack request body is null"),


    /**
     * 战斧URL 未匹配
     */
    VERIFY_PIN_NOT_MATCH_URL(429,"verify  not match pin url"),


    /**
     * DESC解密错误
     */
    DESC_DECODE_ERROR(430,"desc decode body error"),


    /**
     * 执行JS 出错
     */
    HANDLE_JS_ERROR(431,"handle js error"),


    /**
     * response 跟PIN 都为空
     */
    HANDLE_JS_NULL_RESPONSE_NULL_PIN(432,"handle js response and pin are null"),


    /**
     * 执行JS 为空
     */
    HANDLE_JS_NULL(433,"handle js is null"),


    /**
     * 执行JS 返回结果为空
     */
    HANDLE_JS_RETURN_NULL(434,"handle js return null"),


    /**
     * 解析JS 返回结果报错
     */
    PARSE_JS_RETURN_ERROR(435,"parse js return error"),


    /**
     * 解析JS 返回结果报错
     */
    OPTICKSSECURITY_ERROR(436, "optickssecurity error"),


    /**
     * 解析JS 返回结果报错
     */
    OFFER_CLOSE_ERROR(437, "offer close"),


    /**
     * 需要添加了新的offer，EPM列表发生改变需要重新添加新的epm列表
     */
    OFFER_REPULL_ERROR(438,"repull offer"),


    /**
     * App拉取次数超过最大转化次数
     */
    OVER_APP_MAX_TRANS_OPERATOR_NUM(440,"App over max trans number by operator"),


    /**
     * App拉取次数超过最大转化次数
     */
    PROTECTED_OFFER_SEVEN_DAY(441,"protected offer seven day"),



    /**
     * App拉取次数超过最大转化次数
     */
    PROTECTED_OFFER_THIRTY_DAY(442,"protected offer thirty day"),


    /**
     * 上传Event 内容为空
     */
    SEND_EVENT_CODE_NULL(443,"send post body is null"),
    /**
     * 释放智能栈报错
     */
    FREE_STACK_ERROR( 444, "free stack error"),


    /**
     * offer不属于该运营商
     */
    OFFER_NOT_MATCH_OPERATOR( 439, "offer not match operator");





    private Integer code;

    private String msg;

    ResponseInfoEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
