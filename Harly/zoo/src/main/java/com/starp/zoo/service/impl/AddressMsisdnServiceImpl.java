package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.AddressMsisdnModel;
import com.starp.zoo.entity.zoo.MsisdnRecordModel;
import com.starp.zoo.repo.zoo.AddressMsisdnRepo;
import com.starp.zoo.repo.zoo.MsisdnRecordRepo;
import com.starp.zoo.service.IAddressMsisdnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * AddressMsisdnServiceImpl.
 *
 * @author magic
 * @data 2022/4/21
 */
@Service
@Slf4j
public class AddressMsisdnServiceImpl implements IAddressMsisdnService {

    @Autowired
    private AddressMsisdnRepo addressMsisdnRepo;

    @Autowired
    private MsisdnRecordRepo msisdnRecordRepo;

    @Override
    @Async
    public void save(JSONObject jsonObject) {
        for (String operator : Constant.ADDRESS_OPERATOR) {
            JSONArray jsonArray = jsonObject.getJSONArray(operator);
            if (jsonArray != null) {
                List<String> msisdnList = JSON.parseArray(jsonArray.toJSONString(), String.class);
                if (!CollectionUtils.isEmpty(msisdnList)) {
                    if (ZooConstant.DTAC.equals(operator)) {
                        //保存到msisdn_record中一份
                        for (String msisdn : msisdnList) {
                            try {
                                MsisdnRecordModel model = new MsisdnRecordModel();
                                model.setMsisdn(msisdn);
                                msisdnRecordRepo.save(model);
                            }catch (Exception e){
                                log.error("HANDLE SAVE MSISDN SAME MSISDN:{}",msisdn);
                            }
                        }
                    }
                    for (String msisdn : msisdnList) {
                        AddressMsisdnModel byMsisdn = addressMsisdnRepo.findByMsisdn(msisdn);
                        if (byMsisdn == null) {
                            AddressMsisdnModel model = new AddressMsisdnModel();
                            model.setMsisdn(msisdn);
                            model.setOperator(operator);
                            addressMsisdnRepo.save(model);
                        }
                    }
                }
            }
        }
    }

}
