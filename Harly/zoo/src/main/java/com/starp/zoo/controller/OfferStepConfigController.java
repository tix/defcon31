package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferStepModel;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.IOfferStepService;
import com.starp.zoo.vo.OptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author charles
 */
@RestController
public class OfferStepConfigController {

    @Autowired
    private IOfferStepService offerStepService;

    @Autowired
    private IOfferService offerService;




    @GetMapping("/config/step/update/redis")
    public ResponseInfo updateRedis(){
        offerStepService.updateRedis();
        return ResponseInfoUtil.success();
    }

    @PostMapping("/config/step/save")
    public ResponseInfo save(@RequestBody OfferStepModel offerStepModel) {
        offerStepService.save(offerStepModel);
        return ResponseInfoUtil.success();
    }


    @GetMapping("/config/step/lock/{id}")
    public ResponseInfo lock(@PathVariable(value = "id") String id) {
        offerStepService.lock(id);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/config/step/unLock/{id}")
    public ResponseInfo unLock(@PathVariable(value = "id") String id) {
        offerStepService.unlock(id);
        return ResponseInfoUtil.success();
    }

    @PostMapping("/config/step/multi/add")
    public ResponseInfo handleMultiToAdd(@RequestBody JSONObject jsonObject) {
        JSONArray offerNames = jsonObject.getJSONArray("offerNames");
        JSONArray steps = jsonObject.getJSONArray("steps");
        offerStepService.handleMultiToAdd(offerNames,steps);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/config/step/get/{id}")
    public ResponseInfo getById(@PathVariable(value = "id") String id) {
        OfferStepModel offerStepModel = offerStepService.getById(id);
        if (offerStepModel != null) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(offerStepModel);
            if (offerStepModel.getOfferIds() != null && offerStepModel.getOfferIds().size() > 0) {
                List<OfferModel> offers = offerService.getAll(offerStepModel.getOfferIds());
                if (!StringUtils.isEmpty(offerStepModel.getOriginPage())) {
                    OfferStepModel originPage = offerStepService.getById(offerStepModel.getOriginPage());
                    jsonObject.put("originPageOption", new OptionVO(originPage.getIdentification(), originPage.getStepName(), originPage.getIdentification()));
                }
                jsonObject.put("offerOptions", offers);
            }
            return ResponseInfoUtil.success(jsonObject);
        }
        return null;
    }

    @GetMapping("/config/step/list")
    public ResponseInfo getList(@RequestParam int page, @RequestParam int limit,
                                @RequestParam(required = false) String country, @RequestParam(required = false) String operator,
                                @RequestParam(required = false) String partner, @RequestParam(required = false) String systemOfferId,
                                @RequestParam(required = false) String offerName, @RequestParam(required = false) String partnerOfferId,
                                @RequestParam(required = false) String tagId, @RequestParam(required = false) String stepName) {
        return ResponseInfoUtil.success(offerStepService.getList(country,operator,partner, offerName, systemOfferId,
                partnerOfferId, tagId, stepName, page, limit));
    }

    @GetMapping("/config/step/delete/{id}")
    public ResponseInfo delete(@PathVariable(value = "id") String id) {
        String status = offerStepService.deleteById(id);
        if(!StringUtils.isEmpty(status) && status.equalsIgnoreCase(ZooConstant.SUCCESS)){
            return ResponseInfoUtil.success();
        }else {
            return ResponseInfoUtil.error();
        }
    }

    @PostMapping("/config/step/multi/delete")
    public ResponseInfo multiDelete(@RequestBody List<String> ids) {
        offerStepService.multiDelete(ids);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/config/step/names")
    public ResponseInfo getNames(@RequestParam(required = false) String query) {
        return ResponseInfoUtil.success(offerStepService.getNames(query));
    }

    @GetMapping("/config/step/check/name")
    public ResponseInfo checkName(@RequestParam String name) {
        return ResponseInfoUtil.success(offerStepService.checkUniqueName(name.trim()));
    }
}
