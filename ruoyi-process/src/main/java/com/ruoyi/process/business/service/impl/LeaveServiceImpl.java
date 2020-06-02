/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.business.service.impl;

import com.ruoyi.process.business.domain.Leave;
import com.ruoyi.process.business.mapper.LeaveMapper;
import com.ruoyi.process.business.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月12日 10时56分55秒
 */
@Service("leaveService")
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	LeaveMapper leaveMapper;

	@Override
	public void save(Leave leave) {
		leaveMapper.save(leave);
	}

	@Override
	public Leave findById(String id) {
		return leaveMapper.findById(id);
	}
}
