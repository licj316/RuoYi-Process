package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysUser;

import java.util.List;

public interface ISysUserRoleService {

	/**
	 * 通过
	 * @param roleIdList
	 * @return
	 */
	List<SysUser> selectUserListByRoleCodeList(List<String> roleIdList);

}
