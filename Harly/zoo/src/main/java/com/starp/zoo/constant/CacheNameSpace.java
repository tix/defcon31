package com.starp.zoo.constant;

/**
 * @author starp
 */
public class CacheNameSpace {
	public static final String ASTERISK = "*";

	public static final String COUNT = "count";
	public static final String COLON = ":";

	public static final String AFF_EPM_COUNTER = "zoo_aff_epm_counter";
	public static final String AFF_EPM_LIST = "zoo_aff_epm_list";
    public static final String LIST = "list";
	public static final String CLICK = "click";
	public static final String APP_TRANS = "app_trans";
	public static final String POST_BACK_TRANS = "post_back_trans";
	public static final String MO_TRANS = "mo_trans";
	public static final String REVENUE = "revenue";
	public static final String PULL_COUNT = "pull_count";
	public static final String AFF_OFFER_COUNTER = "zoo_aff_offer_counter";
	public static final String AFF_OFFER_TRANS = "zoo_aff_offer_trans";
	/**
	 * userEvent日志开关标识
	 */
	public static final String APP_USER_EVENT_LOG_STATUS = "app_user_event_log_status";

	/**
	 * 每个ip 最大拉取限制 redis 键
	 */
	public static final String AFF_PULL_COUNTER = "zoo_aff_pull_counter";

	public static final String QUEUE_CONSUMER = "queue_consumer:";
	public static final String MSISDN_PARAMS = "msisdn_params";
	public static final Object AFF_APP_OFFER_TRANS_COUNTER = "zoo_aff_app_offer_trans_counter";
	public static final String AFF_OFFER_PROTECT = "zoo_aff_offer_protect";
	public static final String AFF_OFFER_PULL_COUNTER = "zoo_aff_offer_pull_counter:";
	public static final String FILED_WRITE = "write";
	public static final String FILED_RANDOM_BITS = "randomBits";
	public static final String ZOO_DEDUCT = "zoo_deduct";
	public static final String FILED_SHOULD_WRITE = "shouldWrite";
	public static final String FILED_CURRENT = "current";
	public static final String ZOO_OFFER = "zoo_offer";
	public static final String ZOO_AUTO_TEST_OFFER_ID = "zoo_auto_test_offer_id";
	public static final String ZOO_AUTO_TEST_OFFER_EPM_COUNT = "zoo_auto_test_offer_epm_count";
	public static final String ZOO_AUTO_OFFER_NAME = "zoo_auto_offer_name:";
	public static final String ZOO_VIDEO_TYPE_LIST = "zoo_video_type_list";
	/**
	 * 安卓域名跳转集合
	 */
	public static final String ZOO_JUMP_DOMAIN_LIST = "zoo_jump_domain_list";

	/**
	 * 域名跳转URL
	 */
	public static final String ZOO_JUMP_DOMAIN_URL = "zoo_jump_domain_url";
	/**
	 * 智能栈存储删除的offerId主键
	 */
	public static final String ZOO_AUTOSTACK_OFFERIDS_DELETE = "zoo_autoStack_offerIds_DELETE";
	/**
	 * 智能栈列表存储的开启offer信息，便于智能栈配置获取使用
	 */
	public static final String ZOO_AUTOSTACK_OFFER_LIST = "zoo_autoStack_offer_list";
	/**
	 * 智能栈列表旧数据offer主键集合
	 */
	public static final String ZOO_AUTOSTACK_INIT_OFFERIDS = "zoo_autoStack_init_offerIds";
	/**
	 * 智能栈配置历史APP信息集合
	 */
	public static final String ZOO_AUTOSTACK_APP_CONFIG = "zoo_autoStack_app_config";
	/**
	 * 原有任务组与offer关联关系
	 */
	public static final String RECOVER_OFFER_GROUP_LIST = "recover_offer_group_list";
	/**
	 * 原有任务组与app关联关系
	 */
	public static final String RECOVER_APP_GROUP_LIST = "recover_app_group_list";
	public static final String ZOO_APP = "zoo_app";
	public static final String ZOO_OFFER_ASSIGN = "zoo_offer_assign";
    public static final String ZOO_APP_TEMP_INFO = "zoo_app_temp_info";
    public static final String ZOO_OFFER_MAX_PULL_EMAIL = "zoo_offer_max_pull";
	public static final String PROTC_APP_MAX_PULL = "PROTC_APP_MAX_PULL:";

    public static final String APP_EPM_LIST = "zoo_app_epm_list";
    public static final String APP_TAG_EPM_LIST = "zoo_app_tag_epm_list";
    public static final String LOOP = "loop" ;
	public static final String ZOO_OFFER_ASSIGN_FILTER = "zoo_offer_assign:filter";

	public static final String ZOO_OFF_PULL_COUNTER = "zoo_offer:pull_counter:";
	public static final String ZOO_APP_PULL_COUNTER = "zoo_app:pull_counter:";

	public static final String ZOO_APP_TRNS_COUNTER = "zoo_app:trans_counter:";
	public static final String ZOO_OFFER_TRNS_COUNTER = "zoo_offer:trans_counter:";
	public static final String ZOO_USER_TRANS_LIST = "zoo_user:trans:";

	public static final String ZOO_UNUSED_OFFER = "zoo_unused_offer:";

	public static final String ZOO_OFFER_DAY_INFO = "zoo_offer_day_info:";

	public static final String ZOO_IP_CLICKID = "zoo_ip_clickId:";

	public static final String ZOO_MSISDN_PARAM_INFO = "zoo_msisdn_param_info:";

	public static final String ZOO_OFFER_MSISDN_INFO = "zoo_offer_msisdn_info:";

    public static final String PROTECTED = "protected";

    public static final String ZOO_PROTECTED_TAG = "zoo_protected_tag";

    public static final String ZOO_PROTECTED_OFFER_SEVEN_DAY = "zoo_protected_offer_seven_day:";

	public static final String ZOO_PROTECTED_OFFER_THIRTY_DAY = "zoo_protected_offer_thirty_day:";

	public static final String IP = "ip";

	public static final String USERID = "userid";

	public static final String APP_EPM_RESET = "zoo_app_epm_reset";

	public static final String ZOO_APP_EPM_ALRAM = "zoo_app_epm_alarm";

}
