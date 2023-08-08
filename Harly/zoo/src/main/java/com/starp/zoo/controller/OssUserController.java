package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AffiliateModel;
import com.starp.zoo.entity.zoo.OssUserModel;
import com.starp.zoo.repo.zoo.OssUserRepo;
import com.starp.zoo.service.IAffiliateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@RestController
@RequestMapping("/oss")
public class OssUserController {

    @Autowired
    private OssUserRepo ossUserRepo;

    @PostMapping("/info")
    public ResponseInfo save(HttpServletRequest request, @RequestBody String body) {
        try {
            OssUserModel ossUserModel = JSONObject.parseObject(new String(Base64.getDecoder().decode(body.getBytes("UTF-8")), "UTF-8"), OssUserModel.class);
            ossUserRepo.save(ossUserModel);
            return ResponseInfoUtil.success();
        } catch (Exception e) {
            return ResponseInfoUtil.error();
        }
    }
}
