/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.constant;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/22
 * 描述此类：
 */
public interface FlowConstant {
    /**
     * 流程提交者-可以在后面流程中驳回重新办理
     */
    String SUBMITTER = "FLOW_SUBMITTER";
    /**
     * 流程提交者的部门
     */
    String SUBMITTER_DEPT_ID = "FLOW_SUBMITTER_DEPT_ID";
    /**
     * 流程提交者当前拥有的角色
     */
    String SUBMITTER_ROLE_CODES = "FLOW_SUBMITTER_ROLE_CODES";
    /**
     * 自由选择的下一步审核人
     */
    String NEXT_REVIEWER = "FLOW_NEXT_REVIEWER";
    /**
     * 流程标题
     */
    String PROCESS_TITLE = "processTitle";
    /**
     * 流程审核状态-同意\拒绝
     * 变量
     */
    String AUDIT_PASS = "auditPass";
    /**
     * 流程审核状态-驳回
     * 变量
     */
    String TURN_DOWN = "turnDown";
    /**
     * 手写签字文件名
     */
    String HAND_WRITING_SIGNATURE_ATTACHMENT_NAME = "手写签字";
    /**
     * 表单数据
     */
    String FORM_DATA = "formData";
    /**
     * 未分类流程
     */
    String DEFAULT_CATEGORY = "default";
    /**
     * 业务表主键
     */
    String PRIMARY_KEY = "id";
    /**
     * 多实例中的拒绝计数器
     */
    String MULTIINSTANCE_REJECT_COUNTER = "multiinstance_reject_counter";
    /**
     * 多实例中的同意计数器
     */
    String MULTIINSTANCE_AGREE_COUNTER = "multiinstance_agree_counter";
    /**
     * 多实例审核人集合
     */
    String MULTIINSTANCE_ASSIGNEES_COLLECTION = "sys_multiinstance_assignees";
    /**
     * 多实例审核人变量
     */
    String MULTIINSTANCE_ASSIGNEES_VAR = "sys_multiinstance_assignees_var";
    /**
     * 添加多实例审核人变量
     */
    String ADD_MULTIINSTANCE_ASSIGNEES_VAR = "sys_add_multiinstance_assignees_var";

    /**
     * 添加退回审批意见
     */
    String BACK_COMMENT = "BackComment";

    /**
     * 添加退回重提审批意见
     */
    String BACK_SUBMIT_COMMENT = "BackSubmitComment";

    /**
     * 添加委托审批意见
     */
    String DELEGATE_COMMENT = "DelegateComment";

    /**
     * 添加转派审批意见
     */
    String TRANSFER_COMMENT = "TransferComment";

    /**
     * 添加上会审批意见
     */
    String MEETING_COMMENT = "MeetingComment";

    /**
     * 正常任务类型
     */
    String TASK_TYPE_NORMAL = "Normal";

    /**
     * 退回任务类型
     */
    String TASK_TYPE_BACK = "Back";

    /**
     * 退回重提任务类型
     */
    String TASK_TYPE_BACK_SUBMIT = "BackSubmit";

    /**
     * 委托任务类型
     */
    String TASK_TYPE_DELEGATE = "Delegate";

    /**
     * 转派任务类型
     */
    String TASK_TYPE_TRANSFER = "Transfer";
}
