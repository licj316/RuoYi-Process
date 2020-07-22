package com.ruoyi.process.business.listener;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.process.modules.flow.domain.FlowData;
import com.ruoyi.process.modules.flow.service.FlowDataService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseEndListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) {
		FlowDataService flowDataService = SpringUtils.getBean(FlowDataService.class);
		List<FlowData> flowDataList = flowDataService.findProcessData(execution.getRootProcessInstanceId());
		Map<String, String> formData = new HashMap<>();
		if (null != flowDataList && flowDataList.size() > 0) {
			for (FlowData flowData : flowDataList) {
				formData.put(flowData.getName(), flowData.getText());
			}
		}
		exec(formData, execution.getVariables());
		log.info("流程【" + execution.getProcessDefinitionId() + "】已结束！流程实例ID：" + execution.getRootProcessInstanceId());
	}

	protected abstract void exec(Map<String, String> formData, Map<String, Object> flowVariables);
}