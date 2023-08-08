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
 * @author starp
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_postback")
public class AffPostBackModel extends EntityBase {

    @Column(name = "click_id", columnDefinition = "varchar(500) default null")
    private String clickId;

    @Column(name ="partner", columnDefinition = "varchar(50) default null")
    private String partner;

    @Column(name = "payout", columnDefinition = "float default 0")
    private Float payout;

    /**
     * conversion Ip
     */
    @Column(name = "ip")
    private String ip;

    /**
     * country of conversion Ip
     */
    @Column(name = "country")
    private String country;

    @Column(name ="offer_id", columnDefinition = "varchar(255) default null")
    private String systemOfferId;

    @Column(name ="partner_offer_id", columnDefinition = "varchar(255) default null")
    private String partnerOfferId;

    /**
     * 1: 为app, 2:为 affiliate
     */
    @Column(name = "resource_type", columnDefinition = "int(1) default null")
    private int resourceType;

    @Column(name ="resource_id", columnDefinition = "varchar(255) default null")
    private String resourceId;

}
