package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.VideoInfoModel;
import com.starp.zoo.service.VideoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author curry by 2023/7/4
 */
@RestController
public class VideoInfoController {

	@Autowired
	VideoInfoService videoInfoService;

	@PostMapping("/upload/videoInfo")
	public ResponseInfo uploadVideoInfo(@RequestParam("videoFile") MultipartFile videoFile, @RequestParam("imageFile") MultipartFile imageFile, @RequestParam String dataJson) {
		return ResponseInfoUtil.success(videoInfoService.uploadVideoInfo(videoFile, imageFile, dataJson));
	}

	@PostMapping("/save/videoInfo")
	public ResponseInfo saveVideoInfo(@RequestBody VideoInfoModel model) {
		videoInfoService.saveVideoInfo(model);
		return ResponseInfoUtil.success();
	}

	@GetMapping("/get/videoInfo/{id}")
	public ResponseInfo getVideoInfo(@PathVariable String id) {
		VideoInfoModel model = videoInfoService.getVideoInfo(id);
		return ResponseInfoUtil.success(model);
	}

	@PostMapping("/get/videoInfoList")
	public ResponseInfo getVideoInfoList(@RequestBody JSONObject jsonObject) {
		String type = jsonObject.getString("type");
		Integer page = jsonObject.getInteger("page");
		Integer limit = jsonObject.getInteger("limit");
		return ResponseInfoUtil.success(videoInfoService.getVideoInfoList(type, page, limit));
	}

	@GetMapping("random/get/videoInfo/{type}")
	public ResponseInfo randomGetVideoInfoList(@PathVariable String type) {
		return ResponseInfoUtil.success(videoInfoService.randomGetVideoInfoList(type));
	}

	@GetMapping("/save/videoType")
	public ResponseInfo saveVideoType(@RequestParam String name) {
		videoInfoService.saveVideoType(name);
		return ResponseInfoUtil.success();
	}

	@PostMapping(value = "/multiDelete/videoInfo")
	public ResponseInfo multiDelete(@RequestBody List<String> ids){
		videoInfoService.multiDelete(ids);
		return ResponseInfoUtil.success();
	}

	@GetMapping("/get/videoTypeList")
	public ResponseInfo getVideoTypeList(@RequestParam Integer page, @RequestParam Integer limit) {
		return ResponseInfoUtil.success(videoInfoService.getVideoTypeList(page, limit));
	}

	@GetMapping("/delete/videoType")
	public ResponseInfo deleteVideoType(@RequestParam String type) {
		videoInfoService.deleteVideoType(type);
		return ResponseInfoUtil.success();
	}
}
