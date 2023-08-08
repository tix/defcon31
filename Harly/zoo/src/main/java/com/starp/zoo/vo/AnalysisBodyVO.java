package com.starp.zoo.vo;

import lombok.Data;

/**
 * @author Vic on 2019/9/12
 */
@Data
public class AnalysisBodyVO {
    private String url;

    private String formData;

    private String hs;

    private String cks;
    /**
     * pin 码正则
     */
    private String pinRegx;

    private String method;

    /**
     * 1.利刃   2.战斧
     */
    private Integer type;


    /**
     * 类型为利刃且需要JS 的时候返回脚本
     */
    private String crackScript;


    /**
     * 下一步正则
     */
    private String nextRegex;


    /**
     * 停留时间
     */
    private Integer stayTime;

    /**
     * 步骤时间
     */
    private Integer stepTime;

    /**
     * 四位参数   1.顺序 2.优先级 3.回传 4.匹配
     */
    private Integer aio;

    /**
     * 间隔时间
     */
    private Integer intervals;


    /**
     * 时间戳
     */
    private Long ttl;

    /**
     * 是否保存cookie 1.为保存 0.为不保存
     */
    private Integer saveCookie;

    /**
     * vodacom保存cookie
     */
    private String setCookie;

}
