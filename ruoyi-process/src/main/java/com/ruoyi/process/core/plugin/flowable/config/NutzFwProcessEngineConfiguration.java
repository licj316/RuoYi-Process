/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/11/24 12:10:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.config;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.process.core.plugin.flowable.config.listener.NutzFwProcessEngineLifecycleListener;
import com.ruoyi.process.core.plugin.flowable.elbeans.IocElBeans;
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
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 */
@Component
public class NutzFwProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {

    @Autowired
    DataSource dataSource;
    @Autowired
    TastCreateSetCategoryHandle tastCreateSetCategoryHandle;
//    @Autowired
//    DepartmentLeaderService departmentLeaderService;
    @Autowired
    CustomCreateUserTaskInterceptor customCreateUserTaskInterceptor;

    /**
     * 变量与父类变量重名如果不覆盖 setDataSource 方法，注入 dataSource 时会导致当前类的dataSource为null
     *
     * @param dataSource
     * @return
     */
    @Override
    public ProcessEngineConfiguration setDataSource(DataSource dataSource) {
        super.dataSource = dataSource;
        this.dataSource = dataSource;
        return this;
    }

    @Override
//    @Bean
    public ProcessEngine buildProcessEngine() {
        this.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        this.setActivityFontName("黑体");
        this.setLabelFontName("黑体");
        this.setAnnotationFontName("黑体");
        this.setXmlEncoding(Encoding.UTF8);
        this.setEnableSafeBpmnXml(false);
        this.setHistoryLevel(HistoryLevel.AUDIT);
        //设置禁用Idm引擎
        this.setDisableIdmEngine(true);
        this.setIdGenerator(new FlowStrongUuidGenerator());
        this.setTransactionsExternallyManaged(true);
        this.setTransactionFactory(new NutzTransactionFactory());
        this.setProcessEngineLifecycleListener(new NutzFwProcessEngineLifecycleListener());
        this.setEventListeners(this.getGlobalFlowableEventListener());
        //自定义行为类工厂
        this.activityBehaviorFactory = new CustomDefaultActivityBehaviorFactory();// CustomCreateUserTaskInterceptor
        this.setCreateUserTaskInterceptor(customCreateUserTaskInterceptor);
        this.initElBeans();
        return super.buildProcessEngine();
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
