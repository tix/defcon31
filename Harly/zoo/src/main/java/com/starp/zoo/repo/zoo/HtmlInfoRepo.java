package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.HtmlInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/13.
 */
@Repository
public interface HtmlInfoRepo extends JpaRepository<HtmlInfoModel, String>, JpaSpecificationExecutor<HtmlInfoModel> {

    /**
      * 统计符合条件的HtmlInfoModel数量
      * @param appId
      * @param offerId
      * @return long
      */
    Long countByAppIdAndOfferId(String appId, String offerId);

    /**
      * 查找符合条件的HtmlInfoModelid
      * @param id
      * @return HtmlInfoModel
      */
    HtmlInfoModel findByIdentification(String id);

    /**
     * findByOfferIdAndUserId
     * @param offerId
     * @param userId
     * @return
     */
    List<HtmlInfoModel> findByOfferIdAndUserId(String offerId,String userId);


}
