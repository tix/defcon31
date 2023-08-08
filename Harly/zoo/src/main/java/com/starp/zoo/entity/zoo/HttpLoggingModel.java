package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author  Charles, DATE: 2018/9/29.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_http_logging")
public class HttpLoggingModel extends EntityBase {

    @Column(name = "app_id")
    private String appId;

    @Column(name = "offer_id")
    private String offerId;

    @Column(name = "pid")
    private String pid;

    @Column(name = "user_id")
    private String userId;

    @Column
    private String carrier;

    @Column
    private String imsi;

    @Column
    private String ip;

    @Column(name="client_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date clientTime;

    @Column
    private String method;

    /**
     * 0:request, 1:response
     */
    @Column
    private String type;

    @Column
    private String url;

    @Column
    private String realUrl;

    @Column
    private String formData;

    @Column
    private String protocol;

    @Column
    private String headers;

    @Column
    private String body;

    /**
     * request : 1
     * response: 2
     */
    @Column
    private Integer status;

    @Column
    private String pin;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "step_id")
    private String stepId;

    public void setClientTime(Date clientTime) {
        this.clientTime = (Date)clientTime.clone();
    }

    public Date getClientTime() {
        return (Date)this.clientTime.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpLoggingModel)) {
            return false;
        }

        HttpLoggingModel that = (HttpLoggingModel) o;
        return Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getIp(), that.getIp()) &&
                Objects.equals(getClientTime(), that.getClientTime()) &&
                Objects.equals(getMethod(), that.getMethod()) &&
                Objects.equals(getType(), that.getType()) &&
                Objects.equals(getUrl(), that.getUrl()) &&
                Objects.equals(getRealUrl(), that.getRealUrl()) &&
                Objects.equals(getFormData(), that.getFormData()) &&
                Objects.equals(getProtocol(), that.getProtocol()) &&
                Objects.equals(getHeaders(), that.getHeaders()) &&
                Objects.equals(getBody(), that.getBody()) &&
                Objects.equals(getStatus(), that.getStatus())&&
                Objects.equals(getPin(), that.getPin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getIp(), getClientTime(), getMethod(), getType(), getUrl(), getRealUrl(),
                getFormData(), getProtocol(), getHeaders(), getBody(), getStatus(), getPin());
    }
}
