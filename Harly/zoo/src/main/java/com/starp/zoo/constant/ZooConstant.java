package com.starp.zoo.constant;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/12.
 */
public class ZooConstant {

    public static final String SUCCESS = "success";

    public static final String PAGE = "page";

    public static final String LIMIT = "limit";

    public static final String BASE_DOMAIN = "baseDomain";

    public static final String TARGET_URL = "targetUrl";

    public static final String BASE_URL = "baseUrl";

    public static final String CREATE_TIME = "createTime";

    public static final String VIDEO_PATH = "res/vdo/";

    public static final String IMAGE_PATH = "res/img/";

    public static final String AUTO_TEST = "-autoTest-";

    public static final String FAIL = "fail";

    public static final int STATUS_0 = 0;

    public static final int STATUS_1 = 1;

    public static final String OFFER_WAP = "wap";

    public static final String OFFER_PIN_WAP = "pin-wap";

    public static final String OFFER_MO = "mo";

    public static final String OFFER_PIN_MO = "pin-mo";

    public static final String OFFER_DOUBLE_MO = "double-mo";

    public static final String APP = "app";

    public static final String PAYMENT = "payment";

    public static final int TYPE_UPDATE = 2;

    public static final String VALUE = "values";

    public static final String UNKNOWN = "unknown";

    public static final String LOCAL_IP = "127.0.0.1";

    public static final String SPLIT_CHARTER = ",";

    public static final String SPLIT_IP = ",";

    public static final int IP_LEN = 4;

    public static final int RANDOM_COLOR = 255;

    public static final int RANDOM_RGB = 3;

    public static final int TYPE_PIN = 4;

    public static final int TYPE_LP_MO = 5;

    public static final String PERCENT_SIGN = "%";

    public static final String SLASH = "/";

    public static final String EQUALS = "-";

    public static final String LEFT_BRACKETS = "{";

    public static final String LOG = ".log";

    public static final String REG_ONE = ".*";

    public static final String REG_TWO = "^";

    public static final String REG_THREE = "$";

    /**
     * constant for APP event
     */
    public static final String APP_EVENT_MSG = "msg";
    public static final String APP_EVENT_USER_ID = "userid";
    public static final String DEVICE_ID = "deviceId";
    public static final String APP_EVENT_CODE = "eventCode";
    public static final String APP_EVENT_TIME = "eventTime";
    public static final String APP_EVENT_APP_ID = "appid";
    public static final String MNC = "mnc";
    public static final String APP_EVENT_PARAM_1 = "param1";
    public static final String APP_EVENT_PARAM_2 = "param2";
    public static final String APP_EVENT_PARAM_3 = "param3";
    public static final String APP_EVENT_PARAM_4 = "param4";
    public static final String APP_EVENT_PARAM_5 = "param5";
    public static final int APP_EVENT_CODE_200 = 200;

    public static final String OFFER_ID = "offerId";
    public static final int OFFER_TASK_OPEN = 1;
    public static final String LOCALHOST = "127.0.0.1";
    public static final String TOTAL = "total";
    public static final String INTERROGATION_MARK = "?";
    public static final String LEFT_LINE = "/";
    public static final String AND_MARK = "&";
    public static final String EQUAL_MARK = "=";
    public static final String AFF_APP_SPLITTER = "-app-";
    public static final String AFF_SMART_LINK_SPLITTER = "-sml-";
    public static final String AFF_OFFER_SPLITTER = "_r3_";
    public static final String IE = "IE";


    public static final String PARAM_CLICKID = "{CLICKID}";
    public static final String PARAM_OFFERID = "{OFFERID}";
    public static final String PARAM_PAYOUT = "{PAYOUT}";
    public static final String ERROR_VALUE = "error value";
    public static final String USER_AGENT = "user-Agent";
    public static final String INIT_EPM_NUM = "initEpmNum";
    public static final String PARTNER = "partner";
    public static final String COUNTRY = "country";
    public static final String OPERATOR = "operator";
    public static final String AFF_NAME = "affName";
    public static final String OFFER_NAME = "offerName";
    public static final String BEGIN = "begin";
    public static final String END = "end";
    public static final int CALLBACK_TYPE_1 = 1;
    public static final int CALLBACK_TYPE_2 = 2;
    public static final int CALLBACK_TYPE_3 = 3;
    public static final int CALLBACK_TYPE_4 = 4;
    public static final int CATEGORY_APP = 1;
    public static final int CATEGORY_AFFILIATE = 2;
    public static final int SCRIPT_TYPE_APP = 1;
    public static final int SCRIPT_TYPE_AFFILIATE = 2;

