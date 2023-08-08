package com.starp.zoo.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author Charles
 * @date 2019/6/5
 * @description :
 */
public class CustomLogFilter extends Filter<ILoggingEvent> {

    private static final String JSM_LISTENER_LOGGER = "org.springframework.jms.listener.DefaultMessageListenerContainer";
    private static final String QUEUE_CONSUMER_CLASS_NAME = "Consumer";

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLevel() == Level.ERROR || event.getLevel() == Level.WARN) {
            if(JSM_LISTENER_LOGGER.equals(event.getLoggerName())
                    && event.getThrowableProxy().getMessage().contains(QUEUE_CONSUMER_CLASS_NAME)) {
                // 拒绝打印
                return FilterReply.DENY;
            }
            // 允许打印
            return FilterReply.ACCEPT;
        }
        // 继续执行下一个 filter
        return FilterReply.NEUTRAL;
    }

}
