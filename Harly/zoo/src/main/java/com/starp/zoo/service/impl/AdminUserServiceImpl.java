package com.starp.zoo.service.impl;

import com.starp.zoo.entity.payment.AdminUserModel;
import com.starp.zoo.repo.payment.AdminUserRepo;
import com.starp.zoo.service.IAdminUserService;
import com.starp.zoo.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @Author vic
 * @Date 18:08 2018/12/18
 * @param
 * @return
 **/
@Service("adminUserService")
public class AdminUserServiceImpl implements IAdminUserService {

	@Resource
	private AdminUserRepo adminUserRepo;

	public AdminUserServiceImpl() {
	}

	@Override
	public AdminUserModel login(String userName, String password) throws Exception {
		return adminUserRepo.findFirstByUserNameAndUserPass(userName, password);
	}

	@Override
	public List<AdminUserModel> getAll() throws Exception {

		List<AdminUserModel> userList = adminUserRepo.findAll();
		for (AdminUserModel adminUserModel : userList) {
			if (adminUserModel.getCreateTime() != null) {
				long date = adminUserModel.getCreateTime().getTime();
				String dateStr = DateUtil.getDateStr(date);
				adminUserModel.setCreateTimeStr(dateStr);
			}
		}
		return userList;
	}
}