    public static final String COLON = ":";
    public static final String TXT_SUFFIX = ".txt";

    public static final String TIME_RANGE = "timeRange";

    public static final int EVENT_TYPE_PIN = 4;
    public static final int EVENT_TYPE_MSISDN = 6;
    public static final int EVENT_TYPE_MSISDN_PIN = 7;
    public static final int EVENT_TYPE_PIN_MO = 5;
    public static final int TAG_TYPE_STACK = 1;
    public static final int TAG_TYPE_GROUP = 2;
    public static final int TAG_TYPE_OTHERS = 3;
    public static final String TAG_STACK = "stack";
    public static final String TAG_GROUP = "group";
    public static final String TAG_OTHERS = "others";
    public static final String IS_ASSIGNED = "isAssigned";
    public static final String TAGS = "tags";
    public static final String RESOURCE_NAME = "resourceName";
    public static final String AFFILIATE = "affiliate";


    public static final String GP_REPORT_STOP = "stop";
    public static final String GREY_NEW_ENCODE_0718 = "grey_new_encode_0718";
    public static final String GP_REPORT_OFFLINE = "2";
    public static final String GP_REPORT_ONLINE = "1";
    public static final String GP_REPORT_NOT_ONLINE = "0";
    public static final String EMAIL_DATE = "date";
    public static final String TITLE = "title";
    public static final String GPREPORT_MAIL_TEMPLATE = "gpOfflineReportMail.ftl";
    public static final String GPREPORT_MAIL_OFFLINE_SUBJECT = "GP下线告警";
    public static final String GPREPORT_MAIL_ONLINE_SUBJECT = "GP上线通知";
    public static final String GP_REPORT_NAME = "name";
    public static final String GP_REPORT_LINK = "link";
    public static final String GP_REPORT_CUSTOMER = "customer";
    public static final String IDS = "ids";
    public static final String OFFER_NAMES = "offerNames";
    public static final String EMAILS = "emails";

    public static final String REGULAR_STAR = "(.*?)";
    public static final String REGULAR_POINT = ".*?";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String ZOO_TEST_OFFER = "zoo:test_offer";

    public static final String RECHECK_EPM_HOUR_TEMPLATE = "recheckEpmMail.ftl";
    public static final String RECHECK_EPM_HOUR_SUBJECT = "EPM小时检查告警";
    public static final String EPM_LIST_EXCEPTION_TEMPLATE = "genEpmListErrorAlarmMail.ftl";
    public static final String EPM_LIST_EXCEPTION_SUBJECT = "EPM列表异常告警";
    public static final String EPM_LIST_KEY = "epmListKey";
    public static final String ERROR_EPM_LIST_SIZE = "errorSize";
    public static final String RE_CALCUATE_EPM_LIST_SIZE = "recalcuateSize";
    public static final String EPM_TIME = "epmTime";
    public static final String ALARM_INFO = "alarmInfo";
    public static final String DB_CLICK_NUM = "dbClickNum";
    public static final String DB_TRAN_NUM = "dbTranNum";
    public static final String DB_APP_TRAN_NUM = "dbAppTranNum";
    public static final String DB_REVENUE = "dbRevenue";
    public static final String REDIS_CLICK_NUM = "redisClickNum";
    public static final String REDIS_APP_TRAN_NUM = "redisAppTranNum";
    public static final String REDIS_TRAN_NUM = "redisTranNum";
    public static final String REDIS_REVENUE = "redisRevenue";

