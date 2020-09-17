/*
 * Copyright (c) 2019- 2020 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2020/01/18 16:57:18
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.behavior;


import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.dto.CandidateGroupsDTO;
import com.ruoyi.process.core.plugin.flowable.dto.CandidateUsersDTO;
import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.enums.TaskReviewerScopeEnum;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/1/18
 */
public class CustomMultiInstanceBehaviorHelper {

    private static final Logger log = LogManager.getLogger(CustomMultiInstanceBehaviorHelper.class);

    UserTask userTask;

    UserTaskExtensionDTO taskExtensionDTO;


    public CustomMultiInstanceBehaviorHelper(MultiInstanceActivityBehavior multiInstanceActivityBehavior, Activity activity) {
        if (activity instanceof UserTask) {
            userTask = (UserTask) activity;
            taskExtensionDTO = FlowUtils.getUserTaskExtension(userTask);
            // TODO 暂时注释掉，不使用nutzfw提供的多实例节点处理方式
//            if (Objects.nonNull(taskExtensionDTO)) {
//                multiInstanceActivityBehavior.setCompletionCondition("${multiInstanceCompleteTask.accessCondition(execution)}");
//            }
        }

    }

    /**
     * 后端执行完成当前任务后脚本
     *
     * @param execution
     * @param userTaskExtensionDTO
     */
    public static void runBackstageCompletingCurrentTaskAfterAndUpdateFormData(DelegateExecution execution, UserTaskExtensionDTO userTaskExtensionDTO) {
        //TODO 后端执行完成当前任务后脚本
    }

    /**
     * 创建多实例的执行实例时
     *
     * @param multiInstanceRootExecution
     */
    public void createMultiInstances(DelegateExecution multiInstanceRootExecution) {
        if (Objects.nonNull(taskExtensionDTO)) {
//            List<String> assigneesCollection = new ArrayList<>();
//            TaskReviewerScopeEnum taskReviewerScope = taskExtensionDTO.getTaskReviewerScope();
            // TODO 暂不考虑会签
//            switch (taskReviewerScope) {
//                case MULTIPLE_USERS:
//                    assigneesCollection = Optional.ofNullable(taskExtensionDTO.getCandidateUsers()).orElse(new ArrayList<>(0)).stream().map(CandidateUsersDTO::getUserId).collect(Collectors.toList());
//                    break;
//                case USER_ROLE_GROUPS:
//                    for (int i = 0; i < taskExtensionDTO.getSignNrOfInstances(); i++) {
//                        //使用时需要拆分出来
//                        assigneesCollection.add(Optional.ofNullable(taskExtensionDTO.getCandidateGroups()).orElse(new ArrayList<>()).stream().map(CandidateGroupsDTO::getRoleKey).collect(Collectors.joining(",")));
//                    }
//                    break;
//                default:
//                    break;
//            }
//            if (CollectionUtils.isEmpty(assigneesCollection)) {
//                throw new RuntimeException("[" + userTask.getName() + "]未设置审批人员");
//            }

            String assignees = Objects.toString(multiInstanceRootExecution.getVariable(FlowConstant.NEXT_TASK_ASSIGNEES), "");
            if(StringUtils.isBlank(assignees)){
                throw new FlowableException("请选择下一步审批人");
            }
            //指定多实例审核人
            multiInstanceRootExecution.setVariable(FlowConstant.MULTIINSTANCE_ASSIGNEES_COLLECTION, Arrays.asList(assignees.split(",")));
        }
    }

    /**
     * 后端执行完成当前任务后脚本
     *
     * @param execution
     */
    public void runBackstageCompletingCurrentTaskAfterAndUpdateFormData(DelegateExecution execution) {
        CustomMultiInstanceBehaviorHelper.runBackstageCompletingCurrentTaskAfterAndUpdateFormData(execution, taskExtensionDTO);
    }
}
