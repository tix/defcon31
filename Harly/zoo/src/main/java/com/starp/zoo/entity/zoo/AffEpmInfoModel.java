package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author Charles
 * @date 2019/1/22
 * @description :
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_epm", uniqueConstraints = {
        @UniqueConstraint(name = "unique_app_id_offer_id_calculate_hour", columnNames = {"resource_id", "offer_id", "calculate_hour"}),
})
public class AffEpmInfoModel extends EntityBase implements Cloneable {
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = true, insertable = false, updatable = false)
    private Set<OfferTagModel> offerTags;

    @Transient
    private String date;

    @Transient
    private int offerStatus;

    /**
     * 1: APP 2: 线下, 3: APP 与 线下
     */
    @Column(name = "resource_type", columnDefinition = "int(1)")
    private Integer resourceType;

    @Column(name = "resource_name", columnDefinition = "varchar(255) default null")
    private String resourceName;

    @Column(name = "resource_id", columnDefinition = "varchar(36) default null")
    private String resourceId;

    @Column(name = "offer_name", columnDefinition = "varchar(255) default null")
    private String offerName;

    @Column(name = "offer_id", columnDefinition = "varchar(36) default null")
    private String offerId;

    @Transient
    private String systemOfferId;

    @Transient
    private String partnerOfferId;

    @Transient
    private Long cap;

    @Transient
    private Long transRemainder;

    @Transient
    private List<String> tags;

    @Column(name = "click_num", columnDefinition = "int(11) default 0")
    private Long clickNum;

    @Column(name = "trans_num", columnDefinition = "int(11) default 0")
    private Long transNum;

    /**
     * 短码关键字转化
     */
    @Column(name = "mo_num",columnDefinition = "int(11) default 0")
    private Long moNum;

    @Column(name = "app_trans_num", columnDefinition = "int(11) default 0")
    private Long appTransNum;

    @Column(name = "revenue", columnDefinition = "double default 0")
    private Double revenue;

    @Column(name = "epm", columnDefinition = "double default 0")
    private Double epm;

    @Column(name = "country", columnDefinition = "varchar(10) default null")
    private String country;

    @Column(name = "operator", columnDefinition = "varchar(50) default null")
    private String operator;

    @Column(name = "partner", columnDefinition = "varchar(255) default null")
    private String partner;

    @Column(name = "msg", columnDefinition = "varchar(255) default null")
    private String msg;

    @Column(name = "calculate_hour", columnDefinition = "varchar(15)")
    private String calculateHour;

    /**
     * 归属
     */
    @Column
    private String belong;

    @Override
    public Object clone() {
        Object obj = null;
        try {
            obj =  super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
