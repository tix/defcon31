package com.starp.zoo.repo.zoo;


import com.starp.zoo.entity.zoo.TiktokCallbackModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author vic.zhao
 * Created by vic, DATE: 2020/03/27.
 */
public interface TiktokCallbackRepo extends JpaRepository<TiktokCallbackModel, String> {
}
