package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowTypeMapper {
	FlowType findById(Long id);

	List<FlowType> findAllOrderByShortNoAsc();

	int save(FlowType flowType);

	int update(FlowType flowType);

	int deleteById(Long id);

	int count(Map<String, Object> params);
}