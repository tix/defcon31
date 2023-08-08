package com.starp.zoo.constant;

/**
 * @author Charles
 */

public enum LogNameEnum {
    /**
     * 打点日志
     */
    APP_EVENT("appEventLog"),

    /**
     * epmListLog 日志
     */
    EPM_LIST("epmListLog");

    private String logName;

    LogNameEnum(String logName) {
        this.logName = logName;
    }

    public String getLogName() {
        return logName;
    }
}
