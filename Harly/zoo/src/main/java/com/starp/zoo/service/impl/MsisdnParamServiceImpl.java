package com.starp.zoo.service.impl;

import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.MsisdnParamModel;
import com.starp.zoo.repo.zoo.MsisdnParamRepo;
import com.starp.zoo.service.IMsisdnParamService;
import com.starp.zoo.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Charles
 * @date 2019/5/6
 * @description :
 */
@Service
public class MsisdnParamServiceImpl implements IMsisdnParamService {

    @Autowired
    private MsisdnParamRepo msisdnParamRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(MsisdnParamModel msisdnParamModel) {
        msisdnParamRepo.save(msisdnParamModel);
        // 同步更新记录到 redis 中
        stringRedisTemplate.opsForHash().put(CacheNameSpace.MSISDN_PARAMS, msisdnParamModel.getOperator(), msisdnParamModel.getParams());
    }

    @Override
    public MsisdnParamModel getById(String id) {
        return msisdnParamRepo.findById(id).get();
    }

    @Override
    public PageVO getList(int page, int limit, String country, String operator) {
        Specification<MsisdnParamModel> specification = new Specification<MsisdnParamModel>() {
            @Override
            public Predicate toPredicate(Root<MsisdnParamModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if(!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<MsisdnParamModel> pageVO = new PageVO<>();
        pageVO.setTotal(msisdnParamRepo.count(specification));
        page = page > 1 ? page - 1 : 0;
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        List<MsisdnParamModel> msisdnParamModels = msisdnParamRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        if(msisdnParamModels != null && msisdnParamModels.size() > 0) {
            for(MsisdnParamModel msisdnParamModel : msisdnParamModels) {
                if(!StringUtils.isEmpty(msisdnParamModel.getParams())) {
                    msisdnParamModel.setParamArr(Arrays.asList(msisdnParamModel.getParams().split(ZooConstant.COMMA)));
                }
            }
        }
        pageVO.setList(msisdnParamModels);
        return pageVO;
    }

    @Override
    public void delete(String id) {
        MsisdnParamModel msisdnParamModel = msisdnParamRepo.findById(id).get();
        msisdnParamRepo.deleteById(id);
        if(msisdnParamModel != null) {
            // 从 redis 中删除记录
            stringRedisTemplate.opsForHash().delete(CacheNameSpace.MSISDN_PARAMS, msisdnParamModel.getOperator());
        }
    }
}
