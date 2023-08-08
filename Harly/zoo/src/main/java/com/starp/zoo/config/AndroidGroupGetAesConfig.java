package com.starp.zoo.config;

import com.starp.zoo.common.constant.ProtocolCrackConstant;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.util.AesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author curry by 2022/11/2
 */
public class AndroidGroupGetAesConfig {

	private static final String SDK36 = "SDK36";

	private static final String KEY_36 = "vTiQ51WWHd0wWm1ownYXTA==";

	/**
	 * 第一个新加密 iv 特殊处理
	 */
	private static final String IV_36 = "EPWKTF18pzql2eoUXDO7Mw==";

	private static final String SDK37 = "SDK37";

	private static final byte[] KEY_37 = new byte[]{-40, 70, 89, -119, 121, 102, 83, 94, -19, 23, 81, -35, -40, 69, 40, 122};

	private static final String SDK38 = "SDK38";

	/**
	 * base64加密编码    btoa('2022111420221114')   最多16位字符,因为线上环境 JDK 最高只支持128位加密算法
	 */
	private static final String KEY_38 = "MjAyMjExMTQyMDIyMTExNA==";

	private static final String SDK39 = "SDK39";

	private static final byte[] KEY_39 = new byte[]{62, 125, 57, -99, -3, 40, 71, 52, -18, 96, 17, 101, -14, -93, 107, -74};

	private static final String SDK40 = "SDK40";

	private static final String KEY_40 = "MjAyMjEyMjgyMDIyMTIyOA==";

	private static final String SDK41 = "SDK41";

	private static final byte[] KEY_41 = new byte[]{112, 120, -45, -46, 111, -36, -104, 16, 44, -61, -40, 74, 102, 108, 87, -23};

	private static final String SDK42 = "SDK42";

	private static final String KEY_42 = "MjAyMzAxMjkyMDIzMDEyOQ==";

	private static final String SDK43 = "SDK43";

	private static final byte[] KEY_43 = new byte[]{-117, 124, -2, -44, 85, -1, 65, 37, 72, -90, 1, -63, 92, 15, 60, 32};

	private static final String SDK44 = "SDK44";

	private static final String KEY_44 = "MjAyMzAyMjcyMDIzMDIyNw==";

	private static final String SDK45 = "SDK45";

	private static final byte[] KEY_45 = new byte[]{-88, 89, -99, 71, 106, 51, -37, 123, -86, 88, 42, 78, -45, 19, -84, -54};

	private static final String SDK46 = "SDK46";

	private static final String KEY_46 = "MjAyMzAzMjMyMDIzMDMyMw==";

	private static final String SDK47 = "SDK47";

	private static final byte[] KEY_47 = new byte[]{100, 21, -117, -95, -123, -112, -84, -65, 121, 50, -112, -12, -57, -56, 67, -86};

	private static final String SDK48 = "SDK48";

	private static final String KEY_48 = "MjAyMzA0MDQyMDIzMDQwNA==";

	private static final String SDK49 = "SDK49";

	private static final byte[] KEY_49 = new byte[]{122, 94, -57, 41, 82, 56, 103, 40, -66, -70, 101, -97, 97, -123, 102, -33};

	private static final String SDK50 = "SDK50";

	private static final String KEY_50 = "MjAyMzA1MDQyMDIzMDUwNA==";

	private static final String SDK51 = "SDK51";

	private static final String KEY_51 = "MjAyMzA1MDUyMDIzMDUwNQ==";

	private static final String SDK52 = "SDK52";

	private static final byte[] KEY_52 = new byte[]{94, -127, 61, 36, -101, -45, 12, -57, -4, 83, -36, -86, 120, -74, 72, 93};

	private static final String SDK53 = "SDK53";

	private static final String KEY_53 = "Y2hldDIwMjMwNjAxY2hldA==";

	private static final String SDK54 = "SDK54";

	private static final String KEY_54 = "c3RhcjIwMjMwNjAyc3Rhcg==";

	private static final String SDK55 = "SDK55";

	private static final String KEY_55 = "c3RhcjIwMjMwNzAzMDcwMw==";

	private static final String SDK56 = "SDK56";

	private static final String KEY_56 = "Y2hldDIwMjMwNzAzMDcwMw==";

