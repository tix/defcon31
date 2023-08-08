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
 * @author Charles
 * @date 2019/6/6
 * @description : 渠道转化记录
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_channel_trans")
public class ChannelTransModel extends EntityBase {

    @Column(name = "channel_id", columnDefinition = "varchar(36) NOT NULL")
    private String channelId;

    @Column(name = "channel_Name", columnDefinition = "varchar(36) NOT NULL")
    private String channelName;

    @Column(name = "click_id", columnDefinition = "varchar(500) default null")
    private String clickId;

    @Column(name ="partner", columnDefinition = "varchar(50) default null")
    private String partner;

    @Column(name ="offer_id", columnDefinition = "varchar(255) default null")
    private String systemOfferId;

    @Column(name ="partner_offer_id", columnDefinition = "varchar(255) default null")
    private String partnerOfferId;

    @Column(name = "payout", columnDefinition = "float default 0")
    private Float payout;

    /**
     * 下游的点击
     */
    @Column
    private String cid;

    @Column
    private String extend;

    @Column(name = "request_url", columnDefinition = "text")
    private String requestUrl;

    @Column(name = "response", columnDefinition = "text")
    private String response;

    @Column(name = "response_status")
    private Integer responseStatus;

}
