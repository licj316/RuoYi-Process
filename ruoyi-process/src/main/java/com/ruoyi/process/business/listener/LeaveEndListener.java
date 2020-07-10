package com.ruoyi.process.business.listener;

import java.util.Map;

public class LeaveEndListener extends BaseEndListener{
	@Override
	protected void exec(Map<String, String> formData, Map<String, Object> flowVariables) {

	}

//	@Override
//	public void notify(DelegateExecution execution) {
//		System.out.println("请假流程已结束！");
//		Map<String, Object> variables = execution.getVariables();
//		for(Map.Entry<String, Object> entry : variables.entrySet()) {
//			System.out.println("key=====" + entry.getKey());
//			System.out.println("value===" + entry.getValue());
//		}
//		throw new RuntimeException("结束请假流程失败！");
//	}


}
