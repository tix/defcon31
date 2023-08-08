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
 * AddressMsisdnModel.
 *
 * @author magic
 * @data 2022/4/21
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_address_msisdn")
public class AddressMsisdnModel extends EntityBase {

    @Column(name = "operator", columnDefinition = "varchar(36)")
    private String operator;

    @Column(name = "msisdn", columnDefinition = "varchar(36)")
    private String msisdn;
}
