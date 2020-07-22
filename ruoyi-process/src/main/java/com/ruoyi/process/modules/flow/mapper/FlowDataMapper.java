package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowDataMapper {
	void save(FlowData flowData);

	void updateText(FlowData flowData);

	List<FlowData> findByParams(Map<String, Object> params);

	void saveToHisTaskData(@Param("procInsId") String procInsId,@Param("taskId") String taskId);

	void deleteProcessData(String procInsId);
}