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
@Table(name = "t_affiliate", uniqueConstraints={
        @UniqueConstraint(name = "unique_name", columnNames = {"name"})
})
public class AffiliateModel extends EntityBase {

    @Column(name = "name", columnDefinition = "varchar(255) default null")
    private String name;

    @Column(name = "url", columnDefinition = "text")
    private String url;

    @Column(name = "offer_id_param", columnDefinition = "varchar(50) default null")
    private String offerIdParam;

    @Column(name = "click_id_param", columnDefinition = "varchar(50) default null")
    private String clickIdParam;

    @Column(name = "extend_param", columnDefinition = "varchar(50) default null")
    private String extendParam;

    @Column(name = "payout_param", columnDefinition = "varchar(50) default null")
    private String payoutParam;
}
