package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @Author David
 * @Date 18:10 2018/12/18
 * @param
 * @return
 **/
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name="t_script")
public class ScriptModel extends EntityBase{

    @Column(columnDefinition = "varchar(50)")
    private String name;

    @Column
    private String country;

    /**
     * 正则
     */
    @Column(columnDefinition = "text")
    private String regular;

    /**
     * 脚本
     */
    @Column(columnDefinition = "text")
    private String script;

    @Column(name = "event_type", columnDefinition = "int(1)")
    private Integer eventType;

    @Column(name = "msisdn_location", columnDefinition = "text")
    private String msisdnLocation;

    /**
     * PIN码前字段
     */
    @Column(name = "pin_front_code", columnDefinition = "varchar(10)")
    private String pinFrontCode;


    /**
     * PIN码位数
     */
    @Column(name = "pin_byte_count", columnDefinition = "int(11) default 0")
    private int pinByteCount;


    /**
     * 输入框位置
     */
    @Column(name = "pin_input_location", columnDefinition = "text")
    private String pinInputLocation;

    /**
     * 确认键脚本
     */
    @Column(name = "pin_confirm_location", columnDefinition = "text")
    private String pinConfirmLocation;

    /**
     * PIN请求脚本
     */
    @Column(name = "pin_btn_location", columnDefinition = "text")
    private String pinBtnLocation;

    @Column(name = "short_code")
    private String shortCode;

    @Column
    private String keyword;

    /**
     * 1: GP_手动  2： 线下_手动
     */
    @Column(columnDefinition = "int(1)")
    private Integer type;

    @Column(name = "pin_regular", columnDefinition = "text")
    private String pinRegular;

    @Column(name = "pin_page_regular", columnDefinition = "varchar(255)")
    private String pinPageRegular;
}
