package com.starp.zoo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author charles
 */
@Data
public class AppEventVo {

    private Date clientTime;

    private String aff;

    private String pid;

    private String operator;

    private String page;

    private String url;

    private String step;

    private String offerId;

    private String appId;

    private String userId;

    public Date getClientTime() {
        if (clientTime == null) {
            return null;
        }
        return (Date) clientTime.clone();
    }

    public void setClientTime(Date clientTime) {
        if (clientTime != null) {
            this.clientTime = (Date) clientTime.clone();
        } else {
            this.clientTime = null;
        }
    }
}
