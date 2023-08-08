package com.starp.zoo.service.impl;

import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.HttpLoggingRecordModel;
import com.starp.zoo.repo.zoo.HttpLoggingRecordRepo;
import com.starp.zoo.service.IHttpLoggingRecordService;
import com.starp.zoo.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * HttpLoggingRecordServiceImpl.
 *
 * @author magic
 * @date 2021/9/26
 */
@Service
public class HttpLoggingRecordServiceImpl implements IHttpLoggingRecordService {

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Autowired
    HttpLoggingRecordRepo httpLoggingRecordRepo;

    @Override
    @Async
    public void saveRecord(HttpLoggingModel httpLoggingModel) {
        HttpLoggingRecordModel model = new HttpLoggingRecordModel();
        model.setCarrier(httpLoggingModel.getCarrier());
        model.setAppId(httpLoggingModel.getAppId());
        model.setOfferId(httpLoggingModel.getOfferId());
        model.setUserId(httpLoggingModel.getUserId());
        model.setPid(httpLoggingModel.getPid());
        model.setStepNumber(httpLoggingModel.getStepId());
        model.setStepName(httpLoggingModel.getStepName());
        model.setRecordDate(DateUtil.formatDay(new Date()));
        httpLoggingRecordRepo.save(model);
    }

    @Override
    public List<HttpLoggingRecordModel> findList(String operator, String appName, String offerId, String userId, String pid, String stepNumber, Long begin, Long end, String stepName, Integer page, Integer limit) {
        List<HttpLoggingRecordModel> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(" select createtime,carrier,app_id,offer_id,user_id,pid,step_number,record_date,step_name from t_http_logging_record where DATE_FORMAT(createtime,'%Y-%m-%d %H:%i:%s') BETWEEN :begin  AND :end and carrier = :operator and app_id = :appName ");
        StringBuilder whereSql = new StringBuilder();
        if (!StringUtils.isEmpty(offerId)) {
            whereSql.append(" and offer_id = :offerId");
        }
        if (!StringUtils.isEmpty(userId)) {
            whereSql.append(" and user_id = :userId");
        }
        if (!StringUtils.isEmpty(pid)) {
            whereSql.append(" and pid = :pid");
        }
        if (!StringUtils.isEmpty(stepNumber)) {
            whereSql.append(" and step_number = :stepNumber");
        }
        if (!StringUtils.isEmpty(stepName)) {
            whereSql.append(" and step_name = :stepName");
        }
        Query query = zooEntityManager.createNativeQuery(sql.append(whereSql).toString());
        query.setParameter("begin", DateUtil.formatyyyyMMddHHmmss(DateUtil.getDateByTimeZone(begin, ZooConstant.GMT_0)));
        query.setParameter("end", DateUtil.formatyyyyMMddHHmmss(DateUtil.getDateByTimeZone(end, ZooConstant.GMT_0)));
        query.setParameter("operator", operator);
        query.setParameter("appName", appName);
        if (!StringUtils.isEmpty(offerId)) {
            query.setParameter("offerId", offerId);
        }
        if (!StringUtils.isEmpty(userId)) {
            query.setParameter("userId", userId);
        }
        if (!StringUtils.isEmpty(pid)) {
            query.setParameter("pid", pid);
        }
        if (!StringUtils.isEmpty(stepNumber)) {
            query.setParameter("stepNumber", stepNumber);
        }
        if (!StringUtils.isEmpty(stepName)) {
            query.setParameter("stepName", stepName);
        }
        List<Object[]> resultList = query.getResultList();
        if (resultList != null) {
            for (Object[] objects : resultList) {
                HttpLoggingRecordModel model = new HttpLoggingRecordModel();
                model.setCreateTime((Date) objects[0]);
                model.setCarrier(objects[1] != null ? objects[1].toString() : null);
                model.setAppId(objects[2] != null ? objects[2].toString() : null);
                model.setOfferId(objects[3] != null ? objects[3].toString() : null);
                model.setUserId(objects[4] != null ? objects[4].toString() : null);
                model.setPid(objects[5] != null ? objects[5].toString() : null);
                model.setStepNumber(objects[6] != null ? objects[6].toString() : null);
                model.setRecordDate(objects[7] != null ? objects[7].toString() : null);
                model.setStepName(objects[8] != null ? objects[8].toString() : null);
                result.add(model);
            }
        }
        return result;
    }
}
