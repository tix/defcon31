package com.starp.zoo.util;

import org.springframework.util.StringUtils;

/**
 * @author charles
 */
public class MobileUtil {

    public static final String DE_MCC = "262";
    public static final String DE_START_1 = "+49";
    public static final String DE_START_2 = "0049";
    public static final String DE_START_3 = "49";

    public static final String GR_MCC = "202";
    public static final String GR_START_1 = "+30";
    public static final String GR_START_2 = "0030";
    public static final String GR_START_3 = "30";


    public static String getShortMobile(String mccmnc, String mobile) {
        if (StringUtils.isEmpty(mccmnc) || StringUtils.isEmpty(mobile)) {
            return mobile;
        }
        if (mccmnc.startsWith(DE_MCC)) {
            mobile = de(mobile);
        }
        if (mccmnc.startsWith(GR_MCC)) {
            mobile = gr(mobile);
        }
        return mobile;
    }

    private static String de(String mobile) {
        if (mobile.startsWith(DE_START_1)) {
            return mobile.substring(DE_START_1.length());
        }
        if (mobile.startsWith(DE_START_2)) {
            return mobile.substring(DE_START_2.length());
        }
        if (mobile.startsWith(DE_START_3)) {
            return mobile.substring(DE_START_3.length());
        }
        return mobile;
    }

    private static String gr(String mobile) {
        if (mobile.startsWith(GR_START_1)) {
            return mobile.substring(GR_START_1.length());
        }
        if (mobile.startsWith(GR_START_2)) {
            return mobile.substring(GR_START_2.length());
        }
        if (mobile.startsWith(GR_START_3)) {
            return mobile.substring(GR_START_3.length());
        }
        return mobile;
    }
}