	private static final String SDK57 = "SDK57";

	private static final String KEY_57 = "YjdjeWxFTzhob1BQZnF6OA==";

	private static final String SDK58 = "SDK58";

	private static final String KEY_58 = "NztbOJwyUUDO7fjcIzKBCXFXkiIzTTggtq86GuoVg7X/CdVlBX1efg8xc1m5LqdxeBElvpMHkJSBY2ym9u8hhOFQPUnjVUD/HNUJkIUhKlj1dvBI+2DskSr/BxLePmM5rcHjIiUyOqwGEJfHX4s2qmVCZ/xAnpPBpfBjnEkJJPPDLC6pxv6QldxKUmZoDreaI5tGtcdTfWvF7WBebc2826gwKmEN/aeipPGS3dzqFMD7C2G5KC/OthxaWYKoFJNzuacL3DuVqt+gsWyvIN1ByH7ypIXY96crBjlzIygOA1ULJhT76WGVjKiXkVC9an+4BWy1LwikmQh1VJGEo7BlN7sDV7ro90CllsVGetMGZMdy83g/JNPAZ/0hh6PctDGi4o8GqnkwueEgoKRQd8u+31kUtMzzgK2pDnf2iGJpaIJlPzfXRop2MfYpF0HceN7+xPe75hi0frtBrEgcUymAavGA1cUn4+kaq+C9YKyk2nYZKFsUFhFiAxMTq/naY5DUWQVh4v5xq/WWwufGWow54zOkRXE42tlF2s0cteyOSndVA/hcubVGyWlbZYlKXqOZnCoOsGpSw9jjt8Pj5xljcHlkqa/WKTsV/sXX8RfvenEq9IF9Jt6bRPawdpEf84FXq7nD67lyVu1CfU30ED8RbqQnrUoOHS8WOZRW7AcQJEM=";

	private static final String SDK59 = "SDK59";

	private static final String KEY_59 = "WMrUFi+8TFustG38fYQtnbIIy7Nq+lA+J+Teh/rJSI4F1MccWB14SyHVCp1k8RbISjuULumZYbHnl/TyIIpyHzx1MR24bRvHFfe4+4j3FnKAj70k+7ZkMRMn+a9aVLu7IvQqyA1SsO5W0Otgkq+0p6n0RuuZ1pQRJefRuEyywvnEpWqZ5UQY2UsW8KJvLGCD+QHcFzaUkeZ1BHH/e/FzWKb2JSHw44ZLZArz6NOt1wrXAkdJM+Ttv8V+PJibV5WRKvY3TF9tVPIYmyYc27TZdHFdaOYNS1FI+nEE5Tr6NiTCLYnjFVlYwKfA4p9r4GUg6Z4KWlTGrTOpkXzY6/K1ZphMjVlKZZ3UgpKXewjTKmf7UkpWGfLHkVATHWOfUrbW6RBj6XtlEZTm/7SftElq0loq3/sxUpDxiEIVg4u0VyCrSZalQo2cjuYHBMh/hiH5qk6iA9PWgHH6NHLqNbtKXTBZ6FbJGdVvDio10xNriMwX5YI8B7XGPryFE0JQUltSBiqgy98BQHpUPZM4MiiquX6HBBbgnYKKN0sjwaYofzSSeQK4gbd4DFu0ir4/AQ49VpFpb+bWlyur2ZFi+2uIjwhxHtUySMzOia8wdY5BO84SMctIML9NQkJbBl1JBkt8YL4bYyC3VIqg5qWEhaeRL3CpqLk0ArGRMBl06WWN9hQ=";

	private static final String SDK60 = "SDK60";

	private static final String KEY_60 = "MjAyMzA4MDJjaGV0MDgwMg==";

	private static final String SDK61 = "SDK61";

	private static final String KEY_61 = "MDgwMnN0YXIyMDIzMDgwMg==";

	@SuppressFBWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
	public static Map<String, String> URI_MAP = new HashMap<>(1);

