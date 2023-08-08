package com.starp.zoo.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author vic
 * @Date 18:12 2018/12/18
 * @param
 * @return
 **/
public class CodeUtil {
	public final static String CODE_TEXT = "code";
	public final static String MESSAGE_TEXT = "message";
	
	public final static String CODE_SUCCESS = "200";
	public final static String CODE_SUCCESS_MESSAGE = "Success";
	
	public final static String CODE_BAD_REQUEST = "400";
	public final static String CODE_BAD_REQUEST_MESSAGE = "Bad request,Request parameter is null or error";
	
	public final static String CODE_UNAUTHORIZED = "401";
	public final static String CODE_UNAUTHORIZED_MESSAGE = "Permission denied";
	
	public final static String CODE_NOT_FOUND = "404";
	public final static String CODE_NOT_FOUND_MESSAGE = "Request or page is not found";
	
	public final static String CODE_SAVE_USER_ERROR = "503";
	public final static String CODE_SAVE_USER_ERROR_MESSAGE = "Save user error,May be you have been saved or some parm is null!";
	
	public final static String CODE_UPDATE_USER_ERROR = "504";
	public final static String CODE_UPDATE_USER_ERROR_MESSAGE = "Update user error,May be you have been saved or some parm is null!";
	
	/** App User 没有找到 */
	public final static String CODE_USER_NOT_FOUND_ERROR = "505";
	public final static String CODE_USER_NOT_FOUND_ERROR_MESSAGE = "App user not found!";
	
	public final static String CODE_NOT_TASK_ISNUMBER = "600";
	public final static String CODE_NOT_TASK_ISNUMBER_MESSAGE = "No task,User is number";
	
	public final static String CODE_NOT_TASK_NOTTIME = "601";
	public final static String CODE_NOT_TASK_NOTTIME_MESSAGE = "No task,Payment time has not arrived";
	
	public final static String CODE_NOT_TASK_NOSIMCARD = "602";
	public final static String CODE_NOT_TASK_NOSIMCARD_MESSAGE = "No task,User has not sim card";
	
	public final static String CODE_NOT_TASK_SERVERE_CONF_ERROR = "603";
	public final static String CODE_NOT_TASK_SERVERE_CONF_ERROR_MESSAGE = "No task,Server configure is error";
	
	public final static String CODE_NOT_TASK_NO_SHORT_CODE = "604";
	public final static String CODE_NOT_TASK_NO_SHORT_CODE_MESSAGE = "No task,ShortCode is not found,please check your parm";
	
	public final static String CODE_SAVE_WEBUSER_CODE = "605";
	public final static String CODE_SAVE_WEBUSER_CODE_MESSAGE = "Save web user referer error";
	
	public final static String CODE_PAY_CHECK_OPERATOR = "606";
	public final static String CODE_PAY_CHECK_OPERATOR_MESSAGE = "Does not support your phone operator to pay";
	
	public static Map<String, String> getBadRequestMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_BAD_REQUEST);
		map.put(MESSAGE_TEXT, CODE_BAD_REQUEST_MESSAGE);
		return map;
	}

	public static Map<String, String> getUnauthorizedMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_UNAUTHORIZED);
		map.put(MESSAGE_TEXT, CODE_UNAUTHORIZED_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getSuccessMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_SUCCESS);
		map.put(MESSAGE_TEXT, CODE_SUCCESS_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getNoTaskIsNumberMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_NOT_TASK_ISNUMBER);
		map.put(MESSAGE_TEXT, CODE_NOT_TASK_ISNUMBER_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getNoTaskNotTimeMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_NOT_TASK_NOTTIME);
		map.put(MESSAGE_TEXT, CODE_NOT_TASK_NOTTIME_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getNoTaskNotSimCardMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_NOT_TASK_NOSIMCARD);
		map.put(MESSAGE_TEXT, CODE_NOT_TASK_NOSIMCARD_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getNoTaskServerConfErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_NOT_TASK_SERVERE_CONF_ERROR);
		map.put(MESSAGE_TEXT, CODE_NOT_TASK_SERVERE_CONF_ERROR_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getNoTaskNoShortCodeFoundErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_NOT_TASK_NO_SHORT_CODE);
		map.put(MESSAGE_TEXT, CODE_NOT_TASK_NO_SHORT_CODE_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getSaveUserErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_SAVE_USER_ERROR);
		map.put(MESSAGE_TEXT, CODE_SAVE_USER_ERROR_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getSaveWebUserErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_SAVE_WEBUSER_CODE);
		map.put(MESSAGE_TEXT, CODE_SAVE_WEBUSER_CODE_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getUpdateUserErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_UPDATE_USER_ERROR);
		map.put(MESSAGE_TEXT, CODE_UPDATE_USER_ERROR_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getUserNotFoundErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_USER_NOT_FOUND_ERROR);
		map.put(MESSAGE_TEXT, CODE_USER_NOT_FOUND_ERROR_MESSAGE);
		return map;
	}
	
	public static Map<String, String> getPayCheckOperatorErrorMap(){
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_PAY_CHECK_OPERATOR);
		map.put(MESSAGE_TEXT, CODE_PAY_CHECK_OPERATOR_MESSAGE);
		return map;
	}
}
