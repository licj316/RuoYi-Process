package com.ruoyi.process.modules.flow.service;

import com.ruoyi.process.modules.flow.domain.FlowConfigExtend;

public interface FlowConfigExtendService {

	void save(FlowConfigExtend flowConfigExtend);

	void update(FlowConfigExtend flowConfigExtend);

	FlowConfigExtend findByProcDefKey(String procDefKey);

}
