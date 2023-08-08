package com.starp.zoo.repo.payment;

import com.starp.zoo.entity.payment.AdminUserModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @Author vic
 * @Date 18:12 2018/12/18
 * @param
 * @return
 **/
public interface AdminUserRepo extends JpaRepository<AdminUserModel, String> {

    /**
     * 获取AdminUserModel
     * @Author David
     * @Date 17:54 2018/12/18
     * @param  userName
     * @param userPass
     * @return com.starp.zoo.entity.payment.AdminUserModel
     **/
    AdminUserModel findFirstByUserNameAndUserPass(String userName, String userPass);
}