    public static final String QUEUE_MAIL_TEMPLATE = "consumerAlarmMail.ftl";
    public static final String QUEUE_MAIL_SUBJECT = "队列消费异常告警";
    public static final String MESSAGE = "message";
    public static final String SYSTEM = "system";
    public static final String QUEUE_NAME = "queueName";
    public static final String EXCEPTION = "exception";
    public static final String MESSAGE_ID = "messageId";
    public static final String EMAIL_UUID = "uuid";

    public static final String AFF_POST_BACK_MODEL = "AffPostBackModel";
    public static final String AFF_APK_POSTBACK_MODEL = "AffApkPostBackModel";

    public static final String AFF_POST_BACK_IE_MODEL = "AffPostBackIeModel";
    public static final String UNDER_LINE = "_";
    public static final String VERTICAL_LINE = "|";
    public static final int APP_MSISDN_URL_EVENT = 11;
    public static final String COMMA = ",";

    public static final String AFF_TRANS_MAIL_TEMPLATE = "affTransAlarmMail.ftl";
    public static final String AFF_TRANS_MAIL_SUBJECT = "渠道 OFFER 转化率异常告警";
    public static final String BEGIN_TIME = "beginTime";
    public static final String END_TIME = "endTime";
    public static final String THRESHOLD = "threshold";
    public static final String PARTNER_OFFER_ID = "partnerOfferId";
    public static final String APP_EVENT_URL = "/app/event";
    public static final String PROTECT = "protect";
    public static final String APP_USER_EVENT = "appUserEvent";
    public static final String NULL_STR = "null";
    public static final String SPLIT_C = "/c";
    public static final String APP_SERVICE_SDK_004 = "Service-SDK-0004";
    public static final String APP_SERVICE_SDK_005 = "Service-SDK-0005";
    public static final Double CHANNEL_DEVENUE = 0.8;
    public static final String TRAN_CHECK_TIME = "2019-07-31";
    public static final String TH_MCC = "520";
    public static final String MY_MCC = "502";
    public static final String APP_ID = "appId";
    public static final String ZOO_CLICK_ID = "zoo_click_id";

    public static final String ZOO_REDIS_ERROR_MAIL_TEMPLATE = "redisAlaramMail.ftl";
    public static final String ZOO_REDIS_ERROR_MAIL_SUBJECT = "redis 服务器异常";

    public static final String ZOO_EPM_ALARM_MAIL_TEMPLATE = "epmAlarmMail.ftl";

    public static final String ZOO_EPM_ALARM_MAIL_SUBJECT = "epm 降低告警";

    public static final String ZOO_MO_CR_ALARM_MAIL_SUBJECT = "mo cr 降低告警";

    public static final String ZOO_MO_CR_ALARM_MAIL_TEMPLATE = "moCrAlarmMail.ftl";

    public static final String IP = "ip";
    public static final int EPM_SHOW_TYPE_OFFER = 1;
    public static final int EPM_SHOW_TYPE_HOUR = 2;
    public static final int EPM_SHOW_TYPE_COUNTRY = 3;
    public static final int EPM_SHOW_TYPE_PARTNER = 4;
    public static final int EPM_SHOW_TYPE_DAY = 5;
    public static final int EPM_SHOW_TYPE_APP = 6;
    public static final int EPM_SHOW_TYPE_OPERATOR = 7;
    public static final String GMT_8 = "GMT+8:00";
    public static final String PROTC_OFFER_STEP = "protc_offer_step";
    public static final String STAR = "*";
    public static final String GMT_0 = "GMT+0:00";
    public static final String MAX_PULL_MAIL_SUBJECT = "渠道 OFFER 满点击告警";
    public static final String COMMENT = "comment";
    public static final String MAX_PULL_TEMPLATE = "maxPullAlarmMail.ftl";

    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ZOO = "zoo";
    public static final String REDIRECT_APK = "redirectApk";
    public static final String HISTORYKEY = "historykey";
    public static final String AFF_REDIRECT_APK_CLICKID = "aff_redirect_apk_clickId";
    public static final String AFF_APK_TEST_POSTBACK_URL = "http://www.baidu.com?test=123";
    public static final String AFF_APK_POSTBACK_URL = "http://mobiletrk.click-now.today/postback?userid=5797";

