/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.controller;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.enums.MultiInstanceLoopCharacteristicsType;
import com.ruoyi.process.core.plugin.flowable.enums.TaskFormStatusEnum;
import com.ruoyi.process.core.plugin.flowable.enums.TaskStatusEnum;
import com.ruoyi.process.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.ruoyi.process.core.plugin.flowable.service.FlowTaskService;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import com.ruoyi.process.core.plugin.flowable.vo.FlowTaskVO;
import com.ruoyi.process.modules.flow.biz.GeneralFlowBiz;
import com.ruoyi.process.modules.flow.domain.FlowData;
import com.ruoyi.process.modules.flow.domain.FlowInstExtend;
import com.ruoyi.process.modules.flow.service.FlowCustomQueryService;
import com.ruoyi.process.modules.flow.service.FlowDataService;
import com.ruoyi.process.modules.flow.service.FlowInstExtendService;
import com.ruoyi.process.modules.flow.service.impl.FlowCustomQueryServiceImpl;
import com.ruoyi.process.modules.flow.vo.FlowParamVO;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 * 通用流程
 */
@Controller
//@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
@RequestMapping("/general/flow/process")
public class GeneralProcessController extends BaseProcessController {

    @Autowired
    GeneralFlowBiz generalFlowBiz;
    @Autowired
    FlowProcessDefinitionService flowProcessDefinitionService;
    @Autowired
    FlowCustomQueryService flowCustomQueryService;
    @Autowired
    FlowTaskService flowTaskService;
    @Autowired
    TaskService taskService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    HistoryService historyService;
    @Autowired
    ISysUserService sysUserService;
    @Autowired
    ISysDeptService sysDeptService;
    @Autowired
    ISysRoleService sysRoleService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    FlowDataService flowDataService;

    @Autowired
    FlowInstExtendService flowInstExtendService;

    @GetMapping("/form")
    public String form(HttpServletRequest request, ModelMap modelMap) {
        String taskId = request.getParameter("taskId");
        String procInsId = request.getParameter("procInsId");
        String procDefId = request.getParameter("procDefId");
        String procDefKey = request.getParameter("procDefKey");
        String procDefversionStr = request.getParameter("procDefversion");
        Integer procDefversion = null;
        if (StringUtils.isNotBlank(procDefversionStr)) {
            procDefversion = Integer.valueOf(procDefversionStr);
        }
        FlowTaskVO flowTaskVO = FlowTaskVO.builder().taskId(taskId).procInsId(procInsId).procDefId(procDefId).procDefKey(procDefKey).procDefversion(procDefversion).build();
//        if (StringUtils.isNotBlank(status)) {
//            flowTaskVO.setStatus(TaskStatusEnum.valueOf(status));
//        }

        SysUser currUser = getCurrUser();

        if (Strings.isNotBlank(taskId)) {
            Task task = flowTaskService.getTask(taskId);
            if (null != task) {
                FlowUtils.setFlowTaskVo(flowTaskVO, task, String.valueOf(currUser.getUserId()));
                FlowInstExtend flowInstExtend = flowInstExtendService.findByProcInsId(procInsId);
                flowTaskVO.setTaskType(flowInstExtend.getCurrTaskType());
            } else {
                HistoricTaskInstance historyTask = flowTaskService.getHistoryTask(taskId);
                FlowUtils.setFlowTaskVo(flowTaskVO, historyTask, String.valueOf(currUser.getUserId()));
            }
            if (flowTaskVO.getProcInsId() != null) {
                // 设置业务表ID
                flowTaskVO.setBusinessId(flowProcessDefinitionService.getBusinessKeyId(flowTaskVO.getProcInsId()));
            }
        } else {
            throw new RuntimeException("任务ID不能为空！");
        }
        NutMap nutMap = new NutMap();
        String formPage = generalFlowBiz.getFormPage(flowTaskVO);
        if (Strings.isBlank(formPage)) {
            throw new RuntimeException("表单不能为空");
        }
        nutMap.put("formPage", formPage);
        nutMap.put("flow", flowTaskVO);
        nutMap.put("title", generalFlowBiz.getFlowName(flowTaskVO));
        nutMap.put("status", TaskFormStatusEnum.EDIT);
        if (flowTaskVO.isFinishTask()) {
            nutMap.put("status", TaskFormStatusEnum.VIEW);
        } else {
            nutMap.put("status", TaskFormStatusEnum.AUDIT);
        }

        modelMap.putAll(nutMap);
        return "view/modules/flow/general/flowAudit.html";
    }

