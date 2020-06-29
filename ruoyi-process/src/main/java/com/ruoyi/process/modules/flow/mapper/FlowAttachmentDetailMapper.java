package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowAttachment;
import com.ruoyi.process.modules.flow.domain.FlowAttachmentConfig;
import com.ruoyi.process.modules.flow.domain.FlowAttachmentDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowAttachmentDetailMapper {

	void save(FlowAttachmentDetail flowAttachmentDetail);

	void deleteById(String id);

}
