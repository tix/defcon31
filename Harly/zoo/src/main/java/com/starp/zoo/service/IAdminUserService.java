package com.starp.zoo.service;

import com.starp.zoo.entity.payment.AdminUserModel;

import java.util.List;

/**
 *
 * @Author Vic
 * @Date 18:09 2018/12/18
 * @param
 * @return
 **/
public interface IAdminUserService {
	
	/**
	 * 用户登陆
	 * @param userName
	 * @param password
	 * @return token 用户验证用户所有的 更新操作
	 * @throws Exception
	 */
	AdminUserModel login(String userName, String password) throws Exception;
	
	/**
	 * 获取所有的管理员用户
	 * @return 所有的管理员用户
	 * @throws Exception
	 */
	List<AdminUserModel> getAll() throws Exception;
}
