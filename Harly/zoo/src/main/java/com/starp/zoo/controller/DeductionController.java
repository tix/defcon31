package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.DeductionModel;
import com.starp.zoo.repo.zoo.AffPostBackRepo;
import com.starp.zoo.service.IDeductionService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.TransformVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
@RestController
public class DeductionController {

    @Autowired
    IDeductionService deductionService;

    @Autowired
    AffPostBackRepo postBackRepo;

    @RequestMapping("/deduction/operators")
    public ResponseInfo fetchOperators(){
        List<OptionVO> operators = deductionService.fetchOperators();
        return ResponseInfoUtil.success(operators);
    }

    @RequestMapping("/deduction/partners")
    public ResponseInfo fetchPartners(){
        List<OptionVO> partners = deductionService.fetchPartners();
        return ResponseInfoUtil.success(partners);
    }

    @RequestMapping("/deduction/offers")
    public ResponseInfo fetchOffers(){
        List<OptionVO> offers = deductionService.fetchOffers();
        return ResponseInfoUtil.success(offers);
    }

    @RequestMapping("/deduction/find")
    public ResponseInfo findByParams(@RequestBody JSONObject jsonObject){
        String country = jsonObject.getString("country");
        String partner = jsonObject.getString("partner");
        String operator = jsonObject.getString("operator");
        String offerName = jsonObject.getString("offerName");
        Long begin = jsonObject.getLong("begin");
        Long end = jsonObject.getLong("end");
        List<DeductionModel> list = deductionService.find(country,partner,offerName,operator,begin,end);
        return ResponseInfoUtil.success(list);
    }

    @RequestMapping("/deduction/findOpByParam")
    public ResponseInfo findOpByParam(@RequestBody JSONObject jsonObject ){
        String country = jsonObject.getString("country");
        List<OptionVO> list=deductionService.findOpByParam(country);
        return ResponseInfoUtil.success(list);
    }

    @RequestMapping("/deduction/findParByParam")
    public ResponseInfo findParByParam(@RequestBody JSONObject jsonObject ){
        String country = jsonObject.getString("country");
        String operator = jsonObject.getString("operator");
        List<OptionVO> list=deductionService.findParByParam(country,operator);
        return ResponseInfoUtil.success(list);
    }

    @RequestMapping("/deduction/findOffByParam")
    public ResponseInfo findOffByParam(@RequestBody JSONObject jsonObject ){
        String country = jsonObject.getString("country");
        String operator = jsonObject.getString("operator");
        String partner = jsonObject.getString("partner");
        List<OptionVO> list=deductionService.findOffByParam(country,operator,partner);
        return ResponseInfoUtil.success(list);
    }
}
