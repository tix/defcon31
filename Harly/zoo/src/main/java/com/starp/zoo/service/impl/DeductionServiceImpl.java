package com.starp.zoo.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.entity.zoo.DeductionModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.repo.zoo.AffPostBackRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.SubscribeRepo;
import com.starp.zoo.service.IDeductionService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.vo.OptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author covey
 */
@Service
public class DeductionServiceImpl implements IDeductionService {

    @Autowired
    OfferRepo offerRepo;

    @Autowired
    SubscribeRepo subscribeRepo;

    @Autowired
    AffPostBackRepo postBackRepo;

    @Override
    public List<OptionVO> fetchOperators() {
        List<String> operators = offerRepo.fetchOperators();
        List<OptionVO> options=new ArrayList<>();
        for (String operator : operators) {
            OptionVO optionVO=new OptionVO();
            optionVO.setValue(operator);
            optionVO.setIdentification(operator);
            optionVO.setLabel(operator);
            options.add(optionVO);
        }
        return options;
    }

    @Override
    public List<OptionVO> fetchPartners() {
        List<String> partners = offerRepo.fetchPartners();
        List<OptionVO> options=new ArrayList<>();
        for (String partner : partners) {
            OptionVO optionVO=new OptionVO();
            optionVO.setValue(partner);
            optionVO.setIdentification(partner);
            optionVO.setLabel(partner);
            options.add(optionVO);
        }
        return options;
    }

    @Override
    public List<OptionVO> fetchOffers() {
        List<String> offerNames = offerRepo.findOfferNames();
        List<OptionVO> options=new ArrayList<>();
        for (String offerName : offerNames) {
            OptionVO optionVO=new OptionVO();
            optionVO.setValue(offerName);
            optionVO.setIdentification(offerName);
            optionVO.setLabel(offerName);
            options.add(optionVO);
        }
        return options;
    }

    @Override
    public List<DeductionModel> find(String country, String partner, String offerName, String operator, Long begin, Long end) {
        Specification specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if(!StringUtils.isEmpty(partner)) {
                    predicates.add(criteriaBuilder.equal(root.get("partner"), partner));
                }
                if(!StringUtils.isEmpty(offerName)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerName"), offerName));
                }
                if(!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<OfferModel> offers = offerRepo.findAll(specification);
        Set<String> ids = new HashSet<>();
        Set<String> offerIds = new HashSet<>();
        for (OfferModel offer : offers) {
            ids.add(offer.getIdentification());
            offerIds.add(offer.getOfferId());
        }
        List<DeductionModel> list=new ArrayList<>();
        int days= (int) Math.ceil(( (double)end- (double)begin)/NumberEnum.ONE_DAY_MILLISECONDS.getNum());
        for (int i = 0; i <days ; i++) {
            long temp=begin+NumberEnum.ONE_DAY_MILLISECONDS.getNum();
            String time = DateUtil.formatDay(new Date(begin+NumberEnum.ONE_HOUR_MILLISECONDS.getNum()*8));
            double conversionCount=subscribeRepo.findByIdsAndTime(new ArrayList<>(ids),new Date(begin),new Date(temp));
            double postBackCount = postBackRepo.findByIdsAndTime(new ArrayList<>(offerIds), new Date(begin), new Date(temp));
            DeductionModel deductionModel = new DeductionModel();
            deductionModel.setPartner(partner);
            deductionModel.setOperator(operator);
            deductionModel.setOfferName(offerName);
            deductionModel.setCountry(country);
            deductionModel.setConversion(conversionCount);
            deductionModel.setPostBack(postBackCount);
            deductionModel.setTime(time);
            deductionModel.setRate(accuracy(conversionCount,postBackCount,2));
            list.add(deductionModel);
            begin+=NumberEnum.ONE_DAY_MILLISECONDS.getNum();
        }
        return list;
    }

    @Override
    public List<OptionVO> findOpByParam(String country) {
        List<String> operators = offerRepo.findOpByParam(country);
        List<OptionVO> options=new ArrayList<>();
        for (String operator : operators) {
            OptionVO optionVO=new OptionVO();
            optionVO.setValue(operator);
            optionVO.setIdentification(operator);
            optionVO.setLabel(operator);
            options.add(optionVO);
        }
        return options;
    }

    @Override
    public List<OptionVO> findParByParam(String country, String operator) {
        List<String> partners = offerRepo.findParByParam(country,operator);
        List<OptionVO> options=new ArrayList<>();
        for (String partner : partners) {
            OptionVO optionVO=new OptionVO();
            optionVO.setValue(partner);
            optionVO.setIdentification(partner);
            optionVO.setLabel(partner);
            options.add(optionVO);
        }
        return options;
    }

    @Override
    public List<OptionVO> findOffByParam(String country, String operator, String partner) {
        List<String> offerNames = offerRepo.findOffByParam(country,operator,partner);
        List<OptionVO> options=new ArrayList<>();
        for (String offerName : offerNames) {
            OptionVO optionVO=new OptionVO();
            optionVO.setValue(offerName);
            optionVO.setIdentification(offerName);
            optionVO.setLabel(offerName);
            options.add(optionVO);
        }
        return options;
    }

    /**
     * 百分比计算
     * @param num
     * @param total
     * @param scale 设置精确几位小数
     * @return
     */
    private static double accuracy(double num, double total, int scale){
        if (num == 0.0 || total == 0.0) {
            return 0.0;
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracyNum = (num-total) / num * 100;
        return Double.parseDouble(df.format(accuracyNum));
    }

}
