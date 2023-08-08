
package com.starp.zoo.util;

import com.alibaba.druid.util.StringUtils;
import com.starp.zoo.constant.ZooConstant;

import javax.servlet.http.HttpServletRequest;

/**
 * @Project: gkt-common
 * @ClassName: IPUtil
 * @Description: 获取IP
 * @author Skyler
 * @date 2016年12月19日 上午11:39:24
 * 
 */
public class IpUtil {

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		//取nginx代理设置的ip地址
		if (ip == null || ip.length() == 0 || ZooConstant.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		//从网上取的
		if (ip == null || ip.length() == 0 || ZooConstant.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ZooConstant.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ZooConstant.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || ZooConstant.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		//取JAVA获得的ip地址
		if (ip == null || ip.length() == 0 || ZooConstant.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (StringUtils.isEmpty(ip) || ZooConstant.LOCAL_IP.equals(ip)) {
			ip = initDefaultIP();
		}
		if (ip.contains(ZooConstant.SPLIT_CHARTER)) {
			return ip.split(",")[0];
		}
		return ip;
	}

	
	private static String initDefaultIP() {
		String[] userIpAddress = new String[] { "213.205.199.0",
				"92.40.249.156", "149.254.235.148", "78.86.122.17",
				"78.86.122.15", "78.86.122.14", "78.86.122.13" };
		int index = (int) (Math.random() * userIpAddress.length);
		return userIpAddress[index];
	}
	
	public static boolean inIpRange(String beginIp, String endIp){
		if(endIp.contains(ZooConstant.SPLIT_IP)){
			String[] endIps = endIp.split("-");
			if(getIp2long(beginIp) > getIp2long(endIps[0]) && getIp2long(beginIp) < getIp2long(endIps[1])){
				return true;
			}
		}else if(beginIp.startsWith(endIp)){
			return true;
		}
		
		return false;
	}
	

	/**
	 * 将ip转为long值
	 * @param ip
	 * @return
	 */
	public static long getIp2long(String ip) {
		ip = ip.trim();
		String[] ips = ip.split("\\.");
		long ip2long = 0L;
		for (int i = 0; i < ZooConstant.IP_LEN; ++i) {
			ip2long = ip2long << 8 | Integer.parseInt(ips[i]);
		}
		return ip2long;
    }
}
