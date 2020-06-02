/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.dto;

import com.ruoyi.process.core.plugin.flowable.enums.CallBackTypeEnum;
import com.ruoyi.process.core.plugin.flowable.enums.MultiInstanceLoopCharacteristicsType;
import com.ruoyi.process.core.plugin.flowable.enums.SignPassType;
import com.ruoyi.process.core.plugin.flowable.enums.TaskReviewerScopeEnum;

import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/26
 */
public class UserTaskExtensionDTO {
    /**
     * userTask 原始信息
     */
    String userTaskFormKey;
    String userTaskName;
    String userTaskId;
    String userTaskDocumentation;
    /**
     * 是否允许批复意见
     */
    boolean replyOpinion;
    /**
     * 是否允许手写签字
     */
    boolean handwritingSignature;
    /**
     * 同意按钮文字显示
     */
    String agreeButtonName;
    /**
     * 拒绝按钮文字显示
     */
    String refuseButtonName;
    /**
     * 批复意见框提示文字
     */
    String replyOpinionName;
    /**
     * 连线回退（驳回）
     */
    boolean connectionCallBack;
    /**
     * 回退类型
     */
    CallBackTypeEnum callBackType;
    /**
     * 可回退节点
     */
    String callBackNodes;
    String callBackNodesDesc;
    /**
     * 是否是多实例节点
     */
    boolean multiInstanceNode;
    /**
     * 是否允许加签
     */
    boolean addMultiInstance;
    /**
     * 是否允许减签
     */
    boolean delMultiInstance;
    /**
     * 减签后触发父实例完成判断
     */
    boolean delMultiInstanceExecutionIsCompleted;
    /**
     * 创建当前任务之前，执行表单数据动态赋值（动态代码逻辑）-应该是一段动态js，或者动态是绑定策略。
     * 不能返回给前台
     */
    String beforeCreateCurrentTaskFormDataDynamicAssignment;
    /**
     * 创建当前任务之后，执行表单数据动态赋值（动态代码逻辑）-应该是一段动态js，或者动态是绑定策略。
     * 不能返回给前台
     */
    String afterCreateCurrentTaskFormDataDynamicAssignment;
    /**
     * 完成当前任务后立即执行表单数据动态赋值（动态代码逻辑）-应该是一段动态js，或者动态是绑定策略。
     * 不能返回给前台
     */
    String formDataDynamicAssignment;
    /**
     * 指定当前用户步骤任务审核人范围
     */
    TaskReviewerScopeEnum taskReviewerScope;
    /**
     * 自由选择下一步审核人(下一步流程要确保能通过流程条件正确跳转至用户任务节点)
     */
    boolean dynamicFreeChoiceNextReviewerMode;
    /**
     * 指定的下一步审核人范围-用户可以在其中任选一个或多选
     */
    List<CandidateUsersDTO> nextReviewerCandidateUsers;
    /**
     * 分配给指定用户
     * TaskReviewerScopeEnum.SINGLE_USER 时生效
     */
    String assignee;
    /**
     * 候选用户角色组
     * TaskReviewerScopeEnum.USER_ROLE_GROUPS 时生效
     */
    List<CandidateGroupsDTO> candidateGroups;
    /**
     * 多个候选用户
     * TaskReviewerScopeEnum.MULTIPLE_USERS 时生效
     */
    List<CandidateUsersDTO> candidateUsers;
    /**
     * JavaIocBean人员选择器
     */
    String iocFlowAssignment;
    /**
     * 会签类型
     */
    MultiInstanceLoopCharacteristicsType multiInstanceLoopCharacteristics;
    /**
     * 通过类型
     */
    SignPassType signType;
    /**
     * 同意通过比例
     */
    Integer signScale;
    /**
     * 总实例数量
     */
    Integer signNrOfInstances;
    /**
     * 会签人全部参与处理
     */
    Boolean signAll;


    public static UserTaskExtensionDTO NEW() {
        return new Builder().callBackType(CallBackTypeEnum.NONE).addMultiInstance(false).build();
    }

    public String getAgreeButtonName() {
        return agreeButtonName == null ? "同意" : agreeButtonName;
    }

