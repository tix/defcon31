package com.starp.zoo.service.impl;

import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.IProtcLogService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
@Service
public class ProtcLogServiceImpl implements IProtcLogService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private ApplicationRepo applicationRepo;


    @Override
    public PageVO findLog(String country, String operator, String appName, String offerId, String stepId, String pid, int limit, int page, long begin, long end) {
        String table ="t_http_logging";
        String date = DateUtil.formatyyyyMMddHHmmss(begin + 8*NumberEnum.ONE_HOUR_MILLISECONDS.getNum()).split(" ")[0].replace("-", "");
        String date1 = DateUtil.formatyyyyMMddHHmmss(System.currentTimeMillis()).split(" ")[0].replace("-", "");
        ApplicationModel app = applicationRepo.findFirstByAppName(appName);
        if(!date.equalsIgnoreCase(date1)){
            table =table + DateUtil.formatyyyyMMddHHmmss(begin).split(" ")[0].replace("-","");
        }
        StringBuilder dataSql = new StringBuilder("select * from " + table);

        StringBuilder countSql = new StringBuilder("SELECT count(1) FROM " + table);
        //拼接where条件
        StringBuilder whereSql = new StringBuilder(" WHERE 1 = 1");

        whereSql.append(" and  carrier = :operator");

        whereSql.append(" and  app_id = :appId");

        whereSql.append(" and  offer_id = :offerId");

        whereSql.append(" and createtime >= :begin");



        if (pid != null && pid.length() > 0) {
            whereSql.append(" and  pid = :pid");

        }

        if (stepId != null && stepId.length() > 0) {
            whereSql.append(" and  step_id = :stepId");

        }

        //组装sql语句
        dataSql.append(whereSql).append(" order by createtime desc");
        countSql.append(whereSql);

        //创建本地sql查询实例
        Query dataQuery = entityManager.createNativeQuery(dataSql.toString(), HttpLoggingModel.class);
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        //设置参数

        if (pid != null && pid.length() > 0) {
            dataQuery.setParameter("pid", pid);
            countQuery.setParameter("pid",pid);
        }

        if (stepId != null && stepId.length() > 0) {
            dataQuery.setParameter("stepId",stepId);
            countQuery.setParameter("stepId",stepId);
        }

        dataQuery.setParameter("begin",new Date(begin));
        dataQuery.setParameter("operator",operator);
        dataQuery.setParameter("appId",app.getIdentification());
        dataQuery.setParameter("offerId",offerId);

        countQuery.setParameter("begin",new Date(begin));
        countQuery.setParameter("operator",operator);
        countQuery.setParameter("appId",app.getIdentification());
        countQuery.setParameter("offerId",offerId);

        //设置分页
        int offSet = (page - 1) * limit;
        dataQuery.setFirstResult(offSet);
        dataQuery.setMaxResults(limit);
        BigInteger count = (BigInteger) countQuery.getSingleResult();
        Long total = count.longValue();
        List<HttpLoggingModel> list = total > offSet ? dataQuery.getResultList() : Collections.<HttpLoggingModel>emptyList();
        PageVO pageVO = new PageVO();
        pageVO.setList(list);
        pageVO.setTotal(total);
        pageVO.setLimit(limit);
        pageVO.setPage(page);
        return pageVO;
    }

    @Override
    public List<OptionVO> fetchOffersWithOperators(String operator) {
        List<OfferModel> list = offerRepo.findByOperator(operator);
        ArrayList<OptionVO> options = new ArrayList<>();
        for (OfferModel offerModel : list) {
            OptionVO option = new OptionVO(offerModel.getIdentification(), offerModel.getOfferName(), offerModel.getIdentification());
            options.add(option);
        }
        return options;
    }

}