    public static final String TXID = "txid";
    public static final String HANDLE_DEBUG_JS = "Y";
    public static final String MNC_CODE = "zoo:mnc";
    public static final String MSISDN = "msisdn";
    public static final String FB_STR = "fb";
    public static final String WIFI_STR = "wifi";
    public static final String PERMISSION_STR = "permission";
    public static final String MNC_PERMISSION = "zoo:mnc:permission:";
    public static final String BRACKETS = "{}";
    public static final String PERMISSION = "zoo:permission";

    public static final String APP_OPERATOR_CAP_LIST = "operatorCapList";
    public static final String APP_OPERATOR_TOP_OFFER_LIST = "operatorTopOfferList";
    public static final String APP_OPERATOR_CAP_KEY = "zoo:app_operator:cap_key";
    public static final String APP_OPERATOR_TOP_OFFER = "zoo:app_operator:top_offer:";
    public static final String EPM = "epm";
    public static final String ENABLE_LIST = "enableList";
    public static final String WIFI = "zoo:wifi";
    public static final String ADMIN_MNC_CONFIG = "admin_mnc_config";

    public static final String MAX_PULL = "maxPull";
    public static final String APP_CAP = "app_cap";
    public static final String CAP = "cap";

    public static final String PULLED_NUM = "pulledNum";
    public static final String APP_TRANS_NUM = "appTransNum";
    public static final String OFFER_TRANS_NUM = "appTransNum";
    public static final String USERID_USERAGENT_INDEX = "zoo:userId_userAgent:";
    public static final String SDK_APP_OPERATOR = "opSdkList";
    public static final String SDK_URL = "sdkPath";
    public static final String BLACK_MACHINE = "zoo:black:machine";
    public static final String MODEL = "model";
    public static final String OLD_NAME = "oldName";
    public static final String NAME = "name";

    public static final String OPERATOR_ARRAY = "operatorArr";
    public static final String USER_COUNT = "userCount";

    public static final String DATE_TIME = "datetime";

    public static final String AIS = "AIS";

    public static final String TRUE = "TRUE";

    public static final String UNKNOWN2 = "UNKNOWN";

    public static final String DTAC = "DTAC";


    public static final String USER_OFFER_DETAIL = "user_offer_detail";

    public static final String REDIS_OUTOFDIRECTMEMORY = "io.netty.util.internal.OutOfDirectMemoryError";

    public static final String S3_URL = "s3Url";

    public static final String EPM_RESET_HOUR = "epmResetHour";

    public static final String EPM_RESET_EXPONENTIATION = "epmRestExponentiation";


    public static final String EPM_APP_OPERATOR = "opEpmList";

    public static final String APP_NAME = "appName";

    public static final String RATE = "rate";


    public static final String RECORD_RATE = "record_rate";

    public static final String BEFORE_REVENUE = "beforeRevenue";

    public static final String BEFORE_CLICK = "beforeClick";

    public static final String BEFORE_EPM = "beforeEpm";

    public static final String NOW_EPM = "nowEpm";

    public static final String NOW_REVENUE = "nowRevenue";


    public static final String NOW_CLICK = "nowClick";


    /**
     * 月份常量
     */
    public static final String JANUARY = "January";

    public static final String FEBRUARY = "February";

    public static final String MARCH = "March";

    public static final String APRIL = "April";

    public static final String MAY = "May";

    public static final String JUNE = "June";

    public static final String JULY = "July";

    public static final String AUGUST = "August";

    public static final String SEPTEMBER = "September";

    public static final String OCTOBER = "October";

    public static final String NOVEMBER = "November";

    public static final String DECEMBER = "December";

    /**
     * 月份常量英文简写 ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
     */
    public static final String UK_JANUARY = "Jan";

    public static final String UK_FEBRUARY = "Feb";

    public static final String UK_MARCH = "Mar";

    public static final String UK_APRIL = "Apr";

    public static final String UK_MAY = "May";

    public static final String UK_JUNE = "Jun";

    public static final String UK_JULY = "Jul";

    public static final String UK_AUGUST = "Aug";

    public static final String UK_SEPTEMBER = "Sep";

    public static final String UK_OCTOBER = "Oct";

    public static final String UK_NOVEMBER = "Nov";

