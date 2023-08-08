package com.starp.zoo.entity.payment;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Charles, DATE: 2018/9/11.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_short_code")
public class ShortCodeModel extends EntityBase {

    /**
     * 通道ID, 1001,1002,1003,1004
     */
    @Column
    private Integer channelId;


    /**
     * 国家
     */
    @Column
    private String country;

    /**
     * 运营商
     */
    @Column
    private String operator;

    /**
     * 运营商编码
     */
    @Column
    private String plmn;

    /**
     * 指令，订阅短信的内容
     */
    @Column
    private String command;

    /**
     * 同步指令，扣费短信的内容
     */
    @Column
    private String syncCommand;

    /**
     * 短码，发送短信的号码
     */
    @Column
    private String shortCode;


    /**
     * 货币单位
     */
    @Column
    private String monetaryUnit;

    /**
     * 单价
     */
    @Column
    private float unitPrice;

    /**
     * 税金
     */
    @Column
    private float tax;

    /**
     * 结算率
     */
    @Column
    private float accountingRate;


    /**
     * 表示合作方 playPay,BShark
     */
    @Column
    private String partner;

    /**
     * 付费情况 RM1 per message,7 times per week.
     */
    @Column
    private String payInfo;

    /**
     * 运营商名字 Maxis、DiGi、Celcom
     */
    @Column
    private String operatorNames;

    /**
     * 热线  03-2727-2733 (9am-6pm/Mon-Fri).
     */
    @Column
    private String hotline;

    /**
     * 短信内容 STOP SPW
     */
    @Column
    private String smsInfo;


    /**
     * 条款的模版ID
     */
    @Column
    private String termsTemplateId;

    /**
     * 条款模板名称
     */
    @Transient
    private String termsTemplateName;

    /**
     * 欢迎短信的模版ID
     */
    @Column
    private String welcomeMtTemplateId;

    /**
     * 扣费MT的模版ID
     */
    @Column
    private String feeMtTemplateId;

    /**
     * 正常：0，暂停：1，备份：2
     */
    @Column(columnDefinition="varchar(255) default '0'",nullable=false)
    private String status;

    /**
     * 用户组(0:gkt, 1:ads)
     */
    @Column(columnDefinition="varchar(5) default '0'")
    private String subordinate;

    /**
     * 多任务ID
     */
    @Column
    private String mutiTaskManyId;

    /**
     *  cap
     */
    @Column
    private Integer cap = 0;

    @Column
    private String contact;

    @Column
    private String email;

    /**
     * DN统计成功告警 0:未设置 1:启用   2:关闭
     */
    @Column
    private String dnMailStatus;

    /**
     * dn告警成功浮动范围
     */
    @Column
    private String dnFloat;

    /**
     * dn 扣费周期
     */
    @Column
    private String chargeCycle;


    /**
     * dn 成功率(与前一天对比),起始为0
     */
    @Column(columnDefinition="double(20) default '0.00'")
    private Double dnSuccessRate;


    /**
     * dn 告警开始时间
     */
    @Column
    private String dnMailTime;

    /**
     * 抄送邮箱
     */
    @Column
    private String ccEmail;


    /**
     * 每N天收到DN
     */
    @Column
    private Integer dnFrequency;

    /**
     * 类型,sub(订阅) 跟 iod(点播)
     */
    @Column
    private String type;

    /**
     * 外部邮箱
     */
    @Column
    private String outEmail;

    /**
     * 是否关联zoo, 0为未关联  1为关联
     */
    @Column
    private Integer associateZoo;

}
