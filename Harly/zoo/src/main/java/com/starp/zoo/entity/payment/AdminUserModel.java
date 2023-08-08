package com.starp.zoo.entity.payment;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/***
 * 管理员用户
 * @author YeahMobi
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "t_admin_user")
public class AdminUserModel extends EntityBase {
	
	/**
	 * 用户名
	 */
	@Column
	private String userName;
	
	/**
	 * 用户密码
	 */
	@Column
	private String userPass;
	
	/**
	 * 用户权限
	 */
	@Column
	private Integer permission;
	
	/**
	 * 用户token
	 */
	@Column
	private String token;
	
	/**
	 * 创建时间
	 */
	@Column
	private String createTimeStr;
	
	/**
	 * 超级管理员
	 */
	public static Integer ADMIN_SUPER = 1;
	
	/**
	 * 编辑人员
	 */
	public static Integer ADMIN_EDITOR = 2;
	
	/**
	 * 合作伙伴
	 */
	public static Integer ADMIN_PARTNER = 3;
	
	public AdminUserModel() {
	}
}
