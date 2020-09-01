package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowAttachmentDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowAttachmentDetailMapper {

	void save(FlowAttachmentDetail flowAttachmentDetail);

	void deleteById(String id);

}
