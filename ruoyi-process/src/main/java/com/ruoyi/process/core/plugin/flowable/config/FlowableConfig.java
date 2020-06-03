package com.ruoyi.process.core.plugin.flowable.config;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.process.core.plugin.flowable.config.listener.NutzFwProcessEngineLifecycleListener;
import com.ruoyi.process.core.plugin.flowable.elbeans.multiinstance.MultiInstanceCompleteTask;
import com.ruoyi.process.core.plugin.flowable.elbeans.multiinstance.listener.MultiInstanceCompleteTaskListener;
import com.ruoyi.process.core.plugin.flowable.factory.CustomDefaultActivityBehaviorFactory;
import com.ruoyi.process.core.plugin.flowable.interceptor.CustomCreateUserTaskInterceptor;
import com.ruoyi.process.core.plugin.flowable.listener.ProxyFlowableEventListener;
import com.ruoyi.process.core.plugin.flowable.listener.handle.TastCreateSetCategoryHandle;
import com.ruoyi.process.core.plugin.flowable.transaction.NutzTransactionFactory;
import com.ruoyi.process.core.plugin.flowable.util.FlowStrongUuidGenerator;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.nutz.lang.Encoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

	@Autowired
	TastCreateSetCategoryHandle tastCreateSetCategoryHandle;

	@Autowired
	CustomCreateUserTaskInterceptor customCreateUserTaskInterceptor;

	@Override
	public void configure(SpringProcessEngineConfiguration engineConfiguration) {
		engineConfiguration.setDatabaseSchemaUpdate("true");
		engineConfiguration.setActivityFontName("黑体");
		engineConfiguration.setLabelFontName("黑体");
		engineConfiguration.setAnnotationFontName("黑体");
		engineConfiguration.setXmlEncoding(Encoding.UTF8);
		engineConfiguration.setEnableSafeBpmnXml(false);
		engineConfiguration.setHistoryLevel(HistoryLevel.AUDIT);
		//设置禁用Idm引擎
		engineConfiguration.setDisableIdmEngine(true);
		engineConfiguration.setIdGenerator(new FlowStrongUuidGenerator());
		engineConfiguration.setTransactionsExternallyManaged(true);
		engineConfiguration.setTransactionFactory(new NutzTransactionFactory());
		engineConfiguration.setProcessEngineLifecycleListener(new NutzFwProcessEngineLifecycleListener());
		engineConfiguration.setEventListeners(this.getGlobalFlowableEventListener());
		//自定义行为类工厂
		engineConfiguration.setActivityBehaviorFactory(new CustomDefaultActivityBehaviorFactory());
		engineConfiguration.setCreateUserTaskInterceptor(customCreateUserTaskInterceptor);
		this.initElBeans();
	}

	/**
	 * 设置全局事件监听器
	 *
	 * @return
	 */
	private List<FlowableEventListener> getGlobalFlowableEventListener() {
		return Arrays.asList(new ProxyFlowableEventListener(FlowableEngineEventType.TASK_CREATED, Arrays.asList(tastCreateSetCategoryHandle)));
	}


	/**
	 * 注册 flowable el bean
	 */
	public void initElBeans() {
		MultiInstanceCompleteTaskListener multiInstanceCompleteTaskListener = SpringUtils.getBean(MultiInstanceCompleteTaskListener.class);
		MultiInstanceCompleteTask multiInstanceCompleteTask = SpringUtils.getBean(MultiInstanceCompleteTask.class);
		ImmutableMap.of("multiInstanceCompleteTaskListener", multiInstanceCompleteTaskListener, "multiInstanceCompleteTask", multiInstanceCompleteTask);
	}
}
