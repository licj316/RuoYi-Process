package com.ruoyi.process.modules.flow.service;

import com.ruoyi.process.modules.flow.domain.FlowData;

import java.util.List;

public interface FlowDataService {

	void save(FlowData flowData);

	List<FlowData> findByTaskId(String taskId);

	List<FlowData> findProcessData(String procInsId);

	void updateText(FlowData flowData);

	void saveToHisTaskData(String procInsId, String taskId);

	void deleteProcessData(String procInsId);
}
