package com.starp.zoo.entity.zoo;

import com.alibaba.fastjson.annotation.JSONField;
import com.starp.zoo.entity.common.EntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author charles
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity
@Table(name = "t_offer_step_temp",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_name",columnNames = {"step_name"})
        })
public class OfferStepTempModel extends EntityBase {

    /**
     * 步骤名称
     */
    @Column(name = "step_name")
    private String stepName;

    /**
     * 第几步
     */
    @Column(name = "step_index",columnDefinition = "int(10) NOT NULL")
    private int stepIndex;

    /**
     * url 匹配正则
     */
    @Column
    private String regex;


    /**
     * 下一步正则
     */
    @Column
    private String nextRegex;

    /**
     * 1: redirect 2: html
     */
    @Column
    private int type;

    /**
     * redirect 时执行脚本
     */
    @Column(name = "script", columnDefinition = "TEXT default null")
    private String script;

    /**
     * 是否为 pin 流程
     */
    @Column(name = "is_pin")
    private boolean isPin;

    /**
     * 是否提交表单(需要后台解析)
     */
    @Column(name = "is_commit_form")
    private boolean isCommitForm;

    /**
     * 下一步请求方法 js
     */
    @Column(name = "method", columnDefinition = "varchar(50) default 'GET'")
    private String method;


    @Column(name = "stayTime")
    private Integer stayTime;


    /**
     * pin码正则
     */
    @Column(name = "pin_regex")
    private String pinRegex;

    /**
     * 是否保存本页面
     */
    @Column(name = "is_save")
    private boolean isSave;

    /**
     * 历史页面
     */
    @Column(name = "origin_page")
    private String originPage;

    /**
     * 1.有锁  2.未加锁
     */
    @Column
    private String lockStatus;


    /**
     *  历史页面
     */
    @Column(name = "origin_page2")
    private String originPage2;

    /**
     * 历史页面
     */
    @Column(name = "referer_step")
    private String refererStep;

    /**
     * 破解类型
     */
    @Column
    private Integer crackType;

    /**
     * 是否拉取脚本
     */
    @Column
    private Integer pullScript;

    @Transient
    private List<String> offerIds;
}
