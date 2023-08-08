package com.starp.zoo.service;

import com.starp.zoo.constant.AvaliableSystem;
import com.starp.zoo.entity.payment.AdminPermissionsModel;

import java.util.List;

/**
 *
 * @Author Vic
 * @Date 18:09 2018/12/18
 * @param
 * @return
 **/
public interface PermissionsService {
	/**
	 * 获取用户菜单(用户无权限则菜单不显示)
	 * 
	 * @param system
	 * @param userName
	 * @return
	 */
	List<AdminPermissionsModel> getUserPermitedMenuList(AvaliableSystem system, String userName);

	/**
	 * 获取全部菜单(用户无权限也显示菜单)
	 * 
	 * @param system
	 * @return
	 */
	List<AdminPermissionsModel> getPermitedMenuList(AvaliableSystem system);

	/**
	 * 取用户对应的权限 无此权限返回null
	 * 
	 * @param userName
	 * @param permissionsUrl
	 * @return
	 */
	AdminPermissionsModel getUserPermissions(String userName, String permissionsUrl);
}