    public String getRefuseButtonName() {
        return refuseButtonName == null ? "拒绝" : refuseButtonName;
    }

    public String getUserTaskFormKey() {
        return userTaskFormKey;
    }

    public void setUserTaskFormKey(String userTaskFormKey) {
        this.userTaskFormKey = userTaskFormKey;
    }

    public String getUserTaskName() {
        return userTaskName;
    }

    public void setUserTaskName(String userTaskName) {
        this.userTaskName = userTaskName;
    }

    public String getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(String userTaskId) {
        this.userTaskId = userTaskId;
    }

    public String getUserTaskDocumentation() {
        return userTaskDocumentation;
    }

    public void setUserTaskDocumentation(String userTaskDocumentation) {
        this.userTaskDocumentation = userTaskDocumentation;
    }

    public boolean isReplyOpinion() {
        return replyOpinion;
    }

    public void setReplyOpinion(boolean replyOpinion) {
        this.replyOpinion = replyOpinion;
    }

    public boolean isHandwritingSignature() {
        return handwritingSignature;
    }

    public void setHandwritingSignature(boolean handwritingSignature) {
        this.handwritingSignature = handwritingSignature;
    }

    public void setAgreeButtonName(String agreeButtonName) {
        this.agreeButtonName = agreeButtonName;
    }

    public void setRefuseButtonName(String refuseButtonName) {
        this.refuseButtonName = refuseButtonName;
    }

    public String getReplyOpinionName() {
        return replyOpinionName;
    }

    public void setReplyOpinionName(String replyOpinionName) {
        this.replyOpinionName = replyOpinionName;
    }

    public boolean isConnectionCallBack() {
        return connectionCallBack;
    }

    public void setConnectionCallBack(boolean connectionCallBack) {
        this.connectionCallBack = connectionCallBack;
    }

    public CallBackTypeEnum getCallBackType() {
        return callBackType;
    }

    public void setCallBackType(CallBackTypeEnum callBackType) {
        this.callBackType = callBackType;
    }

    public String getCallBackNodes() {
        return callBackNodes;
    }

    public void setCallBackNodes(String callBackNodes) {
        this.callBackNodes = callBackNodes;
    }

    public String getCallBackNodesDesc() {
        return callBackNodesDesc;
    }

    public void setCallBackNodesDesc(String callBackNodesDesc) {
        this.callBackNodesDesc = callBackNodesDesc;
    }

    public boolean isMultiInstanceNode() {
        return multiInstanceNode;
    }

    public void setMultiInstanceNode(boolean multiInstanceNode) {
        this.multiInstanceNode = multiInstanceNode;
    }

    public boolean isAddMultiInstance() {
        return addMultiInstance;
    }

    public void setAddMultiInstance(boolean addMultiInstance) {
        this.addMultiInstance = addMultiInstance;
    }

    public boolean isDelMultiInstance() {
        return delMultiInstance;
    }

    public void setDelMultiInstance(boolean delMultiInstance) {
        this.delMultiInstance = delMultiInstance;
    }

    public boolean isDelMultiInstanceExecutionIsCompleted() {
        return delMultiInstanceExecutionIsCompleted;
    }

    public void setDelMultiInstanceExecutionIsCompleted(boolean delMultiInstanceExecutionIsCompleted) {
        this.delMultiInstanceExecutionIsCompleted = delMultiInstanceExecutionIsCompleted;
    }

    public String getBeforeCreateCurrentTaskFormDataDynamicAssignment() {
        return beforeCreateCurrentTaskFormDataDynamicAssignment;
    }

    public void setBeforeCreateCurrentTaskFormDataDynamicAssignment(String beforeCreateCurrentTaskFormDataDynamicAssignment) {
        this.beforeCreateCurrentTaskFormDataDynamicAssignment = beforeCreateCurrentTaskFormDataDynamicAssignment;
    }

    public String getAfterCreateCurrentTaskFormDataDynamicAssignment() {
        return afterCreateCurrentTaskFormDataDynamicAssignment;
    }

    public void setAfterCreateCurrentTaskFormDataDynamicAssignment(String afterCreateCurrentTaskFormDataDynamicAssignment) {
        this.afterCreateCurrentTaskFormDataDynamicAssignment = afterCreateCurrentTaskFormDataDynamicAssignment;
    }

