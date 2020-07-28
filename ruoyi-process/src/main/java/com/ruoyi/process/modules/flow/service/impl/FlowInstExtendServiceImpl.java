package com.ruoyi.process.modules.flow.service.impl;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import com.ruoyi.process.modules.flow.domain.FlowConfigExtend;
import com.ruoyi.process.modules.flow.domain.FlowInstExtend;
import com.ruoyi.process.modules.flow.mapper.FlowInstExtendMapper;
import com.ruoyi.process.modules.flow.service.FlowConfigExtendService;
import com.ruoyi.process.modules.flow.service.FlowInstExtendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FlowInstExtendServiceImpl implements FlowInstExtendService {

	@Autowired
	FlowInstExtendMapper flowInstExtendMapper;
	@Autowired
	FlowConfigExtendService flowConfigExtendService;

	@Override
	public void save(FlowInstExtend flowInstExtend) {
		flowInstExtendMapper.save(flowInstExtend);
	}

	@Override
	public void update(FlowInstExtend flowInstExtend) {
		flowInstExtendMapper.update(flowInstExtend);
	}

	@Override
	public void updateTaskFields(String procInsId, String currTaskDefKey, String currTaskName, String currTaskType) {
		FlowInstExtend flowInstExtend = new FlowInstExtend();
		flowInstExtend.setProcInsId(procInsId);
		flowInstExtend.setCurrTaskDefKey(currTaskDefKey);
		flowInstExtend.setCurrTaskName(currTaskName);
		flowInstExtend.setCurrTaskType(currTaskType);
		flowInstExtend.setCreateBy(ShiroUtils.getLoginName());
		flowInstExtend.setCreateTime(new Date());
		flowInstExtendMapper.updateCurrTaskInfo(flowInstExtend);
	}

	@Override
	public FlowInstExtend findByProcInsId(String procInsId) {
		Assert.isTrue(StringUtils.isNotBlank(procInsId), "流程实例ID不能为空");
		List<FlowInstExtend> flowInstExtendList = flowInstExtendMapper.findByParams(ImmutableMap.of("procInsId", procInsId));
		if(null != flowInstExtendList && flowInstExtendList.size() > 0) {
			return flowInstExtendList.get(0);
		}
		return null;
	}

	@Override
	public FlowInstExtend createFlowInstExtend(ProcessInstance processInstance, Task task, Map<String, Object> formData) {
		return createFlowInstExtend(processInstance.getProcessDefinitionKey(), processInstance.getProcessDefinitionId(), processInstance.getProcessInstanceId(), task, formData);
	}

	@Override
	public FlowInstExtend createFlowInstExtend(String procDefKey,String procDefId, String procInsId, Task task, Map<String, Object> formData) {
		FlowConfigExtend flowConfigExtend = flowConfigExtendService.findByProcDefKey(procDefKey);
		if (null == flowConfigExtend) {
			throw new RuntimeException("流程扩展配置不存在，请配置后重试！");
		}
		String keyOneVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeyOne(), formData);
		String keyTwoVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeyTwo(), formData);
		String keyThreeVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeyThree(), formData);
		String keyFourVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeyFour(), formData);
		String keyFiveVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeyFive(), formData);
		String keySixVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeySix(), formData);
		String keySevenVal = FlowUtils.parseFlowKeyWordVal(flowConfigExtend.getKeySeven(), formData);

		FlowInstExtend flowInstExtend = new FlowInstExtend();
		flowInstExtend.setProcDefKey(procDefKey);
		flowInstExtend.setProcDefId(procDefId);
		flowInstExtend.setProcInsId(procInsId);
		flowInstExtend.setCurrTaskDefKey(task.getTaskDefinitionKey());
		flowInstExtend.setCurrTaskName(task.getName());
		flowInstExtend.setCurrTaskType(FlowConstant.TASK_TYPE_DRAFT);
		flowInstExtend.setKeyOne(keyOneVal);
		flowInstExtend.setKeyTwo(keyTwoVal);
		flowInstExtend.setKeyThree(keyThreeVal);
		flowInstExtend.setKeyFour(keyFourVal);
		flowInstExtend.setKeyFive(keyFiveVal);
		flowInstExtend.setKeySix(keySixVal);
		flowInstExtend.setKeySeven(keySevenVal);
		flowInstExtend.setCreateBy(ShiroUtils.getLoginName());
		flowInstExtend.setCreateTime(new Date());
		save(flowInstExtend);
		return flowInstExtend;
	}
}
