package com.ruoyi.process.modules.flow.service;

import com.ruoyi.process.modules.flow.domain.FlowAttachmentConfig;

import java.util.List;
import java.util.Map;

public interface FlowAttachmentConfigService {

	List<Map<String, Object>> listPage(Map<String, Object> params);

	List<FlowAttachmentConfig> findByParams(Map<String, Object> params);
}
