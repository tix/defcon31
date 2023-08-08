package com.starp.zoo.common.constant;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author Vic on 2020/5/22
 */
public class RedisConstants {
    public static final String PIN = "pin";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public static String NAMESPACE_HTTP = "http:";
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public static String NAMESPACE_REFERER = "referer:";
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public static String NAMESPACE_URL_REGXS = "urlRegx";
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public static String NAMESPACE_REFERER_URL_REGXS = "refererUrlRegx";
}

