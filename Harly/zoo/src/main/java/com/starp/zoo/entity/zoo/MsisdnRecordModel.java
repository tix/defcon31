package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author magic
 * @date 2021/10/29
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_msisdn_record")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class MsisdnRecordModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 11)
    private Integer identification;

    @Column(name="createtime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date(System.currentTimeMillis());

    @Column(unique = true)
    private String msisdn;

    private String result;

    @Column(name = "currrent_day_is_pull")
    private String currrentDayIsPull;

    private String r1;

    private String r2;

    private String r3;

    private String r4;

    private String r5;

    private String r6;

    private String r7;
}
