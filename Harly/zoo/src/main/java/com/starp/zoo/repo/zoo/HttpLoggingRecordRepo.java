package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.HttpLoggingRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author magic
 * @date 2021/9/27
 */
@Repository
public interface HttpLoggingRecordRepo extends JpaRepository<HttpLoggingRecordModel, String>, JpaSpecificationExecutor<HttpLoggingRecordModel> {

}
