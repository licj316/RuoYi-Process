package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowTypeMapper {
	FlowType findById(Long id);

	List<FlowType> findAllOrderByShortNoAsc();

	// TODO
	FlowType save(FlowType flowType);

	// TODO
	int update(FlowType flowType);

	int deleteById(String id);

	int count(Map<String, String> params);


}