package com.starp.zoo.constant;

/**
 * 权限类型定义
 * 
 * @author yeahmobi
 *
 */
public enum PermissionsType {

	/**
	 * 系统
	 */
	SYSTEM,
	/**
	 * 目录
	 */
	CATALOG,
	/**
	 * 菜单
	 */
	MENU,
	/**
	 * 方法，功能
	 */

	FUNCTION;

	public PermissionsType advancedPermissionsType(PermissionsType permissionsType) {

		for (PermissionsType type : PermissionsType.values()) {
			if (permissionsType.ordinal() - 1 == type.ordinal()) {
				return type;
			}
		}
		return null;
	}
}
