/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.action;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.process.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.enums.TaskFormStatusEnum;
import com.ruoyi.process.core.plugin.flowable.enums.TaskReviewerScopeEnum;
import com.ruoyi.process.core.plugin.flowable.enums.TaskStatusEnum;
import com.ruoyi.process.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.ruoyi.process.core.plugin.flowable.service.FlowTaskService;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import com.ruoyi.process.core.plugin.flowable.vo.FlowTaskVO;
import com.ruoyi.process.modules.flow.biz.GeneralFlowBiz;
import com.ruoyi.process.modules.flow.service.FlowCustomQueryService;
import com.ruoyi.process.modules.flow.vo.FlowParamVO;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	RepositoryService repositoryService;
    @Autowired
    ISysUserService sysUserService;
    @Autowired
    ISysRoleService sysRoleService;

    @GetMapping("/form")
    public String form(HttpServletRequest request, ModelMap modelMap) {
        String taskId = request.getParameter("taskId");
        String procInsId = request.getParameter("procInsId");
        String procDefId = request.getParameter("procDefId");
        String procDefKey = request.getParameter("procDefKey");
        String procDefversionStr = request.getParameter("procDefversion");
        String status = request.getParameter("status");
        Integer procDefversion = null;
        if(StringUtils.isNotBlank(procDefversionStr)) {
            procDefversion = Integer.valueOf(procDefversionStr);
        }
        FlowTaskVO flowTaskVO = FlowTaskVO.builder().taskId(taskId).procInsId(procInsId).procDefId(procDefId).procDefKey(procDefKey).procDefversion(procDefversion).build();
        if(StringUtils.isNotBlank(status)) {
            flowTaskVO.setStatus(TaskStatusEnum.valueOf(status));
        }

        SysUser currUser = getCurrUser();

        if (Strings.isNotBlank(flowTaskVO.getTaskId())) {
            FlowUtils.setFlowTaskVo(flowTaskVO, flowTaskService.getTaskOrHistoryTask(flowTaskVO.getTaskId()), String.valueOf(currUser.getUserId()));
            if (flowTaskVO.getProcInsId() != null) {
                // 设置业务表ID
                flowTaskVO.setBusinessId(flowProcessDefinitionService.getBusinessKeyId(flowTaskVO.getProcInsId()));
            }
        }
        if (Strings.isBlank(flowTaskVO.getTaskId()) && Strings.isNotBlank(flowTaskVO.getProcDefKey())) {
            //没有任务ID，是采用最新版本新增流程
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(flowTaskVO.getProcDefKey()).latestVersion().singleResult();
            flowTaskVO.setProcDefId(processDefinition.getId());
            flowTaskVO.setProcDefversion(processDefinition.getVersion());
        }
        NutMap nutMap = new NutMap();
        String formPage = generalFlowBiz.getFormPage(flowTaskVO);
        if (Strings.isBlank(formPage)) {
            throw new RuntimeException("表单不能为空");
        }
        nutMap.put("formPage", formPage);
        nutMap.put("flow", flowTaskVO);
        nutMap.put("title", generalFlowBiz.getFlowName(flowTaskVO));
        nutMap.put("formData", generalFlowBiz.loadFormData(flowTaskVO, currUser));
        nutMap.put("status", TaskFormStatusEnum.EDIT);
        if (flowTaskVO.isFinishTask()) {
            nutMap.put("status", TaskFormStatusEnum.VIEW);
        } else if (Strings.isBlank(flowTaskVO.getTaskId())) {
            nutMap.put("status", TaskFormStatusEnum.EDIT);
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
            UserTask userTask = flowTaskService.previewNextNode(formData, flowTaskVO, getCurrUser());
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("id", userTask.getId());
            resMap.put("name", userTask.getName());
            resMap.put("assignee", userTask.getAssignee());
            resMap.put("candidateGroups", userTask.getCandidateGroups());
            resMap.put("candidateUsers", userTask.getCandidateUsers());
            // TODO 需要拿到下一步处理人列表
            // UserTaskExtensionDTO userTaskExtension = FlowUtils.getUserTaskExtension(userTask);
            // TaskReviewerScopeEnum taskReviewerScope = userTaskExtension.getTaskReviewerScope();

            if (userTask != null) {
                return AjaxResult.success("操作成功", resMap);
            }
            return AjaxResult.error("下一步不是用户节点");
        } catch (Exception e) {
            throw new RuntimeException("事务无法打开！");
        }
    }

    /**
     * 展示下一步流程审核人选择
     *
     * @param flowTaskVO
     * @return
     */
    @PostMapping("/choiceNextReviewerUser")
    @ResponseBody
    public TableDataInfo choiceNextReviewerUser(@RequestBody FlowTaskVO flowTaskVO, @RequestParam("nextNodeId") String nextNodeId) {
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcDefId());
        if (dto.isDynamicFreeChoiceNextReviewerMode()) {
            if (Strings.isNotBlank(nextNodeId)) {
                dto = flowProcessDefinitionService.getUserTaskExtension(nextNodeId, flowTaskVO.getProcDefId());
                FlowSubmitInfoDTO submitInfoDTO = flowCustomQueryService.getFlowSubmitInfo(flowTaskVO.getTaskId());
                return getDataTable(generalFlowBiz.listUserTaskNodeAllReviewerUser(dto, submitInfoDTO));
            }
        }
        return getDataTable(Collections.emptyList());
    }

    /**
     * 流程回退
     *
     * @param backToStepVO
     */
    @PostMapping("/backToStep")
//    @Aop(TransAop.READ_COMMITTED)
    @ResponseBody
    public AjaxResult backToStep(@RequestBody FlowParamVO backToStepVO) {
        Map formData = backToStepVO.getForm();
        FlowTaskVO flowTaskVO = backToStepVO.getFlow();
        if (formData != null && flowTaskVO != null) {
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
     * 加签
     *
     * @param flowParamVO
     */
    @PostMapping("/addMultiInstance")
//    @Aop(TransAop.READ_COMMITTED)
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
     * 启动流程--工单执行（完成任务）
     *
     * @param data
     * @return
     */
    @RequestMapping("/saveAudit")
//    @Aop(TransAop.READ_COMMITTED)
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
                String message = generalFlowBiz.userAudit(formData, flowTaskVO, currUser);
                if (message != null) {
                    return AjaxResult.error(message);
                }
            } else if (null == currUser.getDeptId()) {
                return AjaxResult.error("流程发起人不存在任何部门中！");
            } else {
//                String s = sysUserService.selectUserRoleGroup(currUser.getUserId());
                Set<String> roleKeySet = sysRoleService.selectRoleKeys(currUser.getUserId());
                String message = generalFlowBiz.start(formData, flowTaskVO, currUser, roleKeySet);
                return AjaxResult.success(message);
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
        Object formData = generalFlowBiz.loadFormData(flowTaskVO, sessionUserAccount);
        if (formData == null) {
            return AjaxResult.error("数据不存在，可能是新增");
        }
        return AjaxResult.success(formData);
    }
}
