package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AppUserEventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/12.
 */
@Repository
public interface AppUserEventRepo extends JpaRepository<AppUserEventModel, String> {
}
