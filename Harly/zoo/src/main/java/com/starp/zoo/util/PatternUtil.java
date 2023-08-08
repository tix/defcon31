package com.starp.zoo.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.regex.Pattern;

/**
 * @author Charles
 * @description :
 */
public class PatternUtil {

    public static final Pattern URL_PARAMS_PATTERN = Pattern.compile("(\\?|&+)(.+?)=([^&]*)");

    public static final Pattern FLOAT_PAT = Pattern.compile("^[0-9]+(.[0-9]*(f|F)?$)?");

    public static final Pattern IT_WIND_STEP01_URL_PATTERN =  Pattern.compile("./*(linkPage\\s*=\\s*\")(\\S+)(\";*)");
    public static final Pattern TH_TRUEMOVE_AFF_NETWORK01 = Pattern.compile("./*(window\\.location\\.href\\s*=\\s*\")(.+)(\";*)");
    public static final Pattern TH_TRUEMOVE_AFF_NETWORK02 = Pattern.compile("./*(\"msisdn\":)(/*)(,\"oper\"*)");
    public static final Pattern TH_TRUEMOVE_AFF_NETWORK04 = Pattern.compile(".*(calladp.*\"\\s*,\\s*\")(\\S*)(\"\\);)");
    public static final Pattern TH_TRUEMOVE_AFF_REQ = Pattern.compile("reqId:(\\s*')(\\S+)('\\s*,)");
    public static final Pattern TH_TRUEMOVE_AFF_REF = Pattern.compile("refId:(\\s*')(\\S+)('\\s*,)");
    public static final Pattern TH_TRUEMOVE_AFF_3_DATA = Pattern.compile("var myinfor_data = '([^;]*)");
    public static final Pattern TH_TRUEMOVE_AFF_3_REPLACE = Pattern.compile("window\\.location\\.replace\\(\"(\\S*)(\"\\))");

    public static final Pattern MY_DIGI_AFF1_NETWORK01 = Pattern.compile("./*(window\\.location\\.href\\s*=\\s*(\"|'))(.+)((\"|');*)");
    public static final Pattern MY_DIGI_AOC01_RESUBMIT = Pattern.compile("./*(else\\s*window\\.location\\.href\\s*=\\s*(\"|'))(.+)((\"|');*)");
    public static final Pattern MY_DIGI_AOC01_CONFIRM = Pattern.compile("./*(document\\.getElementById(\"confirmURL\")\\.href\\s*=\\s*(\"|'))(.+)((\"|')\"\\s*+\\s*x)");
    public static final Pattern MY_DIGI_AFF2_NETWORK00 = Pattern.compile("./*(result\":\")(.+)(\")");

    public static final Pattern TH_AIS_AFF_NETWORK01 = Pattern.compile("./*(window\\.location\\.href\\s*=\\s*\")(.+)(\";*)");

    private static String REGEX_NUM = "[0-9]+";

    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public static final Pattern PROTOCOL_TYPE = Pattern.compile("^(http|https):\\/\\/");

    public static boolean isNum(String str){
        return Pattern.matches(REGEX_NUM, str);
    }
}
