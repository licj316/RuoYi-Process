package com.ruoyi.process.modules.flow.service.impl;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.process.modules.flow.domain.FlowData;
import com.ruoyi.process.modules.flow.mapper.FlowDataMapper;
import com.ruoyi.process.modules.flow.service.FlowDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowDataServiceImpl implements FlowDataService {

	@Autowired
	FlowDataMapper flowDataMapper;

	@Override
	public void save(FlowData flowData) {
		flowDataMapper.save(flowData);
	}

	@Override
	public List<FlowData> findByTaskId(String taskId) {
		return flowDataMapper.findByParams(ImmutableMap.of("taskId", taskId));
	}

	@Override
	public List<FlowData> findProcessData(String procInsId) {
		ImmutableMap<String, Object> params = ImmutableMap.of("procInsId", procInsId, "nullTaskId", "true");
		return flowDataMapper.findByParams(params);
	}

	@Override
	public void updateText(FlowData flowData) {
		flowDataMapper.updateText(flowData);
	}

	@Override
	public void saveToHisTaskData(String procInsId, String taskId) {
		flowDataMapper.saveToHisTaskData(procInsId, taskId);
	}

	@Override
	public void deleteProcessData(String procInsId) {
		flowDataMapper.deleteProcessData(procInsId);
	}
}
