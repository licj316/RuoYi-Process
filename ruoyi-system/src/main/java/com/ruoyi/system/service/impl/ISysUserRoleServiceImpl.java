package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ISysUserRoleServiceImpl implements ISysUserRoleService {

	@Autowired
	SysUserRoleMapper sysUserRoleMapper;

	@Override
	public List<SysUser> selectUserListByRoleKeyList(List<String> roleKeyList) {
		return sysUserRoleMapper.selectUserListByRoleKeyList(roleKeyList);
	}
}
