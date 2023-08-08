package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.AffiliateModel;
import com.starp.zoo.repo.zoo.AffiliateRepo;
import com.starp.zoo.service.IAffiliateService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Service
public class AffiliateServiceImpl implements IAffiliateService {

    @Autowired
    private AffiliateRepo affiliateRepo;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Override
    public void save(AffiliateModel affiliateModel) {
        affiliateRepo.save(affiliateModel);
    }

    @Override
    public AffiliateModel getById(String id) {
        return affiliateRepo.findById(id).get();
    }

    @Override
    public PageVO getList(int page, int limit, String name) {
        Specification<AffiliateModel> specification = new Specification<AffiliateModel>() {
            @Override
            public Predicate toPredicate(Root<AffiliateModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(name)) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), name));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                orders.add(criteriaBuilder.asc(root.get("name")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<AffiliateModel> pageVO = new PageVO();
        Long total = affiliateRepo.count(specification);
        pageVO.setTotal(total);
        page = page >= 1? page - 1: 0;
        List<AffiliateModel> list = affiliateRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        pageVO.setList(list);
        pageVO.setLimit(limit);
        pageVO.setPage(page);
        return pageVO;
    }

    @Override
    public void deleteById(String id) {
        affiliateRepo.deleteById(id);
    }

    @Override
    public List<OptionVO> getAllNames() {
        StringBuilder sql = new StringBuilder("select distinct t.name from t_affiliate t order by t.name asc");
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        List<String> queryList = nativeQuery.getResultList();
        List<OptionVO> list = new ArrayList<>();
        for(String name : queryList){
            OptionVO optionVO = new OptionVO();
            optionVO.setIdentification(name);
            optionVO.setLabel(name);
            optionVO.setValue(name);
            if(list != null && !list.contains(optionVO)){
                list.add(optionVO);
            }
        }
        return list;
    }

    @Override
    public List<OptionVO> getAll() {
        List<AffiliateModel> affiliateModels = affiliateRepo.findAll();
        List<OptionVO> optionVOS = new ArrayList<>();
        if(affiliateModels != null && affiliateModels.size() > 0){
            for(AffiliateModel affiliateModel : affiliateModels){
                optionVOS.add(new OptionVO(affiliateModel.getIdentification(), affiliateModel.getName(), affiliateModel.getIdentification()));
            }
        }
        return optionVOS;
    }
}
