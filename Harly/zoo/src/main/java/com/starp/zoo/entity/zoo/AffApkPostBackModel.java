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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_apk_postback")
public class AffApkPostBackModel extends EntityBase {

    @Column
    private String postbackUrl;

    @Column
    private String ip;

    @Column
    private String clickId;

    @Column
    private String appId;

    @Column
    private String mnc;

}
