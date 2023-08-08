package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AffSmartLinkModel;
import com.starp.zoo.service.ISmartLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@RestController
@RequestMapping("/config/stl")
public class SmartLinkConfigController {

    @Autowired
    private ISmartLinkService smartLinkService;

    @PostMapping("/save")
    public ResponseInfo save(@RequestBody AffSmartLinkModel affSmartLinkModel){
        smartLinkService.saveConfig(affSmartLinkModel);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/get/{id}")
    public ResponseInfo get(@PathVariable String id){
        return ResponseInfoUtil.success(smartLinkService.getById(id));
    }

    @GetMapping("/list")
    public ResponseInfo list(@RequestParam int page, @RequestParam int limit,
                             @RequestParam(required = false) String name, @RequestParam(required = false) String affId,
                             @RequestParam(required = false) String id){
        return ResponseInfoUtil.success(smartLinkService.getList(page, limit, name, affId, id));
    }

    @GetMapping("/delete/{id}")
    public ResponseInfo delete(@PathVariable String id){
        smartLinkService.deleteById(id);
        return ResponseInfoUtil.success();
    }


    @GetMapping("/names")
    public ResponseInfo names(){
        return ResponseInfoUtil.success(smartLinkService.getAllSmartLinkNames());
    }

    @GetMapping("/ids")
    public ResponseInfo ids(){
        return ResponseInfoUtil.success(smartLinkService.getAllSmartLinkIds());
    }

    @PostMapping("/tag/init/deduct")
    public ResponseInfo getInitDeduct(@RequestBody List<String> tagIds) {
        return ResponseInfoUtil.success(smartLinkService.getInitDeductModels(tagIds));
    }

    @GetMapping(value = "/check/id")
    public ResponseInfo checkId(@RequestParam String id){
        return ResponseInfoUtil.success(smartLinkService.checkUniqueId(id));
    }
}
