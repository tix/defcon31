package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.ProductTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
public interface ProductTypeRepo extends JpaRepository<ProductTypeModel, String>, JpaSpecificationExecutor<ProductTypeModel> {

    /**
      * 查找符合条件的ProductTypeModel
      * @param id
      * @return ProductTypeModel
      */
    ProductTypeModel findFirstByIdentification(String id);

    /**
     * 按名称顺序查找所有
     * @return
     */
    @Query("select t from ProductTypeModel t order by t.productValue asc")
    List<ProductTypeModel> findQuery();
}
