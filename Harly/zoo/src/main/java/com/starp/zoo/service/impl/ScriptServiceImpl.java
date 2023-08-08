package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.AexErrorCodeEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.OfferAutoScriptModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.ResultScriptModel;
import com.starp.zoo.entity.zoo.ScriptModel;
import com.starp.zoo.repo.zoo.OfferAutoScriptRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.ScriptRepo;
import com.starp.zoo.service.IScriptService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Charles
 * @date 2019/3/4
 * @description :
 */
@Service
@Slf4j
public class ScriptServiceImpl implements IScriptService {

    @Autowired
    private ScriptRepo scriptRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private OfferAutoScriptRepo offerAutoScriptRepo;

    @Autowired
    private IScriptService scriptService;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Override
    public List<OptionVO> getOptions(String country) {
        Specification<ScriptModel> specification = new Specification<ScriptModel>() {
            @Override
            public Predicate toPredicate(Root<ScriptModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(country)) {
                    Predicate predicate1 = criteriaBuilder.equal(root.get("country"), country);
                    Predicate predicate2 = criteriaBuilder.equal(root.get("country"), "");
                    Predicate predicate3 = criteriaBuilder.isNull(root.get("country"));
                    Predicate predicate = criteriaBuilder.or(predicate1, predicate2, predicate3);
                    predicates.add(predicate);
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        List<ScriptModel> models = scriptRepo.findAll(specification);
        List<OptionVO> optionVOS = new ArrayList<>();
        if(models != null && models.size() > 0){
            for(ScriptModel model : models){
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(model.getIdentification());
                optionVO.setLabel(model.getName());
                optionVO.setValue(model.getIdentification());
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public ScriptModel getScript(String id) {
        return scriptRepo.findById(id).get();
    }

    @Override
    public ScriptModel saveScript(ScriptModel scriptModel, List<String> offerIds) {
        ScriptModel saveModel = null;
        if(scriptModel != null){
            boolean isPin = scriptModel.getEventType() != null && scriptModel.getEventType() != ZooConstant.EVENT_TYPE_PIN
                    && scriptModel.getEventType() !=ZooConstant.EVENT_TYPE_MSISDN && scriptModel.getEventType() !=ZooConstant.EVENT_TYPE_MSISDN_PIN;
            if(isPin){
                if(scriptModel.getEventType() !=ZooConstant.EVENT_TYPE_MSISDN_PIN) {
                    scriptModel.setMsisdnLocation(null);
                }
                scriptModel.setPinByteCount(0);
                scriptModel.setPinFrontCode(null);
                scriptModel.setPinInputLocation(null);
                scriptModel.setPinConfirmLocation(null);
                scriptModel.setPinBtnLocation(null);
            }else if(scriptModel.getEventType() != null && scriptModel.getEventType() != ZooConstant.EVENT_TYPE_PIN_MO){
                scriptModel.setShortCode(null);
                scriptModel.setKeyword(null);
            }
            saveModel = scriptRepo.save(scriptModel);
            // 保存offer关联
            if(offerIds != null){
                if(offerIds.size() > 0) {
                    List<OfferAutoScriptModel> origins = offerAutoScriptRepo.findAllByAutoScriptId(saveModel.getIdentification());
                    List<String> enableNextIds = new ArrayList<>();
                    if (origins != null && origins.size() > 0) {
                        for (OfferAutoScriptModel offerAutoScriptModel : origins) {
                            if (!offerIds.contains(offerAutoScriptModel.getIdentification())) {
                                offerAutoScriptRepo.deleteById(offerAutoScriptModel.getIdentification());
                            } else {
                                enableNextIds.add(offerAutoScriptModel.getIdentification());
                            }
                        }
                    }
                    for (String offerId : offerIds) {
                        if (enableNextIds != null && !enableNextIds.contains(offerId) && !StringUtils.isEmpty(offerId)) {
                            OfferAutoScriptModel offerAutoScriptModel = new OfferAutoScriptModel();
                            offerAutoScriptModel.setOfferId(offerId);
                            offerAutoScriptModel.setAutoScriptId(scriptModel.getIdentification());
                            offerAutoScriptRepo.save(offerAutoScriptModel);
                        }
                    }
                }else {
                    offerAutoScriptRepo.deleteByAutoScriptId(saveModel.getIdentification());
                }
            }
        }
        return saveModel;
    }

    @Override
    public PageVO getScriptList(int page, int limit, String type, String name, String country, String eventType, List<String> scripts) {
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList= new ArrayList<>();
                if(!StringUtils.isEmpty(name)){
                    predicateList.add(criteriaBuilder.equal(root.get("name"),name));
                }
                if(!StringUtils.isEmpty(type)){
                    predicateList.add(criteriaBuilder.equal(root.get("type"),type));
                }
                if(!StringUtils.isEmpty(eventType)){
                    predicateList.add(criteriaBuilder.equal(root.get("eventType"),eventType));
                }
                if(!StringUtils.isEmpty(country)){
                    predicateList.add(criteriaBuilder.equal(root.get("country"),country));
                }
                if(scripts.size()>0){
                    Path<Object> path = root.get("regular");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                    for (String script : scripts) {
                        in.value(script);
                    }
                    predicateList.add(criteriaBuilder.and(in));
                }
                criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<ScriptModel> pageVo  = new PageVO<>();
        Long total =  scriptRepo.count(specification);
        pageVo.setTotal(total);
        page = page>=1?page-1:0;
        Page<ScriptModel> pageResult = scriptRepo.findAll(specification, PageRequest.of(page,limit));
        pageVo.setList(pageResult.getContent());
        pageVo.setLimit(limit);
        pageVo.setPage(page);
        return pageVo;
    }

    @Override
    public void delete(String id) {
        scriptRepo.deleteById(id);
        // 删除脚本关联
        offerAutoScriptRepo.deleteByAutoScriptId(id);
    }

    @Override
    public void multiDelete(List<String> ids) {
        if(ids != null && ids.size() > 0){
            for(String id : ids){
                delete(id);
            }
        }
    }

    @Override
    public List<OptionVO> findScriptName(String query) {
        String sql = String.format("SELECT distinct(name) FROM t_script where name like '%s' order by name asc",
                ZooConstant.PERCENT_SIGN + query + ZooConstant.PERCENT_SIGN);
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<String> names = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if(names != null && names.size() > 0){
            for(String name : names){
                OptionVO optionVo = new OptionVO();
                optionVo.setValue(name);
                optionVo.setLabel(name);
                optionVOS.add(optionVo);
            }
        }
        return optionVOS;
    }

    @Override
    public List<OfferAutoScriptModel> getOfferScript(String id) {
        return offerAutoScriptRepo.findAllByAutoScriptId(id);
    }

    @Override
    @Cacheable(value = "autoScript",key = "#offerId")
    public List<ScriptModel> findScriptByOfferId(String offerId) {
        return scriptRepo.findOfferScript(offerId);
    }

    @Timed
    @Override
    public ScriptModel getAppScript(String url, String country) {
        List<ScriptModel> scriptModelList = scriptService.findScriptListByCountry(country);
        if(scriptModelList != null && scriptModelList.size() > 0){
            for(ScriptModel scriptModel : scriptModelList){
                try {
                    if(Pattern.matches(scriptModel.getRegular(), url)){
                        //匹配到就返回
                        return scriptModel;
                    }
                }catch (Exception e){
                    log.info("pull js error:{}", JSON.toJSONString(e));
                    continue;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> checkExistRegular(String regular, String country, String identification, String type, String eventType) {
        if (!StringUtils.isEmpty(regular)) {
            String subRegular = "";
            Boolean result = false;
            Map<String, Object> map = new HashMap<>(1);
            if (regular.indexOf(ZooConstant.REGULAR_STAR) > -1) {
                subRegular = regular.substring(5, regular.lastIndexOf(ZooConstant.REGULAR_STAR));
            } else if (regular.indexOf(ZooConstant.REGULAR_POINT) > -1) {
                subRegular = regular.substring(3, regular.lastIndexOf(ZooConstant.REGULAR_POINT));
            } else {
                subRegular = regular;
            }
            List<ScriptModel> scriptModelList = scriptRepo.findAllScriptModel(country, Integer.valueOf(type), Integer.valueOf(eventType));
            for (ScriptModel scriptModel : scriptModelList) {
                if (!scriptModel.getIdentification().equalsIgnoreCase(identification)) {
                    String dbRegular = scriptModel.getRegular();
                    if (dbRegular.indexOf(ZooConstant.REGULAR_STAR) > -1) {
                        dbRegular = dbRegular.substring(5, dbRegular.lastIndexOf(ZooConstant.REGULAR_STAR));
                    } else if (dbRegular.indexOf(ZooConstant.REGULAR_POINT) > -1) {
                        dbRegular = dbRegular.substring(3, dbRegular.lastIndexOf(ZooConstant.REGULAR_POINT));
                    }
                    if (dbRegular.indexOf(subRegular) > -1 || subRegular.indexOf(dbRegular) > -1) {
                        map.put("model", scriptModel);
                        result = true;
                        break;
                    }
                }
            }
            map.put("result", result);
            return map;
        }
        return null;
    }

    @Override
    public List<OptionVO> findAllNames() {
        List<String> list=scriptRepo.findallNames();
        List<OptionVO> options=new ArrayList<>();
        for (String s : list) {
            OptionVO optionVO = new OptionVO();
            optionVO.setLabel(s);
            optionVO.setIdentification(s);
            optionVO.setValue(s);
            options.add(optionVO);
        }
        return options;
    }

    @Override
    public ScriptModel findJsByName(String name) {
        ScriptModel scriptModel=scriptRepo.findFirstByName(name);
        return scriptModel;
    }

    @Override
    public List<ScriptModel> getOnlineJsList(String type) {
        List<ScriptModel> list = scriptRepo.findByType(Integer.parseInt(type));
        return list;
    }

    @Override
    public List<OptionVO> fetchScriptOption() {
        List<ResultScriptModel> scriptModels = scriptRepo.findAllResultScriptModel();
        List<OptionVO> optionVOS = new ArrayList<>();
        for (ResultScriptModel scriptModel : scriptModels) {
            if(scriptModel.getScript().length()>0){
                OptionVO optionVO = new OptionVO();
                optionVO.setLabel(scriptModel.getName());
                optionVO.setValue(scriptModel.getScript());
                optionVO.setIdentification(scriptModel.getIdentification());
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public List<JSONObject> fetchResult(List<String> offerNames) {
        if(offerNames.size()>0){
            List<OfferModel> offerModels = offerRepo.findByOfferNames(offerNames);
            List<JSONObject> result =new ArrayList<>();
            for (String offerName : offerNames) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("offerName",offerName);
                for (OfferModel offerMode : offerModels) {
                    if(offerName.equalsIgnoreCase(offerMode.getOfferName())){
                        jsonObject.put("offerId",offerMode.getOfferId());
                    }
                }
                result.add(jsonObject);
            }
            return result;
        }else {
            return null;
        }
    }

    @Override
    @Timed
    public JSONObject getAppScriptJson(String url, String country) {
        JSONObject result = new JSONObject();
        List<ScriptModel> scriptModelList = scriptService.findScriptListByCountry(country);
        if(scriptModelList != null && scriptModelList.size() > 0){
            boolean matchJs = false;
            for(ScriptModel scriptModel : scriptModelList){
                try {
                    if(Pattern.matches(scriptModel.getRegular(), url)){
                        //匹配到就返回
                        matchJs = true;
                        result.put(ZooConstant.SCRIPT_MODEL,JSON.toJSONString(scriptModel));
                        return result;
                    }
                }catch (Exception e){
                    log.info("pull js error:{}", JSON.toJSONString(e));
                    continue;
                }
            }
            if(!matchJs){
                result.put(ZooConstant.ERROR_CODE, AexErrorCodeEnum.SWORD_URL_NOT_MATCH.getCode());
                result.put(ZooConstant.ERROR_MESSAGE,country + " "+ url +  AexErrorCodeEnum.SWORD_URL_NOT_MATCH.getMsg());
            }
        }else {
            result.put(ZooConstant.ERROR_CODE, AexErrorCodeEnum.SWORD_COUNTRY_JS_NULL.getCode());
            result.put(ZooConstant.ERROR_MESSAGE,country + AexErrorCodeEnum.SWORD_COUNTRY_JS_NULL.getMsg());
        }
        return result;
    }

    @Override
    @Cacheable(value = "script",key="#country")
    public List<ScriptModel> findScriptListByCountry(String country) {
        List<ScriptModel> scriptModels = scriptRepo.findQuery(country);
        return scriptModels;
    }

    @Override
    public List<OptionVO> fetchScriptsDetail(String country, String name, String type, String eventType, List<String> scripts) {
        StringBuilder dataSql = new StringBuilder("select regular from t_script ");
        //拼接where条件
        StringBuilder whereSql = new StringBuilder(" WHERE 1 = 1");

        if (!StringUtils.isEmpty(country)) {
            whereSql.append(" AND country = :country");
        }

        if (!StringUtils.isEmpty(name)) {
            whereSql.append(" AND name = :name");
        }

        if (!StringUtils.isEmpty(type)) {
            whereSql.append(" AND type = :type");
        }

        if (!StringUtils.isEmpty(eventType)) {
            whereSql.append(" AND event_type = :eventType");
        }

        if (scripts.size()>0) {
            whereSql.append(" AND  regular in ( :scripts )");
        }

        //组装sql语句
        dataSql.append(whereSql);
        Query dataQuery = zooEntityManager.createNativeQuery(dataSql.toString());
        if (!StringUtils.isEmpty(country)) {
            dataQuery.setParameter("country", country);
        }
        if (!StringUtils.isEmpty(name)) {
            dataQuery.setParameter("name", name);
        }
        if (!StringUtils.isEmpty(type)) {
            dataQuery.setParameter("type", type);
        }
        if (!StringUtils.isEmpty(eventType)) {
            dataQuery.setParameter("eventType", eventType);
        }
        if (scripts.size()>0) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < scripts.size(); i++) {
                builder.append(scripts.get(i));
                if(i != scripts.size() -1) {
                    builder.append(",");
                }
            }
            String script = builder.toString();
            dataQuery.setParameter("scripts",Arrays.asList(script.split(",")));
        }
        dataQuery.unwrap(NativeQuery.class);
        List<String> list = dataQuery.getResultList();

        Set<String> set = new HashSet<>();
        for (String regular : list) {
            if(regular.length()>0){
                set.add(regular);
            }
        }
        List<OptionVO> optionVOs=new ArrayList<>();
        for (String regular : set) {
            OptionVO optionVO = new OptionVO();
            optionVO.setValue(regular);
            optionVO.setIdentification(regular);
            optionVO.setLabel(regular);
            optionVOs.add(optionVO);
        }
        return optionVOs;
    }

}
