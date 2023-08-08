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
 * @date 2018/12/12
 * @description :
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_postback_ie")
public class AffPostBackIeModel extends EntityBase {

    @Column(name = "short_code", columnDefinition = "varchar(20)")
    private String shortCode;

    @Column(columnDefinition = "varchar(20)")
    private String keyword;

    @Column(columnDefinition = "varchar(20)")
    private String msisdn;

    @Column(columnDefinition = "varchar(10)")
    private String telcoid;
}
