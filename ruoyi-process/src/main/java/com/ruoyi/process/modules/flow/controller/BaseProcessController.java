package com.ruoyi.process.modules.flow.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseProcessController extends BaseController {

	@Autowired
	ISysRoleService sysRoleService;

	protected SysUser getCurrUser() {
		return ShiroUtils.getSysUser();
	}

	protected List<SysRole> getUserRoleList(Long userId) {
		return sysRoleService.selectRolesByUserId(userId);
	}
}
