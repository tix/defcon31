package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_sub_count",
        uniqueConstraints = {@UniqueConstraint(name = "unique_offer_id", columnNames = {"offer_id"})})
public class SubCountModel extends EntityBase {
    
    @Column(name = "offer_id", columnDefinition = "varchar(50)")
    private String offerId;
    
    @Column(columnDefinition = "int(11) default 0")
    private int count=0;
}
