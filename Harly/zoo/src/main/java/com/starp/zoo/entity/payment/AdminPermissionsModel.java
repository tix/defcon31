package com.starp.zoo.entity.payment;

import com.starp.zoo.constant.PermissionsType;
import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * 管理员权限
 * 
 * @author yeahmobi
 *
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "t_admin_permissions", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "permissions_title", "permissions_type", "parent_id" }) })
public class AdminPermissionsModel extends EntityBase {

	/** 父权限ID */
	@Column(name = "parent_id")
	private String parentId;
	/** 权限标题 */
	@Column(name = "permissions_title", nullable = false)
	private String permissionsTitle;
	/** 权限URL */
	@Column(name = "permissions_url")
	private String permissionsUrl;
	/** 权限类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "permissions_type", nullable = false)
	private PermissionsType permissionsType;

	/** 权限等级 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "permissions_level", nullable = false)
	private PermissionsType permissionsLevel;

	/** 排序索引 */
	@Column(name = "sort_index", nullable = false)
	private Integer sortIndex;
	/** 次级权限 */
	@Transient
	private List<AdminPermissionsModel> subPermissions;
	@Transient
	private boolean permited;

	public AdminPermissionsModel() {
	}

	public AdminPermissionsModel(String identification, String parentId, String permissionsTitle, String permissionsUrl,
                                 Integer sortIndex) {
		setIdentification(identification);
		this.parentId = parentId;
		this.permissionsTitle = permissionsTitle;
		this.permissionsUrl = permissionsUrl;
		this.sortIndex = sortIndex;
	}

	public boolean isParent(AdminPermissionsModel other) {
		Objects.requireNonNull(other);

		if (other.getParentId() == null) {
			return false;
		}

		if (other.getParentId().equals(getIdentification())) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "AdminPermissionsModel [parentId=" + parentId + ", permissionsTitle=" + permissionsTitle
				+ ", permissionsUrl=" + permissionsUrl + ", permissionsType=" + permissionsType + ", permissionsLevel="
				+ permissionsLevel + ", sortIndex=" + sortIndex + ", subPermissions=" + subPermissions + ", permited="
				+ permited + "]";
	}

}