    public String getFormDataDynamicAssignment() {
        return formDataDynamicAssignment;
    }

    public void setFormDataDynamicAssignment(String formDataDynamicAssignment) {
        this.formDataDynamicAssignment = formDataDynamicAssignment;
    }

    public TaskReviewerScopeEnum getTaskReviewerScope() {
        return taskReviewerScope;
    }

    public void setTaskReviewerScope(TaskReviewerScopeEnum taskReviewerScope) {
        this.taskReviewerScope = taskReviewerScope;
    }

    public boolean isDynamicFreeChoiceNextReviewerMode() {
        return dynamicFreeChoiceNextReviewerMode;
    }

    public void setDynamicFreeChoiceNextReviewerMode(boolean dynamicFreeChoiceNextReviewerMode) {
        this.dynamicFreeChoiceNextReviewerMode = dynamicFreeChoiceNextReviewerMode;
    }

    public List<CandidateUsersDTO> getNextReviewerCandidateUsers() {
        return nextReviewerCandidateUsers;
    }

    public void setNextReviewerCandidateUsers(List<CandidateUsersDTO> nextReviewerCandidateUsers) {
        this.nextReviewerCandidateUsers = nextReviewerCandidateUsers;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<CandidateGroupsDTO> getCandidateGroups() {
        return candidateGroups;
    }

    public void setCandidateGroups(List<CandidateGroupsDTO> candidateGroups) {
        this.candidateGroups = candidateGroups;
    }

    public List<CandidateUsersDTO> getCandidateUsers() {
        return candidateUsers;
    }

    public void setCandidateUsers(List<CandidateUsersDTO> candidateUsers) {
        this.candidateUsers = candidateUsers;
    }

    public String getIocFlowAssignment() {
        return iocFlowAssignment;
    }

    public void setIocFlowAssignment(String iocFlowAssignment) {
        this.iocFlowAssignment = iocFlowAssignment;
    }

    public MultiInstanceLoopCharacteristicsType getMultiInstanceLoopCharacteristics() {
        return multiInstanceLoopCharacteristics;
    }

    public void setMultiInstanceLoopCharacteristics(MultiInstanceLoopCharacteristicsType multiInstanceLoopCharacteristics) {
        this.multiInstanceLoopCharacteristics = multiInstanceLoopCharacteristics;
    }

    public SignPassType getSignType() {
        return signType;
    }

    public void setSignType(SignPassType signType) {
        this.signType = signType;
    }

    public Integer getSignScale() {
        return signScale;
    }

    public void setSignScale(Integer signScale) {
        this.signScale = signScale;
    }

    public Integer getSignNrOfInstances() {
        return signNrOfInstances;
    }

    public void setSignNrOfInstances(Integer signNrOfInstances) {
        this.signNrOfInstances = signNrOfInstances;
    }

    public Boolean getSignAll() {
        return signAll;
    }

    public void setSignAll(Boolean signAll) {
        this.signAll = signAll;
    }


    public static final class Builder {
        String userTaskFormKey;
        String userTaskName;
        String userTaskId;
        String userTaskDocumentation;
        boolean replyOpinion;
        boolean handwritingSignature;
        String agreeButtonName;
        String refuseButtonName;
        String replyOpinionName;
        boolean connectionCallBack;
        CallBackTypeEnum callBackType;
        String callBackNodes;
        String callBackNodesDesc;
        boolean multiInstanceNode;
        boolean addMultiInstance;
        boolean delMultiInstance;
        boolean delMultiInstanceExecutionIsCompleted;
        String beforeCreateCurrentTaskFormDataDynamicAssignment;
        String afterCreateCurrentTaskFormDataDynamicAssignment;
        String formDataDynamicAssignment;
        TaskReviewerScopeEnum taskReviewerScope;
        boolean dynamicFreeChoiceNextReviewerMode;
        List<CandidateUsersDTO> nextReviewerCandidateUsers;
        String assignee;
        List<CandidateGroupsDTO> candidateGroups;
        List<CandidateUsersDTO> candidateUsers;
        String iocFlowAssignment;
        MultiInstanceLoopCharacteristicsType multiInstanceLoopCharacteristics;
        SignPassType signType;
        Integer signScale;
        Integer signNrOfInstances;
        Boolean signAll;

        private Builder() {
        }

        public static Builder anUserTaskExtensionDTO() {
            return new Builder();
        }

        public Builder userTaskFormKey(String userTaskFormKey) {
            this.userTaskFormKey = userTaskFormKey;
            return this;
        }

        public Builder userTaskName(String userTaskName) {
            this.userTaskName = userTaskName;
            return this;
        }

        public Builder userTaskId(String userTaskId) {
            this.userTaskId = userTaskId;
            return this;
        }

        public Builder userTaskDocumentation(String userTaskDocumentation) {
            this.userTaskDocumentation = userTaskDocumentation;
            return this;
        }

        public Builder replyOpinion(boolean replyOpinion) {
            this.replyOpinion = replyOpinion;
            return this;
        }

        public Builder handwritingSignature(boolean handwritingSignature) {
            this.handwritingSignature = handwritingSignature;
            return this;
        }

        public Builder agreeButtonName(String agreeButtonName) {
            this.agreeButtonName = agreeButtonName;
            return this;
        }

        public Builder refuseButtonName(String refuseButtonName) {
            this.refuseButtonName = refuseButtonName;
            return this;
        }

        public Builder replyOpinionName(String replyOpinionName) {
            this.replyOpinionName = replyOpinionName;
            return this;
        }

        public Builder connectionCallBack(boolean connectionCallBack) {
            this.connectionCallBack = connectionCallBack;
            return this;
        }

        public Builder callBackType(CallBackTypeEnum callBackType) {
            this.callBackType = callBackType;
            return this;
        }

        public Builder callBackNodes(String callBackNodes) {
            this.callBackNodes = callBackNodes;
            return this;
        }

        public Builder callBackNodesDesc(String callBackNodesDesc) {
            this.callBackNodesDesc = callBackNodesDesc;
            return this;
        }

        public Builder multiInstanceNode(boolean multiInstanceNode) {
            this.multiInstanceNode = multiInstanceNode;
            return this;
        }

        public Builder addMultiInstance(boolean addMultiInstance) {
            this.addMultiInstance = addMultiInstance;
            return this;
        }

        public Builder delMultiInstance(boolean delMultiInstance) {
            this.delMultiInstance = delMultiInstance;
            return this;
        }

        public Builder delMultiInstanceExecutionIsCompleted(boolean delMultiInstanceExecutionIsCompleted) {
            this.delMultiInstanceExecutionIsCompleted = delMultiInstanceExecutionIsCompleted;
            return this;
        }

        public Builder beforeCreateCurrentTaskFormDataDynamicAssignment(String beforeCreateCurrentTaskFormDataDynamicAssignment) {
            this.beforeCreateCurrentTaskFormDataDynamicAssignment = beforeCreateCurrentTaskFormDataDynamicAssignment;
            return this;
        }

        public Builder afterCreateCurrentTaskFormDataDynamicAssignment(String afterCreateCurrentTaskFormDataDynamicAssignment) {
            this.afterCreateCurrentTaskFormDataDynamicAssignment = afterCreateCurrentTaskFormDataDynamicAssignment;
            return this;
        }

        public Builder formDataDynamicAssignment(String formDataDynamicAssignment) {
            this.formDataDynamicAssignment = formDataDynamicAssignment;
            return this;
        }

        public Builder taskReviewerScope(TaskReviewerScopeEnum taskReviewerScope) {
            this.taskReviewerScope = taskReviewerScope;
            return this;
        }

        public Builder dynamicFreeChoiceNextReviewerMode(boolean dynamicFreeChoiceNextReviewerMode) {
            this.dynamicFreeChoiceNextReviewerMode = dynamicFreeChoiceNextReviewerMode;
            return this;
        }

        public Builder nextReviewerCandidateUsers(List<CandidateUsersDTO> nextReviewerCandidateUsers) {
            this.nextReviewerCandidateUsers = nextReviewerCandidateUsers;
            return this;
        }

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder candidateGroups(List<CandidateGroupsDTO> candidateGroups) {
            this.candidateGroups = candidateGroups;
            return this;
        }

        public Builder candidateUsers(List<CandidateUsersDTO> candidateUsers) {
            this.candidateUsers = candidateUsers;
            return this;
        }

        public Builder iocFlowAssignment(String iocFlowAssignment) {
            this.iocFlowAssignment = iocFlowAssignment;
            return this;
        }

        public Builder multiInstanceLoopCharacteristics(MultiInstanceLoopCharacteristicsType multiInstanceLoopCharacteristics) {
            this.multiInstanceLoopCharacteristics = multiInstanceLoopCharacteristics;
            return this;
        }

        public Builder signType(SignPassType signType) {
            this.signType = signType;
            return this;
        }

        public Builder signScale(Integer signScale) {
            this.signScale = signScale;
            return this;
        }

        public Builder signNrOfInstances(Integer signNrOfInstances) {
            this.signNrOfInstances = signNrOfInstances;
            return this;
        }

        public Builder signAll(Boolean signAll) {
            this.signAll = signAll;
            return this;
        }

        public UserTaskExtensionDTO build() {
            UserTaskExtensionDTO userTaskExtensionDTO = new UserTaskExtensionDTO();
            userTaskExtensionDTO.setUserTaskFormKey(userTaskFormKey);
            userTaskExtensionDTO.setUserTaskName(userTaskName);
            userTaskExtensionDTO.setUserTaskId(userTaskId);
            userTaskExtensionDTO.setUserTaskDocumentation(userTaskDocumentation);
            userTaskExtensionDTO.setReplyOpinion(replyOpinion);
            userTaskExtensionDTO.setHandwritingSignature(handwritingSignature);
            userTaskExtensionDTO.setAgreeButtonName(agreeButtonName);
            userTaskExtensionDTO.setRefuseButtonName(refuseButtonName);
            userTaskExtensionDTO.setReplyOpinionName(replyOpinionName);
            userTaskExtensionDTO.setConnectionCallBack(connectionCallBack);
            userTaskExtensionDTO.setCallBackType(callBackType);
            userTaskExtensionDTO.setCallBackNodes(callBackNodes);
            userTaskExtensionDTO.setCallBackNodesDesc(callBackNodesDesc);
            userTaskExtensionDTO.setMultiInstanceNode(multiInstanceNode);
            userTaskExtensionDTO.setAddMultiInstance(addMultiInstance);
            userTaskExtensionDTO.setDelMultiInstance(delMultiInstance);
            userTaskExtensionDTO.setDelMultiInstanceExecutionIsCompleted(delMultiInstanceExecutionIsCompleted);
            userTaskExtensionDTO.setBeforeCreateCurrentTaskFormDataDynamicAssignment(beforeCreateCurrentTaskFormDataDynamicAssignment);
            userTaskExtensionDTO.setAfterCreateCurrentTaskFormDataDynamicAssignment(afterCreateCurrentTaskFormDataDynamicAssignment);
            userTaskExtensionDTO.setFormDataDynamicAssignment(formDataDynamicAssignment);
            userTaskExtensionDTO.setTaskReviewerScope(taskReviewerScope);
            userTaskExtensionDTO.setDynamicFreeChoiceNextReviewerMode(dynamicFreeChoiceNextReviewerMode);
            userTaskExtensionDTO.setNextReviewerCandidateUsers(nextReviewerCandidateUsers);
            userTaskExtensionDTO.setAssignee(assignee);
            userTaskExtensionDTO.setCandidateGroups(candidateGroups);
            userTaskExtensionDTO.setCandidateUsers(candidateUsers);
            userTaskExtensionDTO.setIocFlowAssignment(iocFlowAssignment);
            userTaskExtensionDTO.setMultiInstanceLoopCharacteristics(multiInstanceLoopCharacteristics);
            userTaskExtensionDTO.setSignType(signType);
            userTaskExtensionDTO.setSignScale(signScale);
            userTaskExtensionDTO.setSignNrOfInstances(signNrOfInstances);
            userTaskExtensionDTO.setSignAll(signAll);
            return userTaskExtensionDTO;
        }
    }
}
