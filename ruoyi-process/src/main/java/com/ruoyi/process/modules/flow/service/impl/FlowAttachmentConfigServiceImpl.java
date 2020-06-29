package com.ruoyi.process.modules.flow.service.impl;

import com.ruoyi.process.modules.flow.domain.FlowAttachmentConfig;
import com.ruoyi.process.modules.flow.mapper.FlowAttachmentConfigMapper;
import com.ruoyi.process.modules.flow.service.FlowAttachmentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FlowAttachmentConfigServiceImpl implements FlowAttachmentConfigService {

	@Autowired
	FlowAttachmentConfigMapper flowAttachmentConfigMapper;

	@Override
	public List<Map<String, Object>> listPage(Map<String, Object> params) {
		return flowAttachmentConfigMapper.listPage(params);
	}

	@Override
	public List<FlowAttachmentConfig> findByParams(Map<String, Object> params) {
		return flowAttachmentConfigMapper.findByParams(params);
	}
}
