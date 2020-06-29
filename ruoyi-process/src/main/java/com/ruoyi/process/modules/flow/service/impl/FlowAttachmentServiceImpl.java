package com.ruoyi.process.modules.flow.service.impl;

import com.ruoyi.process.modules.flow.domain.FlowAttachment;
import com.ruoyi.process.modules.flow.mapper.FlowAttachmentMapper;
import com.ruoyi.process.modules.flow.service.FlowAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FlowAttachmentServiceImpl implements FlowAttachmentService {

	@Autowired
	private FlowAttachmentMapper flowAttachmentMapper;

	@Override
	public FlowAttachment findById(String id) {
		return flowAttachmentMapper.findById(id);
	}

	@Override
	public void upload() {

	}

	@Override
	public List<Map<String, Object>> listDetail(Map<String, Object> params) {
		return flowAttachmentMapper.listDetail(params);
	}
}
