package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.HttpLoggingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/12.
 */
@Repository
public interface HttpLoggingRepo extends JpaRepository<HttpLoggingModel, String> {
}
