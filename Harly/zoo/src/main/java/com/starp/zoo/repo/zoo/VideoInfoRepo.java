package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.VideoInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author curry by 2023/7/4
 */
@Repository
public interface VideoInfoRepo extends JpaRepository<VideoInfoModel, String>, JpaSpecificationExecutor<VideoInfoModel> {

	/**
	 * findByIdentification
	 * @param id
	 * @return
	 */
	VideoInfoModel findByIdentification(String id);

	/**
	 * findAllByType
	 * @param type
	 * @return java.util.List<com.starp.zoo.entity.zoo.VideoInfoModel>
	 * @author Curry
	 * @date 2023/7/5
	 */
	List<VideoInfoModel> findAllByType(String type);
}
