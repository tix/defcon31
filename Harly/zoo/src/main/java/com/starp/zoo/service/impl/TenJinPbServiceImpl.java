package com.starp.zoo.service.impl;

import com.starp.zoo.entity.zoo.TenJinPbModel;
import com.starp.zoo.repo.zoo.TenJinPbRepo;
import com.starp.zoo.service.ITenJinPbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * TenJinPBServiceImpl.
 *
 * @author magic
 * @data 2022/4/14
 */
@Service
public class TenJinPbServiceImpl implements ITenJinPbService {

    @Autowired
    private TenJinPbRepo tenJinPBRepo;

    @Override
    public void saveModel(TenJinPbModel model) {
        if (!StringUtils.isEmpty(model.getAdvertisingId())) {
            TenJinPbModel originModel = tenJinPBRepo.findByAdvertisingId(model.getAdvertisingId());
            if (originModel != null) {
                model.setIdentification(originModel.getIdentification());
            }
        }
        tenJinPBRepo.save(model);
    }
}
