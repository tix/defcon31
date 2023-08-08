package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.IpUrlModel;
import com.starp.zoo.repo.zoo.IpUrlRepo;
import com.starp.zoo.service.CommonService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.IpUtil;
import com.starp.zoo.util.RandomUtil;
import com.starp.zoo.util.S3Util;
import com.starp.zoo.vo.DomainUrlVo;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 公共服务实现类
 * @author curry by 2023/4/23
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

	@Value("${sdkPullFile.s3.path}")
	String sdkFilePath;

	@Value("${sdkPullFile.s3.bucket}")
	String sdkFileBucket;

	@Autowired
	private StringRedisTemplate masterRedisTemplate;

	@Resource(name = "cluster3RedisTemplate")
	private StringRedisTemplate cluster3RedisTemplate;

	@Autowired
	S3Util s3Util;

	@Autowired
	IpUrlRepo ipUrlRepo;

	@Override
	public void uploadSdkFileToS3(String txid, byte[] fileBytes) {
		String key = sdkFilePath + txid;
		try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
			s3Util.uploadFileToS3(sdkFileBucket, key, inputStream);
		} catch (Exception e) {
			log.error("fileBytes convert inputStream error:{}", e.getMessage());
		}
	}

	@Override
	public byte[] getSdkFileFromS3(String txid) {
		String key = sdkFilePath + txid;
		return s3Util.downloadFileToS3(sdkFileBucket, key);
	}

	@Override
	public void saveUrl(HttpServletRequest request, String url) {
		String ip = IpUtil.getIpAddr(request);
		ipUrlRepo.save(new IpUrlModel(ip, url));
	}

	@Override
	public ResponseInfo checkUrl(HttpServletRequest request) {
		String ip = IpUtil.getIpAddr(request);
		IpUrlModel model = ipUrlRepo.findFirstByIpOrderByCreateTimeDesc(ip);
		if (model != null && StringUtils.hasText(model.getUrl())) {
			return ResponseInfoUtil.success(model.getUrl());
		} else {
			return ResponseInfoUtil.error(ip);
		}
	}

	@Override
	public List<String> getBaseDomain() {
		return Constant.ZOO_JUMP_DOMAIN_LIST;
	}

	@Override
	public void saveBaseUrl(String baseDomain, String targetUrl) {
		String randomUrl = DateUtil.formatMonDayString(new Date()) + RandomUtil.generateRandomString(NumberEnum.FOUR.getNum()) + ZooConstant.SLASH + RandomUtil.generateRandomString(NumberEnum.SIX.getNum());
		String baseUrl = ZooConstant.HTTP + baseDomain + ZooConstant.SLASH + RandomUtil.getInterFaceName() + ZooConstant.SLASH + randomUrl;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ZooConstant.CREATE_TIME, DateUtil.getDateStr(System.currentTimeMillis()));
		jsonObject.put(ZooConstant.TARGET_URL, targetUrl);
		masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_JUMP_DOMAIN_URL, baseUrl, JSONObject.toJSONString(jsonObject));
	}

	@Override
	public void updateTargetUrl(String baseUrl, String targetUrl) {
		JSONObject jsonObject = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_JUMP_DOMAIN_URL, baseUrl)));
		jsonObject.put(ZooConstant.TARGET_URL, targetUrl);
		masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_JUMP_DOMAIN_URL, baseUrl, JSONObject.toJSONString(jsonObject));
	}

	@Override
	public String getRedirectUrl(String originalUrl, String queryParam) {
		originalUrl = originalUrl.replaceAll(ZooConstant.HTTPS, ZooConstant.HTTP);
		Object object = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_JUMP_DOMAIN_URL, originalUrl);
		if (StringUtils.isEmpty(object)) {
			return null;
		}
		JSONObject jsonObject = JSONObject.parseObject(String.valueOf(object));
		String redirectUrl = jsonObject.getString(ZooConstant.TARGET_URL);
		if (StringUtils.hasText(queryParam)) {
			redirectUrl += "?" + queryParam;
		}
		return redirectUrl;
	}

	@Override
	public PageVO<DomainUrlVo> getDomainUrlVo(Integer page, Integer limit) {
		PageVO<DomainUrlVo> pageVO = new PageVO<>();
		List<DomainUrlVo> list = new ArrayList<>();
		Map<Object, Object> entries = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_JUMP_DOMAIN_URL);
		for (Map.Entry<Object, Object> entry : entries.entrySet()) {
			DomainUrlVo vo = new DomainUrlVo();
			vo.setBaseUrl(String.valueOf(entry.getKey()));
			JSONObject jsonObject = JSONObject.parseObject(String.valueOf(entry.getValue()));
			vo.setCreateTime(jsonObject.getString(ZooConstant.CREATE_TIME));
			vo.setTargetUrl(jsonObject.getString(ZooConstant.TARGET_URL));
			list.add(vo);
		}
		int begin = limit * (page - 1);
		int end = (begin + limit) > list.size() ? list.size() : (begin + limit);
		pageVO.setList(list.subList(begin, end));
		pageVO.setTotal(Long.valueOf(list.size()));
		pageVO.setPage(page);
		pageVO.setLimit(limit);
		return pageVO;
	}

	@Override
	public void multiDeleteBaseUrl(List<String> baseUrlList) {
		for (String key : baseUrlList) {
			masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_JUMP_DOMAIN_URL, key);
		}
	}
}
