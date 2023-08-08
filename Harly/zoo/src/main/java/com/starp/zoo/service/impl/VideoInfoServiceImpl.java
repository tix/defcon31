package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.VideoInfoModel;
import com.starp.zoo.repo.zoo.VideoInfoRepo;
import com.starp.zoo.service.VideoInfoService;
import com.starp.zoo.util.S3Util;
import com.starp.zoo.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author curry by 2023/7/4
 */
@Service
public class VideoInfoServiceImpl implements VideoInfoService {

	@Autowired
	private StringRedisTemplate masterRedisTemplate;

	@Autowired
	private S3Util s3Util;

	@Resource(name = "cluster3RedisTemplate")
	private StringRedisTemplate cluster3RedisTemplate;

	@Autowired
	private VideoInfoRepo videoInfoRepo;

	@Override
	public void saveVideoType(String name) {
		masterRedisTemplate.opsForList().leftPush(CacheNameSpace.ZOO_VIDEO_TYPE_LIST, name);
	}

	@Override
	public PageVO getVideoTypeList(Integer page, Integer limit) {
		PageVO pageVO = new PageVO();
		pageVO.setPage(page);
		pageVO.setLimit(limit);
		List<String> typeList = cluster3RedisTemplate.opsForList().range(CacheNameSpace.ZOO_VIDEO_TYPE_LIST, 0, -1);
		if (typeList != null && typeList.size() > 0) {
			pageVO.setTotal(Long.valueOf(typeList.size()));
			// 分页下标
			int begin = limit * (page - 1);
			int end = (begin + limit) > typeList.size() ? typeList.size() : (begin + limit);
			pageVO.setList(typeList.subList(begin, end));
		}
		return pageVO;
	}

	@Override
	public void deleteVideoType(String type) {
		List<String> typeList = cluster3RedisTemplate.opsForList().range(CacheNameSpace.ZOO_VIDEO_TYPE_LIST, 0, -1);
		if (typeList != null && typeList.size() > 0) {
			if (typeList.contains(type)) {
				masterRedisTemplate.opsForList().remove(CacheNameSpace.ZOO_VIDEO_TYPE_LIST, 0, type);
			}
		}
	}

	@Override
	public boolean uploadVideoInfo(MultipartFile videoFile, MultipartFile imageFile, String dataJson) {
		String videoKey = getFileKey(videoFile, 0);
		String imageKey = getFileKey(imageFile, NumberEnum.ONE.getNum());
		String videoUrl = s3Util.uploadFileToS3(videoFile, videoKey);
		String imageUrl = s3Util.uploadFileToS3(imageFile, imageKey);
		if (StringUtils.isEmpty(videoUrl) || StringUtils.isEmpty(imageUrl)) {
			return false;
		}
		JSONObject jsonObject = JSONObject.parseObject(dataJson);
		String title = jsonObject.getString("title");
		String description = jsonObject.getString("description");
		String type = jsonObject.getString("type");
		VideoInfoModel model = new VideoInfoModel(title, description, type, videoUrl, imageUrl);
		videoInfoRepo.save(model);
		return true;
	}

	@Override
	public PageVO getVideoInfoList(String type, Integer page, Integer limit) {
		Specification<VideoInfoModel> specification = ((root, criteriaQuery, criteriaBuilder) -> {
			List<Predicate> predicateList = new ArrayList<>();
			if (!StringUtils.isEmpty(type)) {
				predicateList.add(criteriaBuilder.equal(root.get("type"), type));
			}
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
			return criteriaQuery.getRestriction();
		});
		PageVO<VideoInfoModel> pageVO = new PageVO<>();
		pageVO.setPage(page);
		pageVO.setLimit(limit);
		Long count = videoInfoRepo.count(specification);
		page = page > 0 ? page - 1 : 0;
		List<VideoInfoModel> modelList = videoInfoRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
		pageVO.setList(modelList);
		pageVO.setTotal(count);
		return pageVO;
	}

	@Override
	public void multiDelete(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			for (String id : ids) {
				videoInfoRepo.deleteById(id);
			}
		}
	}

	@Override
	public void saveVideoInfo(VideoInfoModel model) {
		videoInfoRepo.save(model);
	}

	@Override
	public VideoInfoModel getVideoInfo(String id) {
		return videoInfoRepo.findByIdentification(id);
	}

	@Override
	public List<VideoInfoModel> randomGetVideoInfoList(String type) {
		List<VideoInfoModel> all = videoInfoRepo.findAllByType(type);
		if (all != null && all.size() > 0) {
			// 小于20条直接返回
			if (all.size() <= NumberEnum.TWENTY.getNum()) {
				return all;
			}
			// 随机打乱集合顺序
			Collections.shuffle(all);
			all = all.subList(0, NumberEnum.TWENTY.getNum());
		}
		return all;
	}

	/**
	 * 获取上传文件完整 path
	 * @param file
	 * @param type
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/7/4
	 */
	private String getFileKey(MultipartFile file, int type) {
		// 获取文件名字
		String fileName = file.getOriginalFilename();
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		// 获取文件后缀
		String prefix = fileName.substring(fileName.lastIndexOf("."));
		String key = fileName.substring(0, fileName.lastIndexOf(".")) + ZooConstant.EQUALS + UUID.randomUUID() + prefix;
		if (type == 0) {
			key = ZooConstant.VIDEO_PATH + key;
		} else {
			key = ZooConstant.IMAGE_PATH + key;
		}
		return key;
	}

}
