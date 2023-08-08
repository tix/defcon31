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
 * @author Vic on 2020/7/20
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_oss_user")
public class OssUserModel extends EntityBase {
    @Column(name = "platform")
    private String platform;

    @Column(name = "inner_width")
    private String innerWidth;

    @Column(name = "inner_height")
    private String innerHeight;

    @Column(name = "avail_width")
    private String availWidth;

    @Column(name = "avail_height")
    private String availHeight;

    @Column(name = "build_id")
    private String buildID;

    @Column(name = "cookie_enabled")
    private String cookieEnabled;

    @Column(name = "cookie_length")
    private String cookieLength;

    @Column(name = "orientation")
    private String orientation;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "tch")
    private String tch;

    @Column(name = "plugins")
    private String plugins;

    @Column(name = "oscpu")
    private String oscpu;

    @Column(name = "productSub")
    private String productSub;

    @Column(name = "connection_type")
    private String connectionType;

    @Column(name = "languages")
    private String languages;

    @Column(name = "rtt")
    private String rtt;

    @Column(name = "colorDepth")
    private String colorDepth;

    @Column(name = "pixelDepth")
    private String pixelDepth;

    @Column(name = "dpr")
    private String dpr;

    @Column(name = "downlink")
    private String downlink;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "ssu")
    private String ssu;

    @Column(name = "hpe")
    private String hpe;

    @Column(name = "raf")
    private String raf;

    @Column(name = "mem")
    private String mem;

    @Column(name = "open_database")
    private String openDatabase;

    @Column(name = "has_focus")
    private String hasFocus;

    @Column(name = "wglv")
    private String wglv;

    @Column(name = "npermission")
    private String nPermission;

    @Column(name = "serviceWorker")
    private String serviceWorker;

    @Column(name = "webdriver")
    private String webdriver;

    @Column(name = "user_activation")
    private String userActivation;
}
