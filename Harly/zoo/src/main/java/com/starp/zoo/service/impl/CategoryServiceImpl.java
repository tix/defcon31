package com.starp.zoo.service.impl;

import com.starp.zoo.entity.zoo.CategoryTagModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTagModel;
import com.starp.zoo.entity.zoo.TagModel;
import com.starp.zoo.repo.zoo.CategoryTagRepo;
import com.starp.zoo.service.ICategoryService;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryTagRepo categoryTagRepo;


    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Override
    public List<CategoryTagModel> findByCategoryId(int type, String appId) {
        return categoryTagRepo.findAllByTypeAndCategoryId(type, appId);
    }

    @Override
    public Map<String, List<TagModel>> getCategoryTagMap(int type) {
        Map<String, List<TagModel>> map = new HashMap<>(1);
        String sql = "SELECT t1.category_id, t2.identification, t2.tag_name FROM t_category_tag t1 LEFT JOIN t_tag t2 ON t1.type=?1 AND t1.tag_id = t2.identification WHERE t2.identification IS NOT NULL";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter(1, type);
        List<Object[]> result = nativeQuery.getResultList();
        if(result != null && result.size() > 0){
            for(Object[] objects : result){
                TagModel tagModel = new TagModel();
                tagModel.setIdentification((String) objects[1]);
                tagModel.setTagName((String) objects[2]);
                String appId = (String) objects[0];
                List<TagModel> appTags = map.get(appId);
                if(appTags == null){
                    appTags = new ArrayList<>();
                }
                appTags.add(tagModel);
                map.put(appId, appTags);
            }
        }
        return map;
    }
}
