package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_deduct",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_smart_link_id_offer_id", columnNames = {"smart_link_id", "offer_id"})
    }
)
public class DeductConfigModel extends EntityBase {

    @Column(name = "smart_link_id", columnDefinition = "varchar(36) NOT NULL")
    private String smartLinkId;

    @Column(name = "offer_id", columnDefinition = "varchar(36) NOT NULL")
    private String offerId;

    @Column(name = "deduct", columnDefinition = "int(11) default 0")
    private int deduct;

    @Column
    private Float payout;

    @Transient
    private String offerName;

    @Transient
    private String tagId;

    @Transient
    private String group;

}
