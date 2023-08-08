package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.MsisdnParamModel;
import com.starp.zoo.service.IMsisdnParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Charles
 * @date 2019/5/6
 * @description :
 */
@RestController
@RequestMapping("/config/msisdn/param")
public class MsisdnParamController {

    @Autowired
    private IMsisdnParamService msisdnParamService;

    @PostMapping("/save")
    public ResponseInfo save(@RequestBody MsisdnParamModel msisdnParamModel){
        msisdnParamService.save(msisdnParamModel);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/get/{id}")
    public ResponseInfo get(@PathVariable String id){
        return ResponseInfoUtil.success(msisdnParamService.getById(id));
    }

    @GetMapping("/list")
    public ResponseInfo getList(@RequestParam int page, @RequestParam int limit,
                                @RequestParam(required = false) String country, @RequestParam(required = false) String operator){
        return ResponseInfoUtil.success(msisdnParamService.getList(page, limit, country, operator));
    }

    @GetMapping("/delete/{id}")
    public ResponseInfo delete(@PathVariable String id){
        msisdnParamService.delete(id);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/multi/delete/{id}")
    public ResponseInfo multiDelete(@RequestBody List<String> ids){
        if(ids != null && ids.size() > 0) {
            for(String id : ids){
                msisdnParamService.delete(id);
            }
        }
        return ResponseInfoUtil.success();
    }
}