    /**
     * 取得流程下一节点
     *
     * @param flowParamVO
     * @return
     */
    @PostMapping("/getNextNode")
    @ResponseBody
    public AjaxResult getNextNode(@RequestBody FlowParamVO flowParamVO) {
        try {
            Map<String, Object> formData = flowParamVO.getForm();
            FlowTaskVO flowTaskVO = flowParamVO.getFlow();
            List<Map<String, Object>> resList = new ArrayList<>();
            Map<String, Object> resMap = new HashMap<>();

            if (FlowConstant.TASK_TYPE_BACK.equals(flowTaskVO.getTaskType())) {
                // 获取上一步退回节点
                List<ActivityInstance> activityInstanceList = runtimeService.createActivityInstanceQuery().processInstanceId(flowTaskVO.getProcInsId()).finished().orderByActivityInstanceStartTime().desc().list();
                if (activityInstanceList.size() > 0) {
                    ActivityInstance prevActivityInstance = activityInstanceList.get(0);
                    if ("userTask".equals(prevActivityInstance.getActivityType()) && StringUtils.isNotBlank(prevActivityInstance.getDeleteReason())) {
                        resMap.put("nodeType", "UserTaskBack");
                        resMap.put("id", prevActivityInstance.getActivityId());
                        resMap.put("name", prevActivityInstance.getActivityName());
                        resMap.put("assignee", prevActivityInstance.getAssignee());
                        SysUser sysUser = sysUserService.selectUserById(Long.valueOf(prevActivityInstance.getAssignee()));
                        SysDept sysDept = sysDeptService.selectDeptById(sysUser.getDeptId());
                        resMap.put("assignees", ImmutableList.of(ImmutableMap.of("user_id", sysUser.getUserId(), "user_name", sysUser.getUserName(), "dept_name", sysDept.getDeptName())));
                        resList.add(resMap);
                    }
                }
            }

            Pair<String, FlowElement> nextNodePair = flowTaskService.previewNextNode(formData, flowTaskVO, getCurrUser());
            resMap = new HashMap<>();
            if (null == nextNodePair) {
                resMap.put("nodeType", "Other");
            } else if (StencilConstants.STENCIL_TASK_USER.equals(nextNodePair.getLeft())) {
                UserTask userTask = (UserTask) nextNodePair.getRight();
                resMap.put("nodeType", StencilConstants.STENCIL_TASK_USER);
                resMap.put("id", userTask.getId());
                resMap.put("name", userTask.getName());
//                resMap.put("assignee", userTask.getAssignee());
//                resMap.put("candidateGroups", userTask.getCandidateGroups());
//                resMap.put("candidateUsers", userTask.getCandidateUsers());
                // 需要拿到下一步处理人列表
                UserTaskExtensionDTO userTaskExtension = FlowUtils.getUserTaskExtension(userTask);
                FlowSubmitInfoDTO flowSubmitInfo = flowCustomQueryService.getFlowSubmitInfo(flowTaskVO.getTaskId());
                List<Map<String, Object>> assigneeList = generalFlowBiz.listUserTaskNodeAllReviewerUser(userTaskExtension, flowSubmitInfo);
                resMap.put("isMultiInstance", userTaskExtension.isMultiInstanceNode());
                resMap.put("assignees", assigneeList);
            } else {
                resMap.put("nodeType", nextNodePair.getLeft());
            }
            resList.add(resMap);

            if (resList.size() > 0) {
                return AjaxResult.success("操作成功", resList);
            }
            return AjaxResult.error("下一步不是用户节点");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("事务无法打开！");
        }
    }

