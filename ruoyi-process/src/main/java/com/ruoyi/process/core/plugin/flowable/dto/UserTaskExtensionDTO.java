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
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/26
 */
public class UserTaskExtensionDTO {
    /**
     * userTask 原始信息
     */
    String userTaskId;
    String userTaskName;
    String userTaskFormKey;
    String userTaskDocumentation;
    /**
     * 是否允许审批意见
     */
    boolean approvalOpinion;

    /**
     * 同意按钮文字显示
     */
    String agreeButtonName;
    /**
     * 拒绝按钮文字显示
     */
    String refuseButtonName;
//    /**
//     * 批复意见框提示文字
//     */
//    String replyOpinionName;

    /**
     * 是否是多实例节点
     */
    boolean multiInstanceNode;
    /**
     * 是否允许加签
     */
    boolean addMultiInstance;
    /**
     * 当前用户步骤任务审核人类型
     */
    TaskReviewerScopeEnum taskReviewerScope;
    /**
     * 当前用户步骤审核人类型取值范围
     */
    List<Map<String, String>> taskReviewerValue;
    /**
     * 页面需要显示的按钮
     */
    List<String> taskButtons;
    /**
     * 可回退节点
     */
    List<String> callBackNodes;

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


    public String getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(String userTaskId) {
        this.userTaskId = userTaskId;
    }

    public String getUserTaskName() {
        return userTaskName;
    }

    public void setUserTaskName(String userTaskName) {
        this.userTaskName = userTaskName;
    }

    public String getUserTaskFormKey() {
        return userTaskFormKey;
    }

    public void setUserTaskFormKey(String userTaskFormKey) {
        this.userTaskFormKey = userTaskFormKey;
    }

    public String getUserTaskDocumentation() {
        return userTaskDocumentation;
    }

    public void setUserTaskDocumentation(String userTaskDocumentation) {
        this.userTaskDocumentation = userTaskDocumentation;
    }

    public boolean isApprovalOpinion() {
        return approvalOpinion;
    }

    public void setApprovalOpinion(boolean approvalOpinion) {
        this.approvalOpinion = approvalOpinion;
    }

    public String getAgreeButtonName() {
        return agreeButtonName;
    }

    public void setAgreeButtonName(String agreeButtonName) {
        this.agreeButtonName = agreeButtonName;
    }

    public String getRefuseButtonName() {
        return refuseButtonName;
    }

    public void setRefuseButtonName(String refuseButtonName) {
        this.refuseButtonName = refuseButtonName;
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

    public TaskReviewerScopeEnum getTaskReviewerScope() {
        return taskReviewerScope;
    }

    public void setTaskReviewerScope(TaskReviewerScopeEnum taskReviewerScope) {
        this.taskReviewerScope = taskReviewerScope;
    }

    public List<Map<String, String>> getTaskReviewerValue() {
        return taskReviewerValue;
    }

    public void setTaskReviewerValue(List<Map<String, String>> taskReviewerValue) {
        this.taskReviewerValue = taskReviewerValue;
    }

    public List<String> getTaskButtons() {
        return taskButtons;
    }

    public void setTaskButtons(List<String> taskButtons) {
        this.taskButtons = taskButtons;
    }

    public List<String> getCallBackNodes() {
        return callBackNodes;
    }

    public void setCallBackNodes(List<String> callBackNodes) {
        this.callBackNodes = callBackNodes;
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

    public static UserTaskExtensionDTO NEW() {
        return new Builder().addMultiInstance(false).build();
    }

    public static final class Builder {
        String userTaskId;
        String userTaskName;
        String userTaskFormKey;
        String userTaskDocumentation;
        boolean approvalOpinion;
        String agreeButtonName;
        String refuseButtonName;
        boolean multiInstanceNode;
        boolean addMultiInstance;
        TaskReviewerScopeEnum taskReviewerScope;
        List<Map<String, String>> taskReviewerValue;
        List<String> taskButtons;
        List<String> callBackNodes;

        private Builder() {
        }

        public static Builder anUserTaskExtensionDTO() {
            return new Builder();
        }

        public Builder userTaskId(String userTaskId) {
            this.userTaskId = userTaskId;
            return this;
        }

        public Builder userTaskName(String userTaskName) {
            this.userTaskName = userTaskName;
            return this;
        }

        public Builder userTaskFormKey(String userTaskFormKey) {
            this.userTaskFormKey = userTaskFormKey;
            return this;
        }

        public Builder userTaskDocumentation(String userTaskDocumentation) {
            this.userTaskDocumentation = userTaskDocumentation;
            return this;
        }

        public Builder approvalOpinion(boolean approvalOpinion) {
            this.approvalOpinion = approvalOpinion;
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

        public Builder multiInstanceNode(boolean multiInstanceNode) {
            this.multiInstanceNode = multiInstanceNode;
            return this;
        }

        public Builder addMultiInstance(boolean addMultiInstance) {
            this.addMultiInstance = addMultiInstance;
            return this;
        }

        public Builder taskReviewerScope(TaskReviewerScopeEnum taskReviewerScope) {
            this.taskReviewerScope = taskReviewerScope;
            return this;
        }

        public Builder taskReviewerValue(List<Map<String, String>> taskReviewerValue) {
            this.taskReviewerValue = taskReviewerValue;
            return this;
        }

        public Builder taskButtons(List<String> taskButtons) {
            this.taskButtons = taskButtons;
            return this;
        }

        public Builder callBackNodes(List<String> callBackNodes) {
            this.callBackNodes = callBackNodes;
            return this;
        }

        public UserTaskExtensionDTO build() {
            UserTaskExtensionDTO userTaskExtensionDTO = new UserTaskExtensionDTO();
            userTaskExtensionDTO.setUserTaskId(userTaskId);
            userTaskExtensionDTO.setUserTaskName(userTaskName);
            userTaskExtensionDTO.setUserTaskFormKey(userTaskFormKey);
            userTaskExtensionDTO.setUserTaskDocumentation(userTaskDocumentation);
            userTaskExtensionDTO.setApprovalOpinion(approvalOpinion);
            userTaskExtensionDTO.setAgreeButtonName(agreeButtonName);
            userTaskExtensionDTO.setRefuseButtonName(refuseButtonName);
            userTaskExtensionDTO.setMultiInstanceNode(multiInstanceNode);
            userTaskExtensionDTO.setAddMultiInstance(addMultiInstance);
            userTaskExtensionDTO.setTaskReviewerScope(taskReviewerScope);
            userTaskExtensionDTO.setTaskReviewerValue(taskReviewerValue);
            userTaskExtensionDTO.setTaskButtons(taskButtons);
            userTaskExtensionDTO.setCallBackNodes(callBackNodes);
            return userTaskExtensionDTO;
        }
    }
}
