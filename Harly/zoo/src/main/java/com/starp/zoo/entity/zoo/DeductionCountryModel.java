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
 * @author david
 */
@Getter
@Setter
@Table(name = "t_deduction")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class DeductionCountryModel extends EntityBase {

    @Column
    private String country;

    @Column
    private String time;

    @Column
    private double deduction;
}
