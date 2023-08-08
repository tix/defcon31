package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Vic on 2020/9/21
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_mnc_permission")
public class MncPermissionModel extends EntityBase {
    private String country;
    private String type;
    private String mnc;
    private String areaCode;
    private String regex;
    private String url;
}
