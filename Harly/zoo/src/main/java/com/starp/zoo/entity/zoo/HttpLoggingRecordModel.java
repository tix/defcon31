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
 * httpLogging存s3的目录层级实体类.
 *
 * @author magic
 * @date 2021/9/26
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_http_logging_record")
public class HttpLoggingRecordModel extends EntityBase {

    @Column
    private String carrier;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "offer_id")
    private String offerId;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "pid")
    private String pid;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "step_number")
    private String stepNumber;

    @Column(name = "record_date")
    private String recordDate;
}
