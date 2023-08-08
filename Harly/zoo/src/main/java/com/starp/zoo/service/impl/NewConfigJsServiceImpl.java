package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.NewScriptModel;
import com.starp.zoo.entity.zoo.OfferAutoScriptModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.repo.zoo.NewAutoScriptRepo;
import com.starp.zoo.repo.zoo.OfferAutoScriptRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.INewConfigJsService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.regex.Pattern;

/***
 *
 * @Author David
 * @Date 11:50 2019/1/3
 * @param
 * @return
 **/
@Service
@Slf4j
public class NewConfigJsServiceImpl implements INewConfigJsService{

    @Autowired
    private NewAutoScriptRepo configJsRepo;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Autowired
    private OfferRepo offerRepo;


    @Autowired
    private OfferAutoScriptRepo offerAutoScriptRepo;

    @Override
    public List<NewScriptModel> getAllConfig() {
        List<NewScriptModel> scriptModelList = configJsRepo.findAll();
        return  scriptModelList;
    }

    @Override
    public void save(NewScriptModel scriptModel) {
        configJsRepo.save(scriptModel);
    }

    @Override
    public List<NewScriptModel> getJsConfig(String jsname,String country) {
        Specification specification = new Specification<NewScriptModel>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if(!StringUtils.isEmpty(jsname)) {
                    predicates.add(criteriaBuilder.like(root.get("name"), jsname));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
                return criteriaQuery.getRestriction();
            }
        };
        return configJsRepo.findAll(specification);
    }

    @Override
    public NewScriptModel findConfigJs(String id) {
        NewScriptModel model = configJsRepo.findByIdentification(id);
        return model;
    }

    @Override
    public void delete(String id) {
        configJsRepo.deleteById(id);
    }


    /***
     * 根据国家,类型跟URL匹配js
     * @Author David
     * @Date 11:52 2019/1/7
     * @param
     * @return com.starp.zoo.entity.zoo.NewScriptModel
     **/
    @Override
    public NewScriptModel findJsByCountryAndType(String url,String country, Integer jsType) {
        List<NewScriptModel> scriptModelList = null;
        if(jsType != null){
            scriptModelList  = configJsRepo.findAllByCountryAndEventType(country,jsType);
        }else{
            scriptModelList = configJsRepo.findAllByCountry(country);
        }
        if(scriptModelList != null && scriptModelList.size() > 0){
            for(NewScriptModel newScriptModel : scriptModelList){
                if(Pattern.matches(newScriptModel.getRegular(), url)){
                    //匹配到就返回
                    return newScriptModel;
                }
            }

        }
        return null;
    }
}
