package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.OssUserModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author vic
 */
public interface OssUserRepo extends JpaRepository<OssUserModel, String> {

}
