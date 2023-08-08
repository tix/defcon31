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
 * t_tenjin_pb实体类.
 *
 * @author magic
 * @data 2022/4/14
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_tenjin_pb")
public class TenJinPbModel extends EntityBase {

    @Column(name = "app_id", columnDefinition = "varchar(36)")
    private String appId;

    @Column(name = "advertising_id", columnDefinition = "varchar(64)")
    private String advertisingId;

    @Column(name = "ip_address", columnDefinition = "varchar(100)")
    private String ipAddress;

    @Column(name = "country", columnDefinition = "varchar(16)")
    private String country;

    @Column(name = "campaign_name", columnDefinition = "varchar(16)")
    private String campaignName;

    @Column(name = "site_id", columnDefinition = "varchar(36)")
    private String siteId;

    @Column(name = "user_agent", columnDefinition = "longtext")
    private String userAgent;
}
