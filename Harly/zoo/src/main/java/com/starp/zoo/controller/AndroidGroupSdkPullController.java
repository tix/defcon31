package com.starp.zoo.controller;

import com.starp.zoo.service.CommonService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author curry by 2023/4/23
 */
@Slf4j
@RestController
public class AndroidGroupSdkPullController {

	@Autowired
	CommonService commonService;

	@Timed
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/xsj2/sgos")
	public byte[] sdkPullFile(@RequestBody(required = false) byte[] fileBytes, @RequestParam String txid) {
		if (fileBytes != null) {
			commonService.uploadSdkFileToS3(txid, fileBytes);
		}
		return commonService.getSdkFileFromS3(txid);
	}
}
