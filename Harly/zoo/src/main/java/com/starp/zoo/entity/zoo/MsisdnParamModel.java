package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author Charles
 * @date 2019/5/6
 * @description :
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_msisdn_param")
public class MsisdnParamModel extends EntityBase {

    private String country;

    private String operator;

    private String params;

    private String htmlRegular;

    private String msisdnRegOne;

    private String msisdnRegTwo;

    @Transient
    private List<String> paramArr;
}
