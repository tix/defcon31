package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.AppEpmModel;
import java.util.*;

/**
 * @author david
 */
public interface IAppEpmResetService {
    /**
     * save model
     * @param params
     */
    void saveModel(JSONObject params);

    /**
     * get model
     * @param country
     * @return
     */
    AppEpmModel getModel(String country);

    /**
     * get model list
     * @param country
     * @return
     */
    List<AppEpmModel> getModelList(List<String> country);

    /**
     * delete
     * @param country
     */
    void deleteModel(String country);

    /**
     * resetEpmHour
     * @param jsonObject
     */
    void resetEpmHour(JSONObject jsonObject);

    /**
     * resetEpmExponentiation
     * @param jsonObject
     */
    void resetEpmExponentiation(JSONObject jsonObject);
}
