package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AffiliateModel;
import com.starp.zoo.service.IAffiliateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@RestController
@RequestMapping("/config/affiliate")
public class AffiliateConfigController {

    @Autowired
    private IAffiliateService affiliateService;

    @PostMapping("/save")
    public ResponseInfo save(@RequestBody AffiliateModel affiliateModel){
        affiliateService.save(affiliateModel);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/get/{id}")
    public ResponseInfo get(@PathVariable String id){
        return ResponseInfoUtil.success(affiliateService.getById(id));
    }

    @GetMapping("/list")
    public ResponseInfo list(@RequestParam int page, @RequestParam int limit,
                             @RequestParam(required = false) String name){
        return ResponseInfoUtil.success(affiliateService.getList(page, limit, name));
    }

    @GetMapping("/delete/{id}")
    public ResponseInfo delete(@PathVariable String id){
        affiliateService.deleteById(id);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/names")
    public ResponseInfo names(){
        return ResponseInfoUtil.success(affiliateService.getAllNames());
    }

    @GetMapping("/options")
    public ResponseInfo options(){
        return ResponseInfoUtil.success(affiliateService.getAll());
    }
}
