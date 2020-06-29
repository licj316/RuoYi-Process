/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.interceptor;

import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.service.FlowCacheService;
import com.ruoyi.process.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.ruoyi.process.core.plugin.flowable.service.FlowTaskService;
import com.ruoyi.process.modules.flow.executor.ExternalFormExecutor;
import com.ruoyi.process.utils.JsContex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
import org.flowable.engine.interceptor.CreateUserTaskInterceptor;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.nutz.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/27
 */
@Component
public class CustomCreateUserTaskInterceptor implements CreateUserTaskInterceptor {

    private static final Logger log = LogManager.getLogger(CustomCreateUserTaskInterceptor.class);

    FlowCacheService flowCacheService;
    FlowTaskService flowTaskService;
    FlowProcessDefinitionService flowProcessDefinitionService;
    boolean inited = false;

    @Autowired
    public CustomCreateUserTaskInterceptor(FlowCacheService flowCacheService, FlowTaskService flowTaskService, FlowProcessDefinitionService flowProcessDefinitionService) {
        if (inited == false) {
            this.flowCacheService = flowCacheService;
            this.flowTaskService = flowTaskService;
            this.flowProcessDefinitionService = flowProcessDefinitionService;
            inited = true;
        }
    }


    @Override
    public void beforeCreateUserTask(CreateUserTaskBeforeContext context) {
        Context ctx = new Context(
                context.getExecution().getProcessDefinitionId(),
                context.getUserTask().getId(),
                context.getExecution().getProcessInstanceBusinessKey(),
                context.getUserTask(),
                context.getExecution(),
                true,
                false,
                null);
        this.execute(ctx);
    }

    @Override
    public void afterCreateUserTask(CreateUserTaskAfterContext context) {
        Context ctx = new Context(
                context.getExecution().getProcessDefinitionId(),
                context.getUserTask().getId(),
                context.getExecution().getProcessInstanceBusinessKey(),
                context.getUserTask(),
                context.getExecution(),
                false,
                true,
                context.getTaskEntity());
        this.execute(ctx);
    }

    private void execute(Context ctx) {
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(ctx.getTaskDefinitionKey(), ctx.getProcessDefinitionId());
        ExternalFormExecutor externalFormExecutor = flowProcessDefinitionService.getExternalFormExecutor(ctx.getProcessDefinitionId());
        if (ctx.isBefore()) {
            if (Strings.isNotBlank(dto.getBeforeCreateCurrentTaskFormDataDynamicAssignment())) {
                Map formData = externalFormExecutor.loadFormData(ctx.getProcessInstanceBusinessKey());
                this.beforeEvalJs(formData, dto);
                externalFormExecutor.insertOrUpdateFormData(formData);
            }
            //创建用户任务前执行
            externalFormExecutor.beforeCreateUserTask(ctx.getExecution(), ctx.getUserTask(), dto, ctx.getProcessInstanceBusinessKey());
        }
        if (ctx.isAfter()) {
            if (Strings.isNotBlank(dto.getAfterCreateCurrentTaskFormDataDynamicAssignment())) {
                Map formData = externalFormExecutor.loadFormData(ctx.getProcessInstanceBusinessKey());
                this.afterEvalJs(formData, dto, ctx.getTaskEntity());
                externalFormExecutor.insertOrUpdateFormData(formData);
            }
            //创建用户任务前执行
            externalFormExecutor.afterCreateUserTask(ctx.getExecution(), ctx.getUserTask(), dto, ctx.getProcessInstanceBusinessKey(), ctx.getTaskEntity());
        }
    }

    /**
     * @param formData
     * @param dto
     * @return
     */
    private Map beforeEvalJs(Map formData, UserTaskExtensionDTO dto) {
        StringBuffer jsCode = new StringBuffer("function runBeforeCreateCurrentTaskFormDataDynamicAssignment(formData,dto){ " + dto.getBeforeCreateCurrentTaskFormDataDynamicAssignment() + "  return formData; }");
        try {
            JsContex.get().compile(jsCode.toString());
            JsContex.get().eval(jsCode.toString());
            Object result = JsContex.get().invokeFunction("runBeforeCreateCurrentTaskFormDataDynamicAssignment", formData, dto);
            formData = (Map) result;
        } catch (Exception e) {
            log.error("解析动态JS错误", e);
            throw new RuntimeException("解析动态JS错误");
        }
        return formData;
    }

    /**
     * @param formData
     * @param dto
     * @return
     */
    private Map afterEvalJs(Map formData, UserTaskExtensionDTO dto, TaskEntity taskEntity) {
        StringBuffer jsCode = new StringBuffer("function runAfterCreateCurrentTaskFormDataDynamicAssignment(formData,dto,task){ " + dto.getAfterCreateCurrentTaskFormDataDynamicAssignment() + "  return formData; }");
        try {
            JsContex.get().compile(jsCode.toString());
            JsContex.get().eval(jsCode.toString());
            Object result = JsContex.get().invokeFunction("runAfterCreateCurrentTaskFormDataDynamicAssignment", formData, dto, taskEntity);
            formData = (Map) result;
        } catch (Exception e) {
            log.error("解析动态JS错误", e);
            throw new RuntimeException("解析动态JS错误");
        }
        return formData;
    }

    class Context {
        String processDefinitionId;
        String taskDefinitionKey;
        String processInstanceBusinessKey;
        UserTask userTask;
        DelegateExecution execution;
        boolean before;
        boolean after;
        TaskEntity taskEntity;

        public Context() {
        }

        public Context(String processDefinitionId, String taskDefinitionKey, String processInstanceBusinessKey, UserTask userTask, DelegateExecution execution, boolean before, boolean after, TaskEntity taskEntity) {
            this.processDefinitionId = processDefinitionId;
            this.taskDefinitionKey = taskDefinitionKey;
            this.processInstanceBusinessKey = processInstanceBusinessKey;
            this.userTask = userTask;
            this.execution = execution;
            this.before = before;
            this.after = after;
            this.taskEntity = taskEntity;
        }

        public String getProcessDefinitionId() {
            return processDefinitionId;
        }

        public void setProcessDefinitionId(String processDefinitionId) {
            this.processDefinitionId = processDefinitionId;
        }

        public String getTaskDefinitionKey() {
            return taskDefinitionKey;
        }

        public void setTaskDefinitionKey(String taskDefinitionKey) {
            this.taskDefinitionKey = taskDefinitionKey;
        }

        public String getProcessInstanceBusinessKey() {
            return processInstanceBusinessKey;
        }

        public void setProcessInstanceBusinessKey(String processInstanceBusinessKey) {
            this.processInstanceBusinessKey = processInstanceBusinessKey;
        }

        public UserTask getUserTask() {
            return userTask;
        }

        public void setUserTask(UserTask userTask) {
            this.userTask = userTask;
        }

        public DelegateExecution getExecution() {
            return execution;
        }

        public void setExecution(DelegateExecution execution) {
            this.execution = execution;
        }

        public boolean isBefore() {
            return before;
        }

        public void setBefore(boolean before) {
            this.before = before;
        }

        public boolean isAfter() {
            return after;
        }

        public void setAfter(boolean after) {
            this.after = after;
        }

        public TaskEntity getTaskEntity() {
            return taskEntity;
        }

        public void setTaskEntity(TaskEntity taskEntity) {
            this.taskEntity = taskEntity;
        }
    }
}
