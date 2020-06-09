package com.ruoyi.process.modules.flow.vo;

import com.ruoyi.process.core.plugin.flowable.vo.FlowTaskVO;

import java.util.Map;

public class FlowParamVO {

	Map<String, Object> form;
	FlowTaskVO flow;

	public Map<String, Object> getForm() {
		return form;
	}

	public void setForm(Map<String, Object> form) {
		this.form = form;
	}

	public FlowTaskVO getFlow() {
		return flow;
	}

	public void setFlow(FlowTaskVO flow) {
		this.flow = flow;
	}
}
