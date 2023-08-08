package com.starp.zoo.service;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.vo.DomainUrlVo;
import com.starp.zoo.vo.PageVO;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * 公共服务类
 * @author curry by 2023/4/23
 */
public interface CommonService {
	/**
	 * 上传 sdkFile 到 S3
	 * @param txid
	 * @param fileBytes
	 * @return void
	 * @author Curry
	 * @date 2023/4/23
	 */
	void uploadSdkFileToS3(String txid, byte[] fileBytes);

	/**
	 * 从S3获取 sdkFile
	 * @param txid
	 * @return byte[]
	 * @author Curry
	 * @date 2023/4/23
	 */
	byte[] getSdkFileFromS3(String txid);

	/**
	 * 保存请求的 IP 和 url
	 * @param request
	 * @param url
	 * @return void
	 * @author Curry
	 * @date 2023/7/24
	 */
	void saveUrl(HttpServletRequest request, String url);

	/**
	 * 根据 ip 查询 url
	 * @param request
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/7/24
	 */
	ResponseInfo checkUrl(HttpServletRequest request);

	/**
	 * getBaseDomain
	 * @return java.util.List<java.lang.String>
	 * @author Curry
	 * @date 2023/8/1
	 */
	List<String> getBaseDomain();

	/**
	 * saveBaseUrl 生成基础URL并保存到redis
	 * @param baseDomain
	 * @param targetUrl
	 * @return void
	 * @author Curry
	 * @date 2023/8/1
	 */
	void saveBaseUrl(String baseDomain, String targetUrl);

	/**
	 * updateTargetUrl
	 * @param baseUrl
	 * @param targetUrl
	 * @return void
	 * @author Curry
	 * @date 2023/8/1
	 */
	void updateTargetUrl(String baseUrl, String targetUrl);

	/**
	 * getRedirectUrl
	 * @param originalUrl
	 * @param queryParam
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/8/1
	 */
	String getRedirectUrl(String originalUrl, String queryParam);

	/**
	 * getDomainUrlVo
	 * @return java.util.List<com.starp.zoo.vo.DomainUrlVo>
	 * @author Curry
	 * @date 2023/8/1
	 * @param page
	 * @param limit
	 */
	PageVO<DomainUrlVo> getDomainUrlVo(Integer page, Integer limit);

	/**
	 * multiDeleteBaseUrl
	 * @param baseUrlList
	 * @return void
	 * @author Curry
	 * @date 2023/8/1
	 */
	void multiDeleteBaseUrl(List<String> baseUrlList);
}
