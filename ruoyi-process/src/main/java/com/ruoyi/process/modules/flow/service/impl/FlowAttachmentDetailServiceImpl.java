package com.ruoyi.process.modules.flow.service.impl;

import com.ruoyi.process.modules.flow.domain.FlowAttachmentDetail;
import com.ruoyi.process.modules.flow.mapper.FlowAttachmentDetailMapper;
import com.ruoyi.process.modules.flow.service.FlowAttachmentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowAttachmentDetailServiceImpl implements FlowAttachmentDetailService {

	@Autowired
	private FlowAttachmentDetailMapper flowAttachmentDetailMapper;

	@Override
	public void save(FlowAttachmentDetail flowAttachmentDetail) {
		flowAttachmentDetailMapper.save(flowAttachmentDetail);
	}

	@Override
	public void deleteById(String flowAttachmentDetailId) {
		flowAttachmentDetailMapper.deleteById(flowAttachmentDetailId);
	}

}