	static {
		Map<String, String> tempMap = new HashMap<>(1);

		/**
		 * Chet 2022-11-01 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cstatus", SDK36);
		//GET_MSISDN
		tempMap.put("/cmsi", SDK36);
		//SEND_DEVICEID_MSISDN
		tempMap.put("/csend", SDK36);
		// GET_OFFER（线上）
		tempMap.put("/cpull", SDK36);
		// 利刃_PULL_CONFIG
		tempMap.put("/cjs", SDK36);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cwrite", SDK36);
		// SEND_APP_EVENT
		tempMap.put("/clog", SDK36);
		// 利刃_SEND_HTML
		tempMap.put("/csource", SDK36);
		// HTTP_SEND_LOG
		tempMap.put("/caxe", SDK36);
		//CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cparam", SDK36);
		//NEW HTTP PULL CONFIG
		tempMap.put("/carr", SDK36);
		//address msisdn
		tempMap.put("/caddr", SDK36);

		/**
		 * Grey 2022-11-01 新接口
		 */
		// GET APP STATUS
		tempMap.put("/gobtpac", SDK37);
		//GET_MSISDN
		tempMap.put("/gcall", SDK37);
		//SEND_DEVICEID_MSISDN
		tempMap.put("/gsemsi", SDK37);
		// GET_OFFER（线上）
		tempMap.put("/gpbill", SDK37);
		// 利刃_PULL_CONFIG
		tempMap.put("/gscri", SDK37);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gmake", SDK37);
		// SEND_APP_EVENT
		tempMap.put("/gdaily", SDK37);
		// 利刃_SEND_HTML
		tempMap.put("/gpage", SDK37);
		// HTTP_SEND_LOG
		tempMap.put("/gfight", SDK37);
		//CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gargum", SDK37);
		//NEW HTTP PULL CONFIG
		tempMap.put("/glist", SDK37);
		//address msisdn
		tempMap.put("/gsave", SDK37);

		/**
		 * Chet 2022-11-14 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cabandon", SDK38);
		//GET_MSISDN
		tempMap.put("/cability", SDK38);
		//SEND_DEVICEID_MSISDN
		tempMap.put("/cable", SDK38);
		// GET_OFFER（线上）
		tempMap.put("/cabortion", SDK38);
		// 利刃_PULL_CONFIG
		tempMap.put("/cabout", SDK38);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cabove", SDK38);
		// SEND_APP_EVENT
		tempMap.put("/cabroad", SDK38);
		// 利刃_SEND_HTML
		tempMap.put("/cabsence", SDK38);
		// HTTP_SEND_LOG
		tempMap.put("/cabsolute", SDK38);
		//CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cabsolutely", SDK38);
		//NEW HTTP PULL CONFIG
		tempMap.put("/cabsorb", SDK38);
		//address msisdn
		tempMap.put("/cabuse", SDK38);

		/**
		 * Gray 2022-11-14
		 */
		// GET APP STATUS
		tempMap.put("/gacademic", SDK39);
		//GET_MSISDN
		tempMap.put("/gaccept", SDK39);
		//SEND_DEVICEID_MSISDN
		tempMap.put("/gaccess", SDK39);
		// GET_OFFER（线上）
		tempMap.put("/gaccident", SDK39);
		// 利刃_PULL_CONFIG
		tempMap.put("/gaccompany", SDK39);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gaccomplish", SDK39);
		// SEND_APP_EVENT
		tempMap.put("/gaccording", SDK39);
		// 利刃_SEND_HTML
		tempMap.put("/gaccount", SDK39);
		// HTTP_SEND_LOG
		tempMap.put("/gaccurate", SDK39);
		//CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gaccuse", SDK39);
		//NEW HTTP PULL CONFIG
		tempMap.put("/gachieve", SDK39);
		//address msisdn
		tempMap.put("/gachievement", SDK39);

		/**
		 * Chet 2022-12-28 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cacid", SDK40);
		// GET_MSISDN
		tempMap.put("/cacknowledge", SDK40);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cacquire", SDK40);
		// GET_OFFER（线上）
		tempMap.put("/cacross", SDK40);
		// 利刃_PULL_CONFIG
		tempMap.put("/cact", SDK40);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/caction", SDK40);
		// SEND_APP_EVENT
		tempMap.put("/cactive", SDK40);
		// 利刃_SEND_HTML
		tempMap.put("/cactivist", SDK40);
		// HTTP_SEND_LOG
		tempMap.put("/cactivity", SDK40);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cactor", SDK40);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cactress", SDK40);
		// address msisdn
		tempMap.put("/cactual", SDK40);

		/**
		 * Gray 2022-12-28
		 */
		// GET APP STATUS
		tempMap.put("/gactually", SDK41);
		// GET_MSISDN
		tempMap.put("/gad", SDK41);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gadapt", SDK41);
		// GET_OFFER（线上）
		tempMap.put("/gadd", SDK41);
		// 利刃_PULL_CONFIG
		tempMap.put("/gaddition", SDK41);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gadditional", SDK41);
		// SEND_APP_EVENT
		tempMap.put("/gaddress", SDK41);
		// 利刃_SEND_HTML
		tempMap.put("/gadequate", SDK41);
		// HTTP_SEND_LOG
		tempMap.put("/gadjust", SDK41);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gadjustment", SDK41);
		// NEW HTTP PULL CONFIG
		tempMap.put("/gadministration", SDK41);
		// address msisdn
		tempMap.put("/gadministrator", SDK41);

		/**
		 * Chet 2023-01-29 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cadmire", SDK42);
		// GET_MSISDN
		tempMap.put("/cadmission", SDK42);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cadmit", SDK42);
		// GET_OFFER（线上）
		tempMap.put("/cadolescent", SDK42);
		// 利刃_PULL_CONFIG
		tempMap.put("/cadopt", SDK42);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cadult", SDK42);
		// SEND_APP_EVENT
		tempMap.put("/cadvance", SDK42);
		// 利刃_SEND_HTML
		tempMap.put("/cadvanced", SDK42);
		// HTTP_SEND_LOG
		tempMap.put("/cadvantage", SDK42);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cadventure", SDK42);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cadvertising", SDK42);
		// address msisdn
		tempMap.put("/cadvice", SDK42);

		/**
		 * Gray 2023-01-29
		 */
		// GET APP STATUS
		tempMap.put("/gadvise", SDK43);
		// GET_MSISDN
		tempMap.put("/gadviser", SDK43);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gadvocate", SDK43);
		// GET_OFFER（线上）
		tempMap.put("/gaffair", SDK43);
		// 利刃_PULL_CONFIG
		tempMap.put("/gaffect", SDK43);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gafford", SDK43);
		// SEND_APP_EVENT
		tempMap.put("/gafraid", SDK43);
		// 利刃_SEND_HTML
		tempMap.put("/gafter", SDK43);
		// HTTP_SEND_LOG
		tempMap.put("/gafternoon", SDK43);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gagain", SDK43);
		// NEW HTTP PULL CONFIG
		tempMap.put("/gagainst", SDK43);
		// address msisdn
		tempMap.put("/gage", SDK43);

		/**
		 * Chet 2023-02-27 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cagency", SDK44);
		// GET_MSISDN
		tempMap.put("/cagenda", SDK44);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cagent", SDK44);
		// GET_OFFER（线上）
		tempMap.put("/caggressive", SDK44);
		// 利刃_PULL_CONFIG
		tempMap.put("/cago", SDK44);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cagree", SDK44);
		// SEND_APP_EVENT
		tempMap.put("/cagreement", SDK44);
		// 利刃_SEND_HTML
		tempMap.put("/cagricultural", SDK44);
		// HTTP_SEND_LOG
		tempMap.put("/cahead", SDK44);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/caircraft", SDK44);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cairline", SDK44);
		// address msisdn
		tempMap.put("/cairport", SDK44);


		/**
		 * Gray 2023-02-27
		 */
		// GET APP STATUS
		tempMap.put("/galbum", SDK45);
		// GET_MSISDN
		tempMap.put("/galcohol", SDK45);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/galive", SDK45);
		// GET_OFFER（线上）
		tempMap.put("/galliance", SDK45);
		// 利刃_PULL_CONFIG
		tempMap.put("/gallow", SDK45);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/galmost", SDK45);
		// SEND_APP_EVENT
		tempMap.put("/galone", SDK45);
		// 利刃_SEND_HTML
		tempMap.put("/galong", SDK45);
		// HTTP_SEND_LOG
		tempMap.put("/galready", SDK45);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/galso", SDK45);
		// NEW HTTP PULL CONFIG
		tempMap.put("/galter", SDK45);
		// address msisdn
		tempMap.put("/galternative", SDK45);

		/**
		 * Chet 2023-03-23 新接口
		 */
		// GET APP STATUS
		tempMap.put("/calthough", SDK46);
		// GET_MSISDN
		tempMap.put("/calways", SDK46);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/camazing", SDK46);
		// GET_OFFER（线上）
		tempMap.put("/camerican", SDK46);
		// 利刃_PULL_CONFIG
		tempMap.put("/camong", SDK46);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/camount", SDK46);
		// SEND_APP_EVENT
		tempMap.put("/canalysis", SDK46);
		// 利刃_SEND_HTML
		tempMap.put("/canalyst", SDK46);
		// HTTP_SEND_LOG
		tempMap.put("/canalyze", SDK46);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cancient", SDK46);
		// NEW HTTP PULL CONFIG
		tempMap.put("/canger", SDK46);
		// address msisdn
		tempMap.put("/cangle", SDK46);

		/**
		 * Gray 2023-03-23
		 */
		// GET APP STATUS
		tempMap.put("/gangry", SDK47);
		// GET_MSISDN
		tempMap.put("/ganimal", SDK47);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/ganniversary", SDK47);
		// GET_OFFER（线上）
		tempMap.put("/gannounce", SDK47);
		// 利刃_PULL_CONFIG
		tempMap.put("/gannual", SDK47);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/ganother", SDK47);
		// SEND_APP_EVENT
		tempMap.put("/ganswer", SDK47);
		// 利刃_SEND_HTML
		tempMap.put("/ganticipate", SDK47);
		// HTTP_SEND_LOG
		tempMap.put("/ganxiety", SDK47);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/ganybody", SDK47);
		// NEW HTTP PULL CONFIG
		tempMap.put("/ganymore", SDK47);
		// address msisdn
		tempMap.put("/ganyone", SDK47);

		/**
		 * Star 2023-04-04 新接口
		 */
		// GET APP STATUS
		tempMap.put("/csanything", SDK48);
		// GET_MSISDN
		tempMap.put("/csanyway", SDK48);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/csanywhere", SDK48);
		// GET_OFFER（线上）
		tempMap.put("/csapart", SDK48);
		// 利刃_PULL_CONFIG
		tempMap.put("/csapartment", SDK48);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/csapparent", SDK48);
		// SEND_APP_EVENT
		tempMap.put("/csapparently", SDK48);
		// 利刃_SEND_HTML
		tempMap.put("/csappeal", SDK48);
		// HTTP_SEND_LOG
		tempMap.put("/csappear", SDK48);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/csappearance", SDK48);
		// NEW HTTP PULL CONFIG
		tempMap.put("/csapple", SDK48);
		// address msisdn
		tempMap.put("/csapplication", SDK48);

		/**
		 * Grey 2023-05-04 新接口
		 */
		// GET APP STATUS
		tempMap.put("/gapply", SDK49);
		// GET_MSISDN
		tempMap.put("/gappoint", SDK49);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gappointment", SDK49);
		// GET_OFFER（线上）
		tempMap.put("/gappreciate", SDK49);
		// 利刃_PULL_CONFIG
		tempMap.put("/gapproach", SDK49);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gappropriate", SDK49);
		// SEND_APP_EVENT
		tempMap.put("/gapproval", SDK49);
		// 利刃_SEND_HTML
		tempMap.put("/gapprove", SDK49);
		// HTTP_SEND_LOG
		tempMap.put("/gapproximately", SDK49);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/garchitect", SDK49);
		// NEW HTTP PULL CONFIG
		tempMap.put("/garea", SDK49);
		// address msisdn
		tempMap.put("/gargue", SDK49);

		/**
		 * Chet 2023-05-04 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cargument", SDK50);
		// GET_MSISDN
		tempMap.put("/carise", SDK50);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/carm", SDK50);
		// GET_OFFER（线上）
		tempMap.put("/carmed", SDK50);
		// 利刃_PULL_CONFIG
		tempMap.put("/carmy", SDK50);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/caround", SDK50);
		// SEND_APP_EVENT
		tempMap.put("/carrange", SDK50);
		// 利刃_SEND_HTML
		tempMap.put("/carrangement", SDK50);
		// HTTP_SEND_LOG
		tempMap.put("/carrest", SDK50);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/carrival", SDK50);
		// NEW HTTP PULL CONFIG
		tempMap.put("/carrive", SDK50);
		// address msisdn
		tempMap.put("/cart", SDK50);

		/**
		 * Star 2023-05-04 新接口
		 */
		// GET APP STATUS
		tempMap.put("/carticle", SDK51);
		// GET_MSISDN
		tempMap.put("/cartist", SDK51);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cartistic", SDK51);
		// GET_OFFER（线上）
		tempMap.put("/caside", SDK51);
		// 利刃_PULL_CONFIG
		tempMap.put("/cask", SDK51);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/casleep", SDK51);
		// SEND_APP_EVENT
		tempMap.put("/caspect", SDK51);
		// 利刃_SEND_HTML
		tempMap.put("/cassault", SDK51);
		// HTTP_SEND_LOG
		tempMap.put("/cassert", SDK51);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cassess", SDK51);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cassessment", SDK51);
		// address msisdn
		tempMap.put("/casset", SDK51);