    /**
     * 取得流程下一节点
     *
     * @param params
     * @return
     */
    @PostMapping("/getBackNode")
    @ResponseBody
    public AjaxResult getBackNode(@RequestBody Map<String, String> params) {
        try {
            String procInsId = params.get("procInsId");
            String callBackNodes = params.get("callBackNodes");
            String[] callBackNodeArr = callBackNodes.split(",");
            // 流程配置所配置的可退回的节点
            ArrayList<String> backNodeDefKeyList = Lists.newArrayList(callBackNodeArr);

            List<Map<String, Object>> resList = Lists.newArrayList();
            Map<String, Object> dataMap;
            List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInsId).orderByHistoricTaskInstanceStartTime().desc().list();
            int size = hisTaskList.size();
            // 判断是否已添加
            List<String> addedCallBackNodeDefKeyList = Lists.newLinkedList();
            for (int i = 0; i < size; i++) {
                HistoricTaskInstance historicTaskInstance = hisTaskList.get(i);
                String hisTaskDefKey = historicTaskInstance.getTaskDefinitionKey();
                if (backNodeDefKeyList.contains(hisTaskDefKey) && !addedCallBackNodeDefKeyList.contains(hisTaskDefKey)) {
                    addedCallBackNodeDefKeyList.add(hisTaskDefKey);
                    dataMap = new HashMap<>();
                    dataMap.put("taskDefKey", hisTaskDefKey);
                    dataMap.put("taskName", historicTaskInstance.getName());
                    dataMap.put("assignees", flowCustomQueryService.listUserTaskNodeAllReviewerUser(ImmutableList.of(Long.valueOf(historicTaskInstance.getAssignee()))));
                    resList.add(dataMap);
                }
            }

            if (resList.size() > 0) {
                return AjaxResult.success("操作成功", resList);
            }
            return AjaxResult.error("获取驳回节点信息失败!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("事务无法打开！");
        }
    }

