package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.CategoryTagModel;
import com.starp.zoo.entity.zoo.TagModel;

import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
public interface ICategoryService {

    /**
     * 根据 category 主键查找
     * @param type
     * @param appId
     * @return
     */
    List<CategoryTagModel> findByCategoryId(int type, String appId);

    /**
     * 获取类别 map
     * @param type
     * @return
     */
    Map<String, List<TagModel>> getCategoryTagMap(int type);
}
