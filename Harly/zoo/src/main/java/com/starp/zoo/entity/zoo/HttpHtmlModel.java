package com.starp.zoo.entity.zoo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author  vic.zhao, DATE: 2098/9/27.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_http_html")
public class HttpHtmlModel {

    @Id
    @Column(length = 80)
    private String identification;

    @Column(name="createtime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date(System.currentTimeMillis());

    @Column(name = "step_name")
    private String stepName;

    @Column
    private String html;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    @Column(name= "header",columnDefinition = "TEXT")
    private String header;

    @Column(name = "next_url", columnDefinition = "TEXT")
    private String nextUrl;

    @Column(name = "form_data", columnDefinition = "TEXT")
    private String formData;

    @Column(name = "next_content_type")
    private String nextContentType;

    public Date getCreateTime() {
        return (Date)this.createTime.clone();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = (Date)createTime.clone();
    }

    @Column
    private String operator;

    @Column
    private String param1;

    @Column
    private String param2;

    @Column
    private String param3;

    @Column
    private String param4;

    @Column
    private String param5;

    @Column
    private String param6;

    @Column
    private String param7;


}
