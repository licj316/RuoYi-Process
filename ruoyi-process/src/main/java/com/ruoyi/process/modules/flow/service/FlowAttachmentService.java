package com.ruoyi.process.modules.flow.service;

import com.ruoyi.process.modules.flow.domain.FlowAttachment;

import java.util.List;
import java.util.Map;

public interface FlowAttachmentService {

	FlowAttachment findById(String id);

	void upload();

	List<Map<String, Object>> listDetail(Map<String, Object> params);
}
