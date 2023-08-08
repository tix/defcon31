package com.starp.zoo.entity.zoo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_application_info",
        uniqueConstraints = {@UniqueConstraint(name = "unique_app_name", columnNames = {"app_name"})})
public class ApplicationModel extends EntityBase {

    @Column(name = "app_name", columnDefinition = "varchar(50)")
    private String appName;

    @Column(name = "package_name", columnDefinition = "varchar(80)")
    private String packageName;

    @Column(name = "product_type", columnDefinition = "varchar(36)")
    private String productType;

    @Column(columnDefinition = "varchar(100)")
    private String domain;

    @Column(columnDefinition = "varchar(255)")
    private String description;

    /**
     * 默认为在线， 0为下线
     */
    @Column(columnDefinition = "int(1) default 1")
    private int status=1;

    /**
     * 对于同一个用户app最大转化数
     */
    @Column(name="max_pull", columnDefinition = "int(11) default 0")
    private int maxPullNum;

    @Column(name = "log_status", columnDefinition = "int(1) default 0")
    private int logStatus;

    @Column(name = "aes_key")
    private String aesKey;

    /**
     * 广告开关  0是关闭， 1是开启
     */
    @Column(name = "ad_status", columnDefinition = "int(1) default 0")
    private Integer adStatus;

    @Column(name = "fbId")
    private String fbId;

    /**
     * sdk url
     */
    @Transient
    private JSONArray sdkUrl;


    @Transient
    private JSONArray epmAlarmOperator;

    @Transient
    private List<String> emails;

    @Transient
    private String s3Url;
}
