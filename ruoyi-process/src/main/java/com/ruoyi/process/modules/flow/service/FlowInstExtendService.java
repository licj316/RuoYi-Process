package com.ruoyi.process.modules.flow.service;

import com.ruoyi.process.modules.flow.domain.FlowInstExtend;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.Map;

public interface FlowInstExtendService {

	void save(FlowInstExtend flowInstExtend);

	void update(FlowInstExtend flowInstExtend);

	void updateTaskFields(String procInsId, String currTaskDefKey, String currTaskName, String currTaskType);

	FlowInstExtend findByProcInsId(String procInsId);

	FlowInstExtend createFlowInstExtend(ProcessInstance processInstance, Task task, Map<String, Object> formData);

	FlowInstExtend createFlowInstExtend(String procDefKey, String procDefId, String procInsId, Task task, Map<String, Object> formData);

}
