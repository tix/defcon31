package com.starp.zoo.service.impl;

import com.starp.zoo.constant.AvaliableSystem;
import com.starp.zoo.constant.PermissionsType;
import com.starp.zoo.entity.payment.AdminPermissionsModel;
import com.starp.zoo.service.PermissionsService;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

/**
 *
 * @Author Vic
 * @Date 18:09 2018/12/18
 * @param
 * @return
 **/
@Service
public class PermissionsServiceImpl implements PermissionsService {

	private static final List<String> DEFAULT_PERMISSIONS = Arrays.asList("version", "dashboard-show");

	@PersistenceContext(unitName = "pEntityManger")
    EntityManager paymentEntityManager;

	@Override
	public List<AdminPermissionsModel> getPermitedMenuList(AvaliableSystem system) {
		// 查出全部菜单(MENU)
		String sql = "SELECT DISTINCT p.identification,p.parent_id,p.permissions_title,p.permissions_url,p.sort_index FROM t_admin_permissions p WHERE p.permissions_type=?1";

		Query nativeQuery = paymentEntityManager.createNativeQuery(sql, AdminPermissionsModel.class).setParameter(1, PermissionsType.MENU.name());
		List<AdminPermissionsModel> permissionList =  nativeQuery.getResultList();

		return assemblySystemMeun(system, permissionList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<AdminPermissionsModel> getUserPermitedMenuList(AvaliableSystem system, String userName) {
		Objects.requireNonNull(system);
		Objects.requireNonNull(userName);

//		Map<String, Object> paramMap = new HashMap<>();

		// 查出用户所拥有的权限(菜单级别MENU)
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(
				"SELECT DISTINCT p.identification,p.parent_id,p.permissions_title,p.permissions_url,p.sort_index ");
		sqlBuilder.append("FROM t_admin_user u JOIN t_admin_user_role ur ON u.identification = ur.userId ");
		sqlBuilder.append(
				"JOIN t_admin_role r ON ur.roleId = r.identification JOIN t_admin_role_permissions rp ON rp.role_id = r.identification ");
		sqlBuilder.append("JOIN t_admin_permissions p ON rp.permissions_id = p.identification ");
		sqlBuilder.append("WHERE p.permissions_type = ?1 AND u.userName = ?2");
		Session session = paymentEntityManager.unwrap(Session.class);
		Query nativeQuery = session.createNativeQuery(sqlBuilder.toString())
				.setParameter(1, PermissionsType.MENU.name())
				.setParameter(2, userName);
		nativeQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.TO_LIST);
		List<List> resultList = nativeQuery.getResultList();
		List<AdminPermissionsModel> adminMenu = new ArrayList<>();
		for(List result : resultList){
			AdminPermissionsModel adminPermissionsModel = new AdminPermissionsModel(result.get(0).toString(), result.get(1).toString(),
					result.get(2).toString(), result.get(3).toString(), Integer.valueOf(result.get(4).toString()));

			adminMenu.add(adminPermissionsModel);
		}
		return assemblySystemMeun(system, adminMenu);
	}

	@Override
	public AdminPermissionsModel getUserPermissions(String userName, String permissionsUrl) {
		Objects.requireNonNull(userName);
		Objects.requireNonNull(permissionsUrl);

		if (DEFAULT_PERMISSIONS.contains(permissionsUrl)) {
			return new AdminPermissionsModel(null, null, null, permissionsUrl, 1);
		}

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT p.identification, p.permissions_title, p.parent_id, p.permissions_url, p.sort_index ");
		sqlBuilder.append(
				"FROM t_admin_user u JOIN t_admin_user_role ur ON u.identification = ur.userId JOIN t_admin_role r ON r.identification = ur.roleId ");
		sqlBuilder.append("JOIN t_admin_role_permissions rp ON r.identification = rp.role_id ");
		sqlBuilder.append("JOIN t_admin_permissions p ON rp.permissions_id = p.identification ");
		sqlBuilder.append("WHERE u.userName = ?1 AND p.permissions_url= ?2 LIMIT 1");
		
		Query nativeQuery = paymentEntityManager.createNativeQuery(sqlBuilder.toString(), AdminPermissionsModel.class)
				.setParameter(1, userName)
				.setParameter(2, permissionsUrl);
		List<AdminPermissionsModel> userPermissions =  nativeQuery.getResultList();
		if (userPermissions.isEmpty()) {
			return null;
		}
		return userPermissions.get(0);
	}

	/**
	 * 组装菜单
	 * 
	 * @param system
	 * @param menuList
	 * @return
	 */
	private List<AdminPermissionsModel> assemblySystemMeun(AvaliableSystem system,
			List<AdminPermissionsModel> menuList) {
		if (menuList.isEmpty()) {
			return Collections.emptyList();
		}
		// 查询出系统目录(目录级别CATALOG)
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(
				"SELECT cap.identification,cap.parent_id,cap.permissions_title,cap.permissions_url,cap.sort_index ");
		sqlBuilder.append(
				"FROM t_admin_permissions pap JOIN t_admin_permissions cap ON pap.identification = cap.parent_id ");
		sqlBuilder.append(
				"WHERE pap.permissions_type = ?1 AND cap.permissions_type = ?2 AND pap.permissions_title = ?3");
		Query nativeQuery = paymentEntityManager.createNativeQuery(sqlBuilder.toString())
				.setParameter(1, PermissionsType.SYSTEM.name())
				.setParameter(2, PermissionsType.CATALOG.name())
				.setParameter(3, system.getSystemName());
		nativeQuery.unwrap(NativeQuery.class);
		List<Object[]> resultList =  nativeQuery.getResultList();
		List<AdminPermissionsModel> catalogs = new ArrayList<>();
		for(Object[] result : resultList){
			AdminPermissionsModel adminPermissionsModel = new AdminPermissionsModel(String.valueOf(result[0]), String.valueOf(result[1]),
					String.valueOf(result[2]), String.valueOf(result[3]), Integer.valueOf(String.valueOf(result[4])));
			catalogs.add(adminPermissionsModel);
		}
		if (catalogs.isEmpty()) {
			return Collections.emptyList();
		}
		// 设置目录的子项(MENU)
		for (AdminPermissionsModel catalog : catalogs) {
			List<AdminPermissionsModel> subPermissionsList = new ArrayList<>();
			catalog.setSubPermissions(subPermissionsList);
			Iterator<AdminPermissionsModel> it = menuList.iterator();
			while (it.hasNext()) {
				AdminPermissionsModel menu = it.next();
				if (catalog.isParent(menu)) {
					subPermissionsList.add(menu);
					it.remove();

				}
			}
		}
		Iterator<AdminPermissionsModel> it = catalogs.iterator();
		while (it.hasNext()) {
			AdminPermissionsModel catalog = it.next();

				// 下级菜单排序
				Collections.sort(catalog.getSubPermissions(), new Comparator<AdminPermissionsModel>() {

					@Override
					public int compare(AdminPermissionsModel menu, AdminPermissionsModel nextMenu) {
						return Integer.compare(menu.getSortIndex(), nextMenu.getSortIndex());
					}
				});
//			}
		}

		// 目录排序
		Collections.sort(catalogs, new Comparator<AdminPermissionsModel>() {

			@Override
			public int compare(AdminPermissionsModel catalog, AdminPermissionsModel nextCatalog) {
				return Integer.compare(catalog.getSortIndex(), nextCatalog.getSortIndex());
			}
		});
		return catalogs;
	}

}
