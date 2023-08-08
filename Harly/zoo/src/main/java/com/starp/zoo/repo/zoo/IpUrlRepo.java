package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.IpUrlModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author curry by 2023/7/24
 */
@Repository
public interface IpUrlRepo extends JpaRepository<IpUrlModel, String>, JpaSpecificationExecutor<IpUrlModel> {

	/**
	 * 返回根据ip查询的最新记录
	 * @param ip
	 * @return com.starp.zoo.entity.zoo.IpUrlModel
	 * @author Curry
	 * @date 2023/7/24
	 */
	IpUrlModel findFirstByIpOrderByCreateTimeDesc(String ip);
}
