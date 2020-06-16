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
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
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
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.cmmn.engine.impl.process.ProcessInstanceService;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ActivityInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.Param;
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
	ISysUserService sysUserService;
	@Autowired
	ISysRoleService sysRoleService;

	@Autowired
	RuntimeService runtimeService;

	@GetMapping("/form")
	public String form(HttpServletRequest request, ModelMap modelMap) {
		String taskId = request.getParameter("taskId");
		String procInsId = request.getParameter("procInsId");
		String procDefId = request.getParameter("procDefId");
		String procDefKey = request.getParameter("procDefKey");
		String procDefversionStr = request.getParameter("procDefversion");
		String status = request.getParameter("status");
		Integer procDefversion = null;
		if (StringUtils.isNotBlank(procDefversionStr)) {
			procDefversion = Integer.valueOf(procDefversionStr);
		}
		FlowTaskVO flowTaskVO = FlowTaskVO.builder().taskId(taskId).procInsId(procInsId).procDefId(procDefId).procDefKey(procDefKey).procDefversion(procDefversion).build();
		if (StringUtils.isNotBlank(status)) {
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
			List<Map<String, Object>> resList = new ArrayList<>();
			Map<String, Object> resMap = new HashMap<>();

			if (FlowConstant.TASK_TYPE_BACK.equals(flowTaskVO.getTaskType())) {
				// 获取退回节点
				List<ActivityInstance> activityInstanceList = runtimeService.createActivityInstanceQuery().processInstanceId(flowTaskVO.getProcInsId()).finished().orderByActivityInstanceStartTime().desc().list();
				if (activityInstanceList.size() > 0) {
					ActivityInstance prevActivityInstance = activityInstanceList.get(0);
					if ("userTask".equals(prevActivityInstance.getActivityType()) && StringUtils.isNotBlank(prevActivityInstance.getDeleteReason())) {
						resMap.put("nodeType", "UserTaskBack");
						resMap.put("id", prevActivityInstance.getActivityId());
						resMap.put("name", prevActivityInstance.getActivityName());
						resMap.put("assignee", prevActivityInstance.getAssignee());
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
				resMap.put("assignee", userTask.getAssignee());
				resMap.put("candidateGroups", userTask.getCandidateGroups());
				resMap.put("candidateUsers", userTask.getCandidateUsers());
				// TODO 需要拿到下一步处理人列表
				// UserTaskExtensionDTO userTaskExtension = FlowUtils.getUserTaskExtension(userTask);
				// TaskReviewerScopeEnum taskReviewerScope = userTaskExtension.getTaskReviewerScope();
			} else {
				resMap.put("nodeType", nextNodePair.getLeft());
			}
			resList.add(resMap);

			if (resMap.size() > 0) {
				return AjaxResult.success("操作成功", resList);
			}
			return AjaxResult.error("下一步不是用户节点");
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
	 * 提交到退回节点
	 *
	 * @param backToStepVO
	 * @return
	 */
	@PostMapping("/submitToBackStep")
	@ResponseBody
	public AjaxResult submitToBackStep(@RequestBody FlowParamVO backToStepVO) {
		Map formData = backToStepVO.getForm();
		FlowTaskVO flowTaskVO = backToStepVO.getFlow();
		if (formData != null && flowTaskVO != null) {
			String message = generalFlowBiz.submitToBackStep(formData, flowTaskVO, ShiroUtils.getSysUser());
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
		Object formData = generalFlowBiz.loadFormData(flowTaskVO, sessionUserAccount);
		if (formData == null) {
			return AjaxResult.error("数据不存在，可能是新增");
		}
		return AjaxResult.success(formData);
	}
}
