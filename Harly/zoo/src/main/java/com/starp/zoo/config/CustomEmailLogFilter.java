package com.starp.zoo.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Charles
 * @date 2019/6/5
 * @description :
 */
public class CustomEmailLogFilter extends Filter<ILoggingEvent> {

    private static final String NULL_POINTER_EXCEPTION = "NullPointerException";
    public static final String NULL_STR = "null";
    private static final String COLON = ":";
    private static final List<String> ERRORS = new ArrayList<>();

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLevel() == Level.ERROR || event.getLevel() == Level.WARN) {
            IThrowableProxy throwable = event.getThrowableProxy();
            if (throwable != null && !StringUtils.isEmpty(throwable.getMessage())) {
                // 如果是异常为null或者是空指针
                if(NULL_STR.equalsIgnoreCase(throwable.getMessage()) || throwable.getMessage().contains(NULL_POINTER_EXCEPTION)) {
                    // 允许打印
                    return FilterReply.ACCEPT;
                } else {
                    String key = throwable.getClassName() + COLON + throwable.getMessage();
                    if (ERRORS.contains(key)) {
                        // 拒绝打印
                        return FilterReply.DENY;
                    }
                    ERRORS.add(key);
                }
            }
            // 允许打印
            return FilterReply.ACCEPT;
        }
        // 继续执行下一个 filter
        return FilterReply.NEUTRAL;
    }

}
