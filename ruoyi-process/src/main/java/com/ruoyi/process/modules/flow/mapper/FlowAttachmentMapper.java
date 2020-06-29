package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowAttachment;
import com.ruoyi.process.modules.flow.domain.FlowAttachmentConfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowAttachmentMapper {

	void batchSave(List<FlowAttachment> flowAttachmentList);

	FlowAttachment findById(String id);

	List<Map<String, Object>> listDetail(Map<String, Object> params);
}
