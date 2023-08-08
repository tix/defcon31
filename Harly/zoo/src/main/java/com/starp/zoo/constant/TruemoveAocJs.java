package com.starp.zoo.constant;


/**
 * @author charles
 */
public class TruemoveAocJs {

    public static final String AOC_XHR_1 = "TH_TRUEMOVE_NEW_STEP07";

    public static final String AOC_XHR_1_ID = "4971c4a2-b7b6-4ca6-8b92-476c8b88b806";

    public static final String AOC_XHR_2 = "TH_TRUEMOVE_NEW_STEP3_1";

    public static final String AOC_XHR_2_ID = "45715aa1-f38c-4670-b50a-ef717f05b0e7";

    public static final String IS_REDIRECT = "isRedirect";

    private static StringBuffer aocJsStb = new StringBuffer();

    public static StringBuffer getAocJsStb() {
        return aocJsStb;
    }

    public static void setAocJsStb(StringBuffer aocJsStb) {
        TruemoveAocJs.aocJsStb = aocJsStb;
    }
}
