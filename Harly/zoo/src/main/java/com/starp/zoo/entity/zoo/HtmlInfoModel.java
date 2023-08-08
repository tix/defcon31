package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/5/16.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_html_info")
public class HtmlInfoModel extends EntityBase {
    
    @Column(name = "app_id", columnDefinition = "varchar(36)")
    private String appId;
    
    @Column(name = "app_name", columnDefinition = "varchar(50)")
    private String appName;
    
    @Column(name = "offer_id", columnDefinition = "varchar(36)")
    private String offerId;
    
    @Column(name = "offer_name", columnDefinition = "varchar(100)")
    private String offerName;
    
    @Column(name = "origin_url", columnDefinition = "text")
    private String originUrl;
    
    @Column(name = "user_id", columnDefinition = "varchar(36)")
    private String userId;
    
    /**
     * s3 存储地址
     */
    @Column(name = "save_url")
    private String saveUrl;
    
    @Transient
    private String source;


}
