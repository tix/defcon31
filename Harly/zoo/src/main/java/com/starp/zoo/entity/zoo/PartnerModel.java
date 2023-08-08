package com.starp.zoo.entity.zoo;

import com.alibaba.fastjson.annotation.JSONField;
import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;

/**
 * @author david
 * 广告主
 */

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_partner",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_partner_name",columnNames = {"partner_name"}),
                @UniqueConstraint(name = "unique_partner_id",columnNames = {"partner_id"})
        })
public class PartnerModel extends EntityBase {

    @Column(name="partner_name",columnDefinition = "varchar(100)")
    private String partnerName;

    @Column(name = "partner_id",columnDefinition = "varchar(10)")
    private String partnerId;

    @Column(name = "remarks")
    private String remarks;


    /**
     * 上游postback url
     */
    @Transient
    private String postBackUrl;

    @Transient
    private OfferModel offer;

}