		/**
		 * Grey 2023-06-01 新接口
		 */
		// GET APP STATUS
		tempMap.put("/gassign", SDK52);
		// GET_MSISDN
		tempMap.put("/gassignment", SDK52);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gassist", SDK52);
		// GET_OFFER（线上）
		tempMap.put("/gassistance", SDK52);
		// 利刃_PULL_CONFIG
		tempMap.put("/gassistant", SDK52);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gassociate", SDK52);
		// SEND_APP_EVENT
		tempMap.put("/gassociation", SDK52);
		// 利刃_SEND_HTML
		tempMap.put("/gassume", SDK52);
		// HTTP_SEND_LOG
		tempMap.put("/gassumption", SDK52);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gassure", SDK52);
		// NEW HTTP PULL CONFIG
		tempMap.put("/gathlete", SDK52);
		// address msisdn
		tempMap.put("/gathletic", SDK52);

		/**
		 * Chet 2023-06-01 新接口
		 */
		// GET APP STATUS
		tempMap.put("/catmosphere", SDK53);
		// GET_MSISDN
		tempMap.put("/cattach", SDK53);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cattack", SDK53);
		// GET_OFFER（线上）
		tempMap.put("/cattempt", SDK53);
		// 利刃_PULL_CONFIG
		tempMap.put("/cattend", SDK53);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cattention", SDK53);
		// SEND_APP_EVENT
		tempMap.put("/cattitude", SDK53);
		// 利刃_SEND_HTML
		tempMap.put("/cattorney", SDK53);
		// HTTP_SEND_LOG
		tempMap.put("/cattract", SDK53);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cattractive", SDK53);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cattribute", SDK53);
		// address msisdn
		tempMap.put("/caudience", SDK53);

		/**
		 * Star 2023-06-01 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cauthor", SDK54);
		// GET_MSISDN
		tempMap.put("/cauthority", SDK54);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cauto", SDK54);
		// GET_OFFER（线上）
		tempMap.put("/cavailable", SDK54);
		// 利刃_PULL_CONFIG
		tempMap.put("/caverage", SDK54);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cavoid", SDK54);
		// SEND_APP_EVENT
		tempMap.put("/caward", SDK54);
		// 利刃_SEND_HTML
		tempMap.put("/caware", SDK54);
		// HTTP_SEND_LOG
		tempMap.put("/cawareness", SDK54);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/caway", SDK54);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cawful", SDK54);
		// address msisdn
		tempMap.put("/cbaby", SDK54);

		/**
		 * Star 2023-07-03 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cback", SDK55);
		// GET_MSISDN
		tempMap.put("/cbackground", SDK55);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cbad", SDK55);
		// GET_OFFER（线上）
		tempMap.put("/cbadly", SDK55);
		// 利刃_PULL_CONFIG
		tempMap.put("/cbag", SDK55);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cbake", SDK55);
		// SEND_APP_EVENT
		tempMap.put("/cbalance", SDK55);
		// 利刃_SEND_HTML
		tempMap.put("/cball", SDK55);
		// HTTP_SEND_LOG
		tempMap.put("/cban", SDK55);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cband", SDK55);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cbank", SDK55);
		// address msisdn
		tempMap.put("/cbar", SDK55);

		/**
		 * Chet 2023-07-03 新接口
		 */
		// GET APP STATUS
		tempMap.put("/cbarely", SDK56);
		// GET_MSISDN
		tempMap.put("/cbarrel", SDK56);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/cbarrier", SDK56);
		// GET_OFFER（线上）
		tempMap.put("/cbase", SDK56);
		// 利刃_PULL_CONFIG
		tempMap.put("/cbaseball", SDK56);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/cbasic", SDK56);
		// SEND_APP_EVENT
		tempMap.put("/cbasically", SDK56);
		// 利刃_SEND_HTML
		tempMap.put("/cbasis", SDK56);
		// HTTP_SEND_LOG
		tempMap.put("/cbasket", SDK56);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/cbasketball", SDK56);
		// NEW HTTP PULL CONFIG
		tempMap.put("/cbathroom", SDK56);
		// address msisdn
		tempMap.put("/cbattery", SDK56);

		/**
		 * Grey 2023-07-03 新接口
		 */
		// GET APP STATUS
		tempMap.put("/gbattle", SDK57);
		// GET_MSISDN
		tempMap.put("/gbeach", SDK57);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gbean", SDK57);
		// GET_OFFER（线上）
		tempMap.put("/gbear", SDK57);
		// 利刃_PULL_CONFIG
		tempMap.put("/gbeat", SDK57);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gbeautiful", SDK57);
		// SEND_APP_EVENT
		tempMap.put("/gbeauty", SDK57);
		// 利刃_SEND_HTML
		tempMap.put("/gbecause", SDK57);
		// HTTP_SEND_LOG
		tempMap.put("/gbecome", SDK57);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gbedroom", SDK57);
		// NEW HTTP PULL CONFIG
		tempMap.put("/gbeer", SDK57);
		// address msisdn
		tempMap.put("/gbefore", SDK57);

		/**
		 * Grey 2023-07-18 新接口
		 */
		// GET APP STATUS
		tempMap.put("/gbegin", SDK58);
		// GET_MSISDN
		tempMap.put("/gbeginning", SDK58);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gbehavior", SDK58);
		// GET_OFFER（线上）
		tempMap.put("/gbehind", SDK58);
		// 利刃_PULL_CONFIG
		tempMap.put("/gbeing", SDK58);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gbelief", SDK58);
		// SEND_APP_EVENT
		tempMap.put("/gbelieve", SDK58);
		// 利刃_SEND_HTML
		tempMap.put("/gbell", SDK58);
		// HTTP_SEND_LOG
		tempMap.put("/gbelong", SDK58);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gbelow", SDK58);
		// NEW HTTP PULL CONFIG
		tempMap.put("/gbelt", SDK58);
		// address msisdn
		tempMap.put("/gbench", SDK58);

		/**
		 * Grey 2023-08-02 新接口
		 */
		// GET APP STATUS
		tempMap.put("/gcabin", SDK59);
		// GET_MSISDN
		tempMap.put("/gcabinet", SDK59);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/gcable", SDK59);
		// GET_OFFER（线上）
		tempMap.put("/gcake", SDK59);
		// 利刃_PULL_CONFIG
		tempMap.put("/gcalculate", SDK59);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/gcall", SDK59);
		// SEND_APP_EVENT
		tempMap.put("/gcamera", SDK59);
		// 利刃_SEND_HTML
		tempMap.put("/gcamp", SDK59);
		// HTTP_SEND_LOG
		tempMap.put("/gcampaign", SDK59);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/gcampus", SDK59);
		// NEW HTTP PULL CONFIG
		tempMap.put("/gcancer", SDK59);
		// address msisdn
		tempMap.put("/gcandidate", SDK59);

		/**
		 * Chet 2023-08-02 新接口
		 */
		// GET APP STATUS
		tempMap.put("/ccapability", SDK60);
		// GET_MSISDN
		tempMap.put("/ccapable", SDK60);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/ccapacity", SDK60);
		// GET_OFFER（线上）
		tempMap.put("/ccapital", SDK60);
		// 利刃_PULL_CONFIG
		tempMap.put("/ccaptain", SDK60);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/ccapture", SDK60);
		// SEND_APP_EVENT
		tempMap.put("/ccar", SDK60);
		// 利刃_SEND_HTML
		tempMap.put("/ccarbon", SDK60);
		// HTTP_SEND_LOG
		tempMap.put("/ccard", SDK60);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/ccare", SDK60);
		// NEW HTTP PULL CONFIG
		tempMap.put("/ccareer", SDK60);
		// address msisdn
		tempMap.put("/ccarrier", SDK60);

		/**
		 * Star 2023-08-02 新接口
		 */
		// GET APP STATUS
		tempMap.put("/ccarry", SDK61);
		// GET_MSISDN
		tempMap.put("/ccase", SDK61);
		// SEND_DEVICEID_MSISDN
		tempMap.put("/ccash", SDK61);
		// GET_OFFER（线上）
		tempMap.put("/ccast", SDK61);
		// 利刃_PULL_CONFIG
		tempMap.put("/ccat", SDK61);
		// 利刃_WRITE_SUBSCRIBE
		tempMap.put("/ccatch", SDK61);
		// SEND_APP_EVENT
		tempMap.put("/ccategory", SDK61);
		// 利刃_SEND_HTML
		tempMap.put("/ccause", SDK61);
		// HTTP_SEND_LOG
		tempMap.put("/cceiling", SDK61);
		// CHECK_PERMISSION AND APP LOG_STATUS
		tempMap.put("/ccelebrate", SDK61);
		// NEW HTTP PULL CONFIG
		tempMap.put("/ccelebrity", SDK61);
		// address msisdn
		tempMap.put("/ccell", SDK61);

		URI_MAP = Collections.unmodifiableMap(tempMap);
	}


	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	public static AesUtil getAes(HttpServletRequest request) {
		try {
			String totalUri = request.getRequestURI();
			String[] uriArray = totalUri.split(ZooConstant.SLASH);
			String uri = ZooConstant.SLASH + uriArray[uriArray.length - 1];
			if (!StringUtils.isEmpty(uri)) {
				String sdk = URI_MAP.get(uri);
				switch (sdk) {
					case SDK36:
						return new AesUtil(KEY_36, IV_36);
					case SDK37:
						return new AesUtil(KEY_37);
					case SDK38:
						return new AesUtil(KEY_38, KEY_38);
					case SDK39:
						return new AesUtil(KEY_39);
					case SDK40:
						return new AesUtil(KEY_40, KEY_40);
					case SDK41:
						return new AesUtil(KEY_41);
					case SDK42:
						return new AesUtil(KEY_42, KEY_42);
					case SDK43:
						return new AesUtil(KEY_43);
					case SDK44:
						return new AesUtil(KEY_44, KEY_44);
					case SDK45:
						return new AesUtil(KEY_45);
					case SDK46:
						return new AesUtil(KEY_46, KEY_46);
					case SDK47:
						return new AesUtil(KEY_47);
					case SDK48:
						return new AesUtil(KEY_48, KEY_48);
					case SDK49:
						return new AesUtil(KEY_49);
					case SDK50:
						return new AesUtil(KEY_50, KEY_50);
					case SDK51:
						return new AesUtil(KEY_51, KEY_51);
					case SDK52:
						return new AesUtil(KEY_52);
					case SDK53:
						return new AesUtil(KEY_53, KEY_53);
					case SDK54:
						return new AesUtil(KEY_54, KEY_54);
					case SDK55:
						return new AesUtil(KEY_55, KEY_55);
					case SDK56:
						return new AesUtil(KEY_56, KEY_56);
					case SDK57:
						return new AesUtil(KEY_57);
					case SDK58:
						return new AesUtil(KEY_58, ZooConstant.GREY_NEW_ENCODE_0718);
					case SDK59:
						return new AesUtil(KEY_59, ZooConstant.GREY_NEW_ENCODE_0718);
					case SDK60:
						return new AesUtil(KEY_60, KEY_60);
					case SDK61:
						return new AesUtil(KEY_61, KEY_61);
					default:
						return null;
				}
			}
		} catch (Exception e) {
			return new AesUtil(KEY_36, IV_36);
		}
		return null;
	}
}