    public static final String UK_DECEMBER = "Dec";


    public static final String TEST_APP_ID = "7dd992ca-56b2-4a24-86be-28d4f80d60e0";
    public static final String ZOO_TEST_OFFER_INFO = "zoo_pb_test_offer_info:";
    public static final String ZOO_TEST_OFFER_DATA = "zoo_test_offer_data";
    public static final String OFFER_EPM_HOUR = "offer_epm_hour";
    public static final String RESPONSE_HEADER = "sh";
    public static final String HTML_INFO = "zoo_html_info";
    public static final String EPM_INFO_TABLE_NAME = "t_aff_epm";
    public static final String TIME_2020 = "2021-01-01 00:00:00";
    public static final String YEAR_2020 = "2020";
    public static final String SCRIPT_MODEL = "script";
    public static final String NODE_JS_RESULT = "zoo_nodejs_result:";
    public static final String ANALYSISBODY_VO = "analysisBodyVo";
    public static final String NOT_EXIST_DB = "not_exist_db";
    public static final String TRANS_COUNT_NOT_MATCH = "trans_not_match";
    public static final String OFFER_STATUS = "offer_status";
    public static final String BELONG = "belong";
    public static final String ZOO_PAY_MO_POSTBACK = "zoo_pay_mo_postback";
    public static final String REDIS_CR_RATE = "redisCrRate";
    public static final String MO_CR_THRESHOLD = "moCrThreshold";
    public static final String CLICK_THRESHOLD = "clickThreshold";
    public static final String CURRENT_HOUR = "currentHour";
    public static final String SHORTCODE = "shortcode";
    public static final String KEYWORD = "keyword";
    public static final String CLICK = "click";
    public static final String TRANS = "trans";
    public static final String ZA_MTN = "ZA_MTN";
    public static final String ZA_VODACOM = "ZA_VODACOM";

    /**
     * 电话号码表常量.
     */
    public static final String RESULT = "result";
    public static final String R0 = "r0";
    public static final String R1 = "r1";
    public static final String R2 = "r2";
    public static final String R3 = "r3";
    public static final String R4 = "r4";
    public static final String R5 = "r5";
    public static final String R6 = "r6";
    public static final String R7 = "r7";
    public static final String B = "b";
    public static final String R = "r";
    public static final String MSISDN_RECORD_DAY = "zoo_msisdn_record_day";
    public static final String LOG_MSISDN_LOCAL_INDEX = "zoo_log_msisdn_local_index";
    public static final String MSISDN_RECORD_LOCK = "msisdn_record_lock";
    public static final String LOCK_MSISDN_LOCAL_INDEX = "msisdn_local_index_lock";





    /**
     * 队列信息
     */
    public static final Integer QUEUE_EPM_CALCULATE = 1;
    public static final String EPM_CALCULATE_KEY = "epmCalculateKey";

    public static final Integer QUEUE_EPM_RETRY_CALCULATE = 2;
    public static final String EPM_RETRY_CALCULATE_KEY = "epmRetryCalculateKey";

    public static final Integer QUEUE_OFFER_AUTO_START = 3;
    public static final String OFFER_AUTO_START_MODEL = "offerAutoStartModel";

    public static final Integer QUEUE_OFFER_REFRESH_UNUSED = 4;
    public static final String QUEUE_OFFER_REFRESH_UNUSED_MODEL = "unusedOfferModel";


    public static final Integer QUEUE_OFFER_CHECK_TRANS = 5;
    public static final String QUEUE_OFFER_CHECK_TRANS_MODEL = "offerCheckTransModel";


    public static final Integer QUEUE_APP_EPM_ALARM = 6;
    public static final String QUEUE_APP_EPM_ALARM_MODEL = "appEpmAlarmModel";

    public static final Integer QUEUE_OFFER_RUNTIME = 7;
    public static final String QUEUE_OFFER_RUNTIME_MODEL = "offerRunTimeModel";

    public static final Integer QUEUE_CLICK_INFO = 8;
    public static final String CLICK_INFO_MODEL = "clickInfoModel";
    public static final String VENDMOB_PARTNER = "vendmob";
}


