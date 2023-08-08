package com.starp.zoo.entity.zoo;


import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/7/3.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name="t_app_user_event")
public class AppUserEventModel extends EntityBase {

    @Column(name = "device_id", columnDefinition = "varchar(50)")
    private String deviceId;

    @Column(name = "user_id", columnDefinition = "varchar(36)")
    private String userId;

    @Column(name = "event_code", columnDefinition = "int(11)")
    private Integer eventCode;

    @Column(name = "event_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime = new Date(System.currentTimeMillis());

    @Column(name = "app_id", columnDefinition = "varchar(36)")
    private String appId;

    @Column(name = "app_name", columnDefinition = "varchar(36)")
    private String appName;

    @Column(name = "app_description", columnDefinition = "varchar(36)")
    private String appDescription;

    @Column(columnDefinition = "text")
    private String param1;

    @Column(columnDefinition = "text")
    private String param2;

    @Column(columnDefinition = "varchar(1000)")
    private String param3;

    @Column(columnDefinition = "varchar(1000)")
    private String param4;

    @Column(columnDefinition = "varchar(1000)")
    private String param5;

    @Column(name = "mnc", columnDefinition = "varchar(20)")
    private String mnc;

    @Column
    private String operator;

    @Column(name = "offer_id")
    private String offerId;

    @Column(name = "offer_name")
    private String offerName;

    @Column(name = "system_offer_id")
    private String systemOfferId;

    @Column(name = "partner")
    private String partner;

    @Column(name = "partner_offer_id")
    private String partnerOfferId;

    /**
     * 栈
     */
    @Column
    private String stacks;

    /**
     * 组
     */
    @Column
    private String groups;

    /**
     * 标签
     */
    @Column(name = "other_tags")
    private String otherTags;

    public AppUserEventModel() {
    }

    public AppUserEventModel(String deviceId, String userId, Integer eventCode, Date eventTime, String appId,
                             String param1, String param2, String param3, String param4, String param5,
                             String mnc, String operator) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.eventCode = eventCode;
        if(null == eventTime){
            this.eventTime = null;
        }else {
            this.eventTime = (Date) eventTime.clone();
        }
        this.appId = appId;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.mnc = mnc;
        this.operator = operator;
    }

    public Date getEventTime() {
        if(null == this.eventTime){
            return null;
        }
        return (Date) eventTime.clone();
    }

    public void setEventTime(Date eventTime) {
        if(null == eventTime){
            this.eventTime = null;
        }else {
            this.eventTime = (Date) eventTime.clone();
        }
    }



    @Override
    public String toString(){
        return  (this.getCreateTime() != null ? this.getCreateTime().getTime() : "") + "^,^" +
                this.deviceId + "^,^" +
                this.userId + "^,^" +
                this.appId + "^,^" +
                this.appName  + "^,^" +
                this.appDescription + "^,^" +
                this.eventCode + "^,^" +
                (this.eventTime != null ? this.eventTime.getTime() : "") + "^,^" +
                this.param1 + "^,^" +
                this.param2 + "^,^" +
                this.param3 + "^,^" +
                this.param4 + "^,^" +
                this.param5 + "^,^" +
                this.mnc + "^,^" +
                this.operator + "^,^" +
                this.offerId + "^,^" +
                this.offerName + "^,^" +
                this.systemOfferId + "^,^" +
                this.partner + "^,^" +
                this.partnerOfferId + "^,^" +
                this.stacks + "^,^" +
                this.groups + "^,^" +
                this.otherTags;
    }
}
