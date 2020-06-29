package com.ruoyi.process.business.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

public class LeaveStartListener implements ExecutionListener {
	@Override
	public void notify(DelegateExecution execution) {
//		throw new RuntimeException("发起请假流程失败！");
		System.out.println("请假流程已启动！");
	}
}
