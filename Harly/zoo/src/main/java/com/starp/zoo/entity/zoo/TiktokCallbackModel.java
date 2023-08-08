package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author vic.zhao
 * Created by vic, DATE: 2020/03/27.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_tiktok_callback")
public class TiktokCallbackModel extends EntityBase{

    @Column(name = "app_id", columnDefinition = "varchar(255)")
    private String appId;

    @Column(name = "auth_code", columnDefinition = "varchar(255)")
    private String authCode;

    @Column(name = "advertiser_id", columnDefinition = "varchar(255)")
    private String advertiserId;

    @Column(name = "extra", columnDefinition = "varchar(255)")
    private String extra;
}
