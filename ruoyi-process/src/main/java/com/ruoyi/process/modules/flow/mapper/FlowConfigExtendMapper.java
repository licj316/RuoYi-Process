package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowConfigExtend;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowConfigExtendMapper {

	void save(FlowConfigExtend flowConfigExtend);

	void update(FlowConfigExtend flowConfigExtend);

	List<FlowConfigExtend> findByParams(Map<String, Object> params);

}