    /**
     * 展示下一步流程审核人选择
     *
     * @param flowTaskVO
     * @return
     */
    @Deprecated
    @PostMapping("/choiceNextReviewerUser")
    @ResponseBody
    public TableDataInfo choiceNextReviewerUser(@RequestBody FlowTaskVO flowTaskVO, @RequestParam("nextNodeId") String nextNodeId) {
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcDefId());
        if (Strings.isNotBlank(nextNodeId)) {
            dto = flowProcessDefinitionService.getUserTaskExtension(nextNodeId, flowTaskVO.getProcDefId());
            FlowSubmitInfoDTO submitInfoDTO = flowCustomQueryService.getFlowSubmitInfo(flowTaskVO.getTaskId());
            return getDataTable(generalFlowBiz.listUserTaskNodeAllReviewerUser(dto, submitInfoDTO));
        }
        return getDataTable(Collections.emptyList());
    }

    /**
     * 流程回退
     *
     * @param backToStepVO
     */
    @PostMapping("/backToStep")
    @ResponseBody
    public AjaxResult backToStep(@RequestBody FlowParamVO backToStepVO) {
        Map formData = backToStepVO.getForm();
        FlowTaskVO flowTaskVO = backToStepVO.getFlow();
        if (formData != null && flowTaskVO != null) {
            flowTaskService.saveCurrTaskData(flowTaskVO.getProcInsId(), formData);
            String message = generalFlowBiz.backToStep(formData, flowTaskVO, ShiroUtils.getSysUser());
            if (Strings.isNotBlank(message)) {
                return AjaxResult.error(message);
            }
            return AjaxResult.success("回退成功");
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 提交到退回节点
     *
     * @param flowParamVO
     * @return
     */
    @PostMapping("/submitToBackStep")
    @ResponseBody
    public AjaxResult submitToBackStep(@RequestBody FlowParamVO flowParamVO) {
        Map formData = flowParamVO.getForm();
        FlowTaskVO flowTaskVO = flowParamVO.getFlow();
        if (formData != null && flowTaskVO != null) {
            flowTaskService.saveCurrTaskData(flowTaskVO.getProcInsId(), formData);
            String message = generalFlowBiz.submitToBackStep(formData, flowTaskVO, ShiroUtils.getSysUser());
            if (Strings.isNotBlank(message)) {
                return AjaxResult.error(message);
            }
            return AjaxResult.success("已提交到下一步。");
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 加签
     *
     * @param flowParamVO
     */
    @PostMapping("/addMultiInstance")
    @ResponseBody
    public AjaxResult addMultiInstance(@RequestBody FlowParamVO flowParamVO) {
        SysUser sessionUserAccount = getCurrUser();
        Map<String, Object> formData = flowParamVO.getForm();
        FlowTaskVO flowTaskVO = flowParamVO.getFlow();
        if (formData != null && flowTaskVO != null) {
            String message = generalFlowBiz.addMultiInstance(formData, flowTaskVO, sessionUserAccount);
            if (Strings.isNotBlank(message)) {
                return AjaxResult.error(message);
            }
            return AjaxResult.success("加签成功");
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 启动流程
     *
     * @param flowParamVO
     * @return
     */
    @RequestMapping("/startFlow")
    @ResponseBody
    public AjaxResult startFlow(@RequestBody FlowParamVO flowParamVO) {
        Map<String, Object> formData = flowParamVO.getForm();
        FlowTaskVO flowTaskVO = flowParamVO.getFlow();
        SysUser currUser = getCurrUser();
        if (formData != null && flowTaskVO != null) {
            if (null == currUser.getDeptId()) {
                return AjaxResult.error("流程发起人不存在任何部门中！");
            } else {
                Set<String> roleKeySet = sysRoleService.selectRoleKeys(currUser.getUserId());
                ProcessInstance processInstance = generalFlowBiz.start(formData, flowTaskVO, currUser, roleKeySet);
                Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

                Map<String, Object> resMap = new HashMap<>();
                resMap.put("taskId", task.getId());
                resMap.put("taskName", task.getName());
                resMap.put("taskDefKey", task.getTaskDefinitionKey());
                resMap.put("procInsId", processInstance.getId());
                resMap.put("procDefId", processInstance.getProcessDefinitionId());
                resMap.put("procDefKey", processInstance.getProcessDefinitionKey());
                resMap.put("procDefversion", processInstance.getProcessDefinitionVersion());
                return AjaxResult.success("发起流程成功", resMap);
            }
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    @RequestMapping("/saveData")
    @ResponseBody
    public AjaxResult saveFlowData(@RequestBody FlowParamVO flowParamVO) {
//		Task task = taskService.createTaskQuery().taskId(flowTaskVO.getTaskId()).singleResult();
//		runtimeService.setVariables(flowTaskVO.getExecutionId(), formData);
        flowTaskService.saveCurrTaskData(flowParamVO.getFlow().getProcInsId(), flowParamVO.getForm());
        return success();
    }

    /**
     * 工单执行（完成任务）
     *
     * @param flowParamVO
     * @return
     */
    @RequestMapping("/saveAudit")
    @ResponseBody
    public AjaxResult saveAudit(@RequestBody FlowParamVO flowParamVO) {
        Map<String, Object> formData = flowParamVO.getForm();
        FlowTaskVO flowTaskVO = flowParamVO.getFlow();
        SysUser currUser = getCurrUser();
        if (formData != null && flowTaskVO != null) {
            if (flowTaskVO.getTurnDown() == true && Strings.isBlank(flowTaskVO.getComment())) {
                return AjaxResult.error("驳回意见不能为空！");
            }
            if (Strings.isNotBlank(flowTaskVO.getBusinessId())) {
                flowTaskService.saveCurrTaskData(flowTaskVO.getProcInsId(), formData);
                String message = generalFlowBiz.userAudit(formData, flowTaskVO, currUser);
                if (message != null) {
                    return AjaxResult.error(message);
                }
            }
            return AjaxResult.success("操作成功！");
        } else {
            return AjaxResult.error("参数异常");
        }
    }


    /**
     * 加载表单信息
     *
     * @param flowTaskVO
     */
    @PostMapping("/loadFormData")
//    @Ok("json:{dateFormat:'yyyy-MM-dd HH:mm',nullListAsEmpty:true}")
    @ResponseBody
    public AjaxResult loadFormData(@RequestBody FlowTaskVO flowTaskVO) {
        SysUser sessionUserAccount = getCurrUser();
        try {
            String taskId = flowTaskVO.getTaskId();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            Map<String, String> dataMap = null;
            if (null != task) {
                dataMap = generalFlowBiz.loadProcessData(flowTaskVO.getProcInsId());
            } else {
                dataMap = generalFlowBiz.loadTaskData(flowTaskVO.getTaskId());
            }
//			Object formData = generalFlowBiz.loadFormData(flowTaskVO, sessionUserAccount);
            if (dataMap == null) {
                return AjaxResult.error("数据不存在，可能是新增");
            }
            return AjaxResult.success(dataMap);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
