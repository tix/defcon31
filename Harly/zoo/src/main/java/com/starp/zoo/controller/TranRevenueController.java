package com.starp.zoo.controller;


import com.alibaba.druid.util.StringUtils;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.AffEpmInfoModel;
import com.starp.zoo.entity.zoo.DeductionCountryModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.repo.zoo.AffEpmInfoRepo;
import com.starp.zoo.repo.zoo.DeductionCountryRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.vo.TransQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author david
 */
@Controller
public class TranRevenueController {

    @Autowired
    AffEpmInfoRepo affEpmInfoRepo;

    @Autowired
    OfferRepo offerRepo;

    @Autowired
    DeductionCountryRepo deductionCountryRepo;

    @Autowired
    private IOfferService offerService;

    @RequestMapping(value = "/tran/revenue",method = {RequestMethod.GET,RequestMethod.POST})
    public String fetchRevenue(@RequestParam(required = false)String time, Model model){
        List<String> appNames = new ArrayList<>(0);
        appNames.add(ZooConstant.APP_SERVICE_SDK_004);
        appNames.add(ZooConstant.APP_SERVICE_SDK_005);
        String dayTime = "";
        List<TransQueryVO>  transQueryVOList = new ArrayList<>();
        if(StringUtils.isEmpty(time)){
            dayTime = DateUtil.today();
        }else {
            dayTime = time;
        }
        if(DateUtil.formatDayTime(dayTime).after(DateUtil.formatDayTime(ZooConstant.TRAN_CHECK_TIME))){
             transQueryVOList  = getTransQueryList(appNames,dayTime);
        }
        model.addAttribute("queryList",transQueryVOList);
        return "/true/tranQuery.html";
    }

    private List<TransQueryVO> getTransQueryList(List<String> appNames,String dayTime) {
        List<TransQueryVO> transQueryVOList = new ArrayList<>();
        List<AffEpmInfoModel> affEpmInfoModels = affEpmInfoRepo.findEpmModels(appNames,dayTime);
        for(AffEpmInfoModel affEpmInfoModel: affEpmInfoModels){
            TransQueryVO transQueryVO = new TransQueryVO();
            BigDecimal revenue = new BigDecimal(0);
            String country = affEpmInfoModel.getCountry();
            List<AffEpmInfoModel> models = affEpmInfoRepo.findEpmByCountry(appNames,dayTime,country);
            // 点击数
            long clickNum = affEpmInfoRepo.queryClickNum(appNames,dayTime,country);
            for(AffEpmInfoModel model:models){
                Long tranNum = affEpmInfoRepo.queryTransNum(appNames,dayTime,country,model.getOfferId());
                BigDecimal epmdeductionTransNum = new BigDecimal(tranNum);
                OfferModel offerModel = offerService.getOfferModel(model.getOfferId());
                if(offerModel!=null){
                    BigDecimal epmRevenue = null;
                    if(epmdeductionTransNum.equals(BigDecimal.ZERO)){
                        epmRevenue = new BigDecimal(0);
                    }else {
                        epmRevenue  = epmdeductionTransNum.multiply(new BigDecimal(offerModel.getPayout()));
                    }
                    revenue = revenue.add(epmRevenue);
                }
            }
            double deduction;
            DeductionCountryModel deductionCountryModel = deductionCountryRepo.findByCountryAndTime(country,dayTime);
            if(deductionCountryModel!=null){
                deduction = deductionCountryModel.getDeduction();
            }else {
                DeductionCountryModel deductionDescModel = deductionCountryRepo.findFirstByCountryAndTimeBeforeOrderByTimeDesc(country,dayTime);
                if(deductionDescModel!=null){
                    deduction = deductionDescModel.getDeduction();
                }else {
                    deduction = 0;
                }
            }
            BigDecimal deductionRevenue = revenue.multiply(new BigDecimal(NumberEnum.ONE.getNum()).subtract(new BigDecimal(deduction))).setScale(2, RoundingMode.DOWN);
            transQueryVO.setCountry(country);
            transQueryVO.setClickNum((int) clickNum);
            transQueryVO.setRevenue(deductionRevenue.doubleValue());
            transQueryVO.setTime(dayTime);
            transQueryVO.setChannelRevenue(deductionRevenue.multiply(new BigDecimal(ZooConstant.CHANNEL_DEVENUE)).setScale(2,RoundingMode.DOWN).doubleValue());
            transQueryVOList.add(transQueryVO);
        }
        return transQueryVOList;
    }

}
