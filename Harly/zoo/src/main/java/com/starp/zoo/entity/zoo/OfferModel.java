package com.starp.zoo.entity.zoo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starp.zoo.entity.common.EntityBase;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "EI_EXPOSE_REP2"})
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_offer",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_offer_name", columnNames = {"offer_name"}),
                @UniqueConstraint(name = "unique_offer_id", columnNames = {"offer_id"})
        })
public class OfferModel extends EntityBase implements Cloneable {

    /**
     * 测试状态
     * 0 测试中 1 待分配 2 已完成
     */
    @Column(name = "test_status", columnDefinition = "int(1) default null")
    private Integer testStatus;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = true, insertable = false, updatable = false)
    @JSONField(serialize = false)
    @JsonIgnore
    private Set<OfferTagModel> offerTags;

    @Column(name = "updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime = new Date();

    public Date getUpdateTime() {
        if (null == this.updateTime) {
            return null;
        } else {
            return (Date) updateTime.clone();
        }
    }

    public void setUpdateTime(Date updateTime) {
        if (null == updateTime) {
            this.updateTime = null;
        } else {
            this.updateTime = (Date) updateTime.clone();
        }
    }

    @Column(name = "offer_name", columnDefinition = "varchar(100)")
    private String offerName;

    @Column(name = "offer_id", columnDefinition = "varchar(50)")
    private String offerId;

    @Column(columnDefinition = "varchar(10)")
    private String country;

    @Column(columnDefinition = "varchar(20)")
    private String operator;

    @Column(columnDefinition = "varchar(20)")
    private String partner;

    @Column(name = "partner_offer_id", columnDefinition = "varchar(50)")
    private String partnerOfferId;

    @Column(columnDefinition = "int(11) default 0")
    private int cap = 0;

    @Column(name = "max_pull", columnDefinition = "int(11) default null")
    private Integer maxPull;

    @Column(name = "app_cap", columnDefinition = "int(11) default null")
    private Integer appCap;

    /**
     * cap刷新时区
     */

    @Column(name = "reset_timezone", columnDefinition = "varchar(255) default 'GMT+0:00'")
    private String resetTimezone;

    /**
     * cap告警状态
     */
    @Column(name = "mail_cap_status", columnDefinition = "int(2)")
    private Integer mailCapStatus;


    /**
     * pay短码
     */
    @Column(name = "pay_shortCode", columnDefinition = "varchar(100)")
    private String payShortCode;


    /**
     * pay关键字
     */
    @Column(name = "pay_keyword", columnDefinition = "varchar(100)")
    private String payKeyword;

    /**
     * offer拉取时间段
     */
    @Column(columnDefinition = "varchar(30)")
    private String timeRange;

    /**
     * 区号
     */
    @Column
    private String areaCode;


    /**
     * 正则
     */
    @Column
    private String regex;


    /**
     * cap刷新时间
     */

    @Column(name = "reset_time", columnDefinition = "varchar(255) default '00:00:00'")
    private String resetTime;

    @Column(name = "is_crawl_html", columnDefinition = "int(1) default 0")
    private int isCrawlHtml = 0;

    /**
     * 0: 不限 1: 3G 2: WIFI
     */
    @Column(columnDefinition = "int(1) default 0")
    private int network = 0;

    /**
     * <option value="wap">WAP</option>
     * <option value="pin-wap">PIN WAP</option>
     * <option value="mo">MO</option>
     * <option value="pin-mo">PIN MO</option>
     * <option value="double-mo">DOUBLE MO</option>
     */
    @Column(columnDefinition = "varchar(10)")
    private String type;

    @Column(columnDefinition = "text")
    private String url;

    /**
     * 取头
     */
    @Column
    private Integer head;


    @Transient
    private List<String> jsList;

    @Transient
    private List<AutoScriptModel> autoScripts;

    @Transient
    private List<ScriptModel> scripts;

    /**
     * 任务持续时间
     */
    @Column(columnDefinition = "int(11) default 0")
    private int duration = 0;

    @Column(name = "pin_front_code", columnDefinition = "varchar(10)")
    private String pinFrontCode;

    @Column(name = "pin_byte_count", columnDefinition = "int(11)")
    private int pinByteCount = 0;

    @Column(name = "pin_btn_location", columnDefinition = "varchar(255)")
    private String pinBtnLocation;

    @Column(name = "pin_input_location", columnDefinition = "varchar(255)")
    private String pinInputLocation;

    @Column(name = "pin_confirm_location", columnDefinition = "varchar(255)")
    private String pinConfirmLocation;

    @Column(name = "pin_success_keyword", columnDefinition = "varchar(255)")
    private String pinSuccessKeyword;

    @Column(name = "short_code", columnDefinition = "varchar(30)")
    private String shortCode;

    @Column(columnDefinition = "varchar(30)")
    private String keyword;

    @Column(name = "confirm_keyword", columnDefinition = "varchar(30)")
    private String confirmKeyword;

    /**
     * double mo 间隔时间
     */
    @Column(name = "interval_time", columnDefinition = "int(11) default 0")
    private int intervalTime = 0;


    @Column(name = "new_offer", columnDefinition = "varchar(10)")
    private String isNewOffer;


    @Column(name = "payout", columnDefinition = "double default 0")
    private Double payout;


    /**
     * 破解类型 1.利刃；2.战斧；3.風の剣
     */
    @Column(name = "crackType", columnDefinition = "int default 1")
    private Integer crackType;

    /**
     * 回调类型
     * 1 支持透传
     * 2 不支持透传但有clickId
     * 3 不支持透传且无法区分（例如 IE）
     */
    @Column(name = "callback_type", columnDefinition = "int(1) default 1")
    private Integer callbackType;

    /**
     * 点击唯一id 对应的参数名称
     * 当 callbackType == 1 只允许一个
     * 当 callbackType == 2 需要利用逗号隔开
     */
    @Column(name = "click_id_param", columnDefinition = "varchar(255) default null")
    private String clickIdParam;

    /**
     * 扩展可用的透传参数 for callbackType == 1
     */
    @Column(name = "extend_param", columnDefinition = "varchar(255) default null")
    private String extendParam;

    /**
     * 上游offerId的参数
     */
    @Column(name = "partner_offer_id_param", columnDefinition = "varchar(255) default null")
    private String partnerOfferIdParam;

    /**
     *
     */
    @Column(name = "init_epm", columnDefinition = "double default 0")
    private Double initEpm;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    /**
     * offer 栈
     */
    @Column
    private String stack;

    @Column(name = "status", columnDefinition = "int(1) default 1")
    private int status;

    /**
     * 自动开启状态
     */
    @Column(name = "auto_status", columnDefinition = "int(1) default 1")
    private int autoStatus;

    /**
     * 自动开启
     */
    @Column(name = "auto_start", columnDefinition = "int(1) default null")
    private Integer autoStart;

    /**
     * 自动结束
     */
    @Column(name = "auto_end", columnDefinition = "int(1) default null")
    private Integer autoEnd;


    @Column(name = "email", columnDefinition = "varchar(500) default null")
    private String email;

    @Column(name = "alarm_status", columnDefinition = "int(1) default 0")
    private Integer alarmStatus;

    /**
     * 判断时取转化的时间区间(单位：分钟)
     * 默认 1 小时
     */
    @Column(name = "trans_range", columnDefinition = "int(11) default 0")
    private Integer transRange;

    /**
     * 告警阈值
     */
    @Column(name = "alarm_threshold", columnDefinition = "int(11) default 0")
    private Integer alarmThreshold;

    @Column(name = "alarm_threshold_check", columnDefinition = "int(1) default 0")
    private boolean alarmThresholdCheck;

    @Column(name = "is_log", columnDefinition = "int(1) default 0")
    private boolean isLog;


    /**
     * CR告警
     */
    @Column(name = "mail_mo_cr_status", columnDefinition = "int(2) default 0")
    private Integer mailMoCrStatus;

    /**
     * CR阈值
     */
    @Column(name = "mo_cr_threshold", columnDefinition = "double default 0")
    private Double moCrThreshold;

    /**
     * CR点击阈值
     */
    @Column(name = "mo_click_threshold", columnDefinition = "int(11) default 0")
    private Integer moClickThreshold;

    /**
     * mo 保护状态
     */
    @Column(name = "mail_mo_protect_status", columnDefinition = "int(2) default 0")
    private Integer mailMoProtectStatus;

    /**
     * 保护范围
     */
    @Column(name = "mo_protect_scope")
    private String moProtectScope;

    /**
     * mo保护 天数
     */
    @Column(name = "mo_protect_day", columnDefinition = "double default 0")
    private Double moProtectDay;

    /**
     * 拉取失败的code
     */
    private int errorCode;

    /**
     * 拉取失败的message
     */
    private String errorMsg;

    /**
     * 归属
     */
    @Column
    private String belong;


    /**
     * offer关闭时间
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Column(name = "closeTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeTime = new Date();


    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    private List<OfferTagModel> otherTags;

    @Override
    public Object clone() {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
