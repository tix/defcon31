package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.PartnerModel;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.PartnerRepo;
import com.starp.zoo.service.IPartnerService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author covey
 *
 */
@Service
public class PartnerServiceImpl implements IPartnerService {

    @Autowired
    private PartnerRepo partnerRepo;


    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Override
    public List<PartnerModel> findAll() {
        return partnerRepo.findAll();
    }


    @Override
    public void save(PartnerModel partnerModel) {
        partnerRepo.save(partnerModel);
    }

    @Override
    public void delete(PartnerModel partnerModel) {
        partnerRepo.delete(partnerModel);
    }

    @Override
    public boolean existsByPartnerId(String partnerId) {
        return partnerRepo.existsByPartnerId(partnerId);
    }

    @Override
    public PageVO getList(Integer page, Integer limit, String partnerName) {
        Specification<PartnerModel> specification = new Specification<PartnerModel>() {
            @Override
            public Predicate toPredicate(Root<PartnerModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(partnerName)) {
                    predicates.add(criteriaBuilder.equal(root.get("partnerName"), partnerName));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.asc(root.get("partnerName")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<PartnerModel> pageVO = new PageVO<>();
        pageVO.setTotal(partnerRepo.count(specification));
        page = page > 0 ? page - 1 : 0;
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        List<PartnerModel> partnerModelList = partnerRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        if(partnerModelList != null && partnerModelList.size() >0){
            for(PartnerModel partnerModel : partnerModelList){
                Object obj = cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER, partnerModel.getPartnerId());
                if(obj != null){
                    OfferModel offerModel = JSONObject.parseObject(String.valueOf(obj),OfferModel.class);
                    partnerModel.setOffer(offerModel);
                }
            }
        }
        pageVO.setList(partnerModelList);
        return pageVO;
    }


    @Override
    public PartnerModel findLast() {
        return partnerRepo.findTopByOrderByCreateTimeDesc();
    }

    @Override
    public List<OptionVO> findPartnerNames() {
        List<String> list = partnerRepo.findPartnerNames();
        List<OptionVO> optionVOS = new ArrayList<>();
        for (String name : list) {
            OptionVO optionVO = new OptionVO();
            optionVO.setIdentification(name);
            optionVO.setLabel(name);
            optionVO.setValue(name);
            optionVOS.add(optionVO);
        }
        return optionVOS;
    }

    @Override
    public List<OptionVO> findPartnerIds() {
        List<String> list = partnerRepo.findPartnerIds();
        List<OptionVO> optionVOS = new ArrayList<>();
        for (String id : list) {
            OptionVO optionVO = new OptionVO();
            optionVO.setIdentification(id);
            optionVO.setLabel(id);
            optionVO.setValue(id);
            optionVOS.add(optionVO);
        }
        return optionVOS;
    }

    @Override
    public PartnerModel findByPartnerId(String partnerId) {
        return partnerRepo.findByPartnerId(partnerId);
    }

    @Override
    public boolean existTestOffer(String id) {
        boolean result = false;
        Boolean exsitOffer = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.ZOO_TEST_OFFER,id);
        if(exsitOffer != null && exsitOffer){
            result = true;
        }
        return result;
    }

}
