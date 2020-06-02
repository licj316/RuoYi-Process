/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.listener.handle;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.process.core.plugin.flowable.listener.EventListenerHandle;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.*;
import org.nutz.ioc.Ioc;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 帮助把流程需要的service初始化好
 */
public abstract class BaseEventListenerHandle implements EventListenerHandle {

    protected RuntimeService runtimeService;

    protected IdentityService identityService;

    protected TaskService taskService;

    protected HistoryService historyService;

    protected ManagementService managementService;

    protected FormService formService;

    protected DynamicBpmnService dynamicBpmnService;

    private boolean inited = false;

    public BaseEventListenerHandle() {
    }

    public void init() {
        if (this.inited == false) {
            this.runtimeService = SpringUtils.getBean(RuntimeService.class);
            this.identityService = SpringUtils.getBean(IdentityService.class);
            this.taskService = SpringUtils.getBean(TaskService.class);
            this.historyService = SpringUtils.getBean(HistoryService.class);
            this.managementService = SpringUtils.getBean(ManagementService.class);
            this.formService = SpringUtils.getBean(FormService.class);
            this.dynamicBpmnService = SpringUtils.getBean(DynamicBpmnService.class);
            this.inited = true;
        }
    }

    @Override
    public void onEvent(FlowableEvent event) {
        this.init();
        this.execute(event);
    }

    /**
     * 执行事件
     *
     * @param event
     */
    public abstract void execute(FlowableEvent event);
}
