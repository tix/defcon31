package com.starp.zoo.service.impl;

import com.starp.zoo.entity.zoo.AutoScriptModel;
import com.starp.zoo.repo.zoo.ConfigJsRepo;
import com.starp.zoo.service.IConfigJsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @Author David
 * @Date 18:03 2018/12/18
 **/
@Service
public class ConfigJsServiceImpl implements IConfigJsService{

    @Autowired
    private ConfigJsRepo configJsRepo;

    @Override
    public List<AutoScriptModel> getAllConfig() {
        List<AutoScriptModel> scriptModelList = configJsRepo.findAll();
        return  scriptModelList;
    }

    @Override
    public void save(AutoScriptModel scriptModel) {
            configJsRepo.save(scriptModel);
    }

    @Override
    public List<AutoScriptModel> getConfigByName(String jsname) {
        List<AutoScriptModel> scriptModelList = configJsRepo.findByNameLike(jsname);
        return scriptModelList;
    }

    @Override
    public AutoScriptModel findConfigJs(String id) {
        AutoScriptModel model = configJsRepo.findByIdentification(id);
        return model;
    }

    @Override
    public void delete(String id) {
        configJsRepo.deleteById(id);
    }
}
