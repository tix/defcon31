package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.MncPermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Vic on 2020/9/21
 */
@Repository
public interface MncPermissionRepo extends JpaRepository<MncPermissionModel, String>, JpaSpecificationExecutor<MncPermissionModel> {
}
