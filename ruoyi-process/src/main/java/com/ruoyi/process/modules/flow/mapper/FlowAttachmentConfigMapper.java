package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowAttachmentConfig;
import com.ruoyi.system.domain.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowAttachmentConfigMapper {

	List<Map<String, Object>> listPage(Map<String, Object> params);

	List<FlowAttachmentConfig> findByParams(Map<String, Object> params);
}
