/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.biz.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.dto.CandidateGroupsDTO;
import com.ruoyi.process.core.plugin.flowable.dto.CandidateUsersDTO;
import com.ruoyi.process.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.enums.TaskReviewerScopeEnum;
import com.ruoyi.process.core.plugin.flowable.service.FlowCacheService;
import com.ruoyi.process.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.ruoyi.process.core.plugin.flowable.service.FlowTaskService;
import com.ruoyi.process.core.plugin.flowable.util.FlowDiagramUtils;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import com.ruoyi.process.core.plugin.flowable.vo.FlowTaskVO;
import com.ruoyi.process.modules.flow.biz.GeneralFlowBiz;
import com.ruoyi.process.modules.flow.domain.FlowAttachment;
import com.ruoyi.process.modules.flow.domain.FlowAttachmentConfig;
import com.ruoyi.process.modules.flow.domain.FlowConfigExtend;
import com.ruoyi.process.modules.flow.domain.FlowInstExtend;
import com.ruoyi.process.modules.flow.executor.ExternalFormExecutor;
import com.ruoyi.process.modules.flow.mapper.FlowAttachmentConfigMapper;
import com.ruoyi.process.modules.flow.mapper.FlowAttachmentMapper;
import com.ruoyi.process.modules.flow.service.*;
import com.ruoyi.process.utils.DateUtil;
import com.ruoyi.process.utils.JsContex;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserRoleService;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 */
@Service
public class GeneralFlowBizImpl implements GeneralFlowBiz {

	private static final Logger log = LogManager.getLogger(FlowDiagramUtils.class);

	@Autowired
	FlowTaskService flowTaskService;
	@Autowired
	FlowCacheService flowCacheService;
	@Autowired
	FlowProcessDefinitionService flowProcessDefinitionService;
	//    @Autowired
//    DepartmentLeaderService departmentLeaderService;
	@Autowired
	ISysUserService sysUserService;
	@Autowired
	ISysRoleService sysRoleService;
	@Autowired
	ISysUserRoleService sysUserRoleService;
	@Autowired
	FlowCustomQueryService flowCustomQueryService;
	@Autowired
	TaskService taskService;
	@Autowired
	FlowAttachmentConfigMapper flowAttachmentConfigMapper;
	@Autowired
	FlowAttachmentMapper flowAttachmentMapper;
	@Autowired
	RuntimeService runtimeService;
	@Autowired
	HistoryService historyService;
	@Autowired
	FlowConfigExtendService flowConfigExtendService;
	@Autowired
	FlowInstExtendService flowInstExtendService;

	@Override
	public String getFormPage(FlowTaskVO flowTaskVO) {
		ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
		return executor.getFormPage(flowTaskVO);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ProcessInstance start(Map formData, FlowTaskVO flowTaskVO, SysUser currUser, Set<String> roleKeys) {
		// 设置当前流程任务办理人
		Authentication.setAuthenticatedUserId(String.valueOf(currUser.getUserId()));
		ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(FlowConstant.AUDIT_PASS, flowTaskVO.isPass());
		flowTaskService.setValuedDataObject(variables, flowTaskVO.getProcDefId(), formData, currUser, false);
		flowTaskVO.setComment("[发起任务]");
		formData = executor.start(formData, flowTaskVO, currUser);
		String primaryKeyId = formData.getOrDefault(FlowConstant.PRIMARY_KEY, "").toString();
		if (Strings.isBlank(primaryKeyId)) {
			throw new RuntimeException("业务ID不能为空");
		}
		ProcessInstance processInstance = flowTaskService.startProcess(flowTaskVO.getProcDefKey(), primaryKeyId, variables, currUser.getUserId().toString(), currUser.getDeptId(), roleKeys);
		// 创建流程附件列表
		List<FlowAttachmentConfig> flowAttachmentConfigList = flowAttachmentConfigMapper.findByParams(ImmutableMap.of("procDefKey", flowTaskVO.getProcDefKey()));
		if (flowAttachmentConfigList != null && flowAttachmentConfigList.size() > 0) {
			List<FlowAttachment> flowAttachmentList = flowAttachmentConfigList.stream().map(flowAttachmentConfig -> {
				FlowAttachment fa = new FlowAttachment();
				fa.setProcInsId(processInstance.getId());
				fa.setProcDefKey(flowAttachmentConfig.getProcDefKey());
				fa.setAttachType(flowAttachmentConfig.getAttachType());
				fa.setCreateBy(currUser.getLoginName());
				fa.setCreateTime(new Date());
				return fa;
			}).collect(Collectors.toList());
			flowAttachmentMapper.batchSave(flowAttachmentList);
		}

		// 保存流程扩展表信息
		List<Task> tasklist = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
		Task task = tasklist.get(0);
		flowInstExtendService.createFlowInstExtend(processInstance, task, formData);

		// 保存流程表单数据
		flowTaskService.saveCurrTaskData(processInstance.getId(), formData);

		return processInstance;
	}

	@Override
	public String backToStep(Map formData, FlowTaskVO flowTaskVO, SysUser sessionUserAccount) {
		// 设置当前流程任务办理人
		Authentication.setAuthenticatedUserId(String.valueOf(sessionUserAccount.getUserId()));
		ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
		flowTaskVO.setComment("[回退] " + flowTaskVO.getComment());
		String errorMsg = executor.backToStep(formData, flowTaskVO, sessionUserAccount);
		if (errorMsg != null) {
			return errorMsg;
		}
		return flowTaskService.backToStep(flowTaskVO, String.valueOf(sessionUserAccount.getUserId()));
	}

	@Override
	public String submitToBackStep(Map formData, FlowTaskVO flowTaskVO, SysUser sessionUserAccount) {
		// 设置当前流程任务办理人
		Authentication.setAuthenticatedUserId(String.valueOf(sessionUserAccount.getUserId()));
		ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
		flowTaskVO.setComment("[退回重提] " + flowTaskVO.getComment());
		String errorMsg = executor.backToStep(formData, flowTaskVO, sessionUserAccount);
		if (errorMsg != null) {
			return errorMsg;
		}
		return flowTaskService.submitToBackStep(flowTaskVO, String.valueOf(sessionUserAccount.getUserId()));
	}

	@Override
	public String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, SysUser sessionUserAccount) {
		// 设置当前流程任务办理人
		Authentication.setAuthenticatedUserId(sessionUserAccount.getUserName());
		ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
		flowTaskVO.setComment("[加签] " + flowTaskVO.getComment());
		String errorMsg = executor.addMultiInstance(formData, flowTaskVO, sessionUserAccount);
		if (errorMsg != null) {
			return errorMsg;
		}
		return flowTaskService.addMultiInstance(flowTaskVO, flowTaskVO.getComment());
	}

	@Override
	@Transactional
	public String userAudit(Map formData, FlowTaskVO flowTaskVO, SysUser sessionUserAccount) {
		// 设置当前流程任务办理人
		Authentication.setAuthenticatedUserId(String.valueOf(sessionUserAccount.getUserId()));
		FlowUtils.setFlowTaskVo(flowTaskVO, flowTaskService.getTaskOrHistoryTask(flowTaskVO.getTaskId()), sessionUserAccount.getUserName());
		ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
		UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcDefId());
		if (Strings.isNotBlank(flowTaskVO.getComment())) {
			String prefix = flowTaskVO.isPass() ? "[通过] " : "[拒绝] ";
			if (flowTaskVO.getReaffirm()) {
				prefix = flowTaskVO.isPass() ? "[重申] " : "[销毁] ";
			}
			if (dto.isConnectionCallBack() && flowTaskVO.getTurnDown() == true) {
				prefix = "[驳回] ";
			}
			flowTaskVO.setComment(prefix + flowTaskVO.getComment());
		} else {
			flowTaskVO.setComment(flowTaskVO.getBusinessComment());
		}
		Map<String, Object> vars = Maps.newHashMap();
		vars.put(FlowConstant.AUDIT_PASS, flowTaskVO.isPass());
		vars.put(FlowConstant.FORM_DATA, formData);
		flowTaskService.setValuedDataObject(vars, flowTaskVO.getProcDefId(), formData, sessionUserAccount, true);
		if (dto.isConnectionCallBack()) {
			vars.put(FlowConstant.TURN_DOWN, flowTaskVO.getTurnDown());
		}
		if (dto.isDynamicFreeChoiceNextReviewerMode() && flowTaskVO.getDelegateStatus() == null) {
			boolean needCheckFlowNextReviewerAssignee = false;
			try {
				//此方法前不要操作数据库，该方法会回滚数据库的
				Pair<String, FlowElement> nextNodePair = flowTaskService.getNextNode(formData, flowTaskVO, sessionUserAccount);
				if (nextNodePair != null && StencilConstants.STENCIL_TASK_USER.equals(nextNodePair.getLeft())) {
					//下一节点存在，需要选择审核人
					needCheckFlowNextReviewerAssignee = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("事务无法打开！");
			}
			if (needCheckFlowNextReviewerAssignee && Strings.isBlank(flowTaskVO.getFlowNextReviewerAssignee())) {
				return "请选择下一步流程审核人！";
			}
			if (Strings.isNotBlank(flowTaskVO.getFlowNextReviewerAssignee())) {
				vars.put(FlowConstant.NEXT_REVIEWER, flowTaskVO.getFlowNextReviewerAssignee());
			}
		}
		formData = evalJavaScriptByModifyForm(formData, flowTaskVO, dto);

		String errorMsg = executor.userAudit(formData, flowTaskVO, sessionUserAccount);
		if (errorMsg != null) {
			return errorMsg;
		}

		// 流程结束
		String nextTaskType = flowTaskVO.getNextTaskType();
		if(FlowConstant.TASK_TYPE_END.equals(nextTaskType)) {
			executor.end(formData, flowTaskVO, sessionUserAccount);
		}
		//变量别修改过了，所以从新设置下
		vars.put(FlowConstant.FORM_DATA, formData);
		flowTaskService.complete(flowTaskVO, vars);

		String procInsId = flowTaskVO.getProcInsId();
		// 保存流程扩展表信息
		if (FlowConstant.TASK_TYPE_END.equals(nextTaskType)) {
			// 流程结束更新流程扩展表的信息
			flowInstExtendService.updateTaskFields(procInsId, null, null, FlowConstant.TASK_TYPE_END);
			// 流程结束后把流程实例的任务数据删掉
			flowTaskService.deleteProcessData(procInsId);
		} else if (FlowConstant.TASK_TYPE_NORMAL.equals(nextTaskType)) {
			// 流程提交后查询当前
			List<Task> tasklist = taskService.createTaskQuery().processInstanceId(procInsId).list();
			if(CollectionUtils.isEmpty(tasklist)) {
				throw new RuntimeException("未查询到下一任务节点信息！");
			}
			Task task = tasklist.get(0);
			flowInstExtendService.updateTaskFields(procInsId, task.getTaskDefinitionKey(), task.getName(), FlowConstant.TASK_TYPE_NORMAL);
		}
		return null;
	}

	@Override
	public Object loadFormData(FlowTaskVO flowTaskVO, SysUser sessionUserAccount) {
		 return getExternalFormExecutor(flowTaskVO.getProcDefId()).loadFormData(flowTaskVO, sessionUserAccount);
	}

	@Override
	public Map<String, String> loadProcessData(String procInsId) {
		return flowTaskService.findProcessData(procInsId);
	}

	@Override
	public Map<String, String> loadTaskData(String taskId) {
		return flowTaskService.findTaskData(taskId);
	}

	@Override
	public String getFlowName(FlowTaskVO flowTaskVO) {
		return flowCacheService.getProcessDefinitionCache(flowTaskVO.getProcDefId()).getName();
	}

	@Override
	public List<Map<String, Object>> listUserTaskNodeAllReviewerUser(UserTaskExtensionDTO taskExtensionDTO, FlowSubmitInfoDTO flowSubmitInfoDTO) {
		List<String> candidateUserNames = new ArrayList<>();
		String flowSubmitterUserName = flowSubmitInfoDTO.getUserName();
		String flowSubmitterDeptId = flowSubmitInfoDTO.getDeptId();
		switch (taskExtensionDTO.getTaskReviewerScope()) {
			case SINGLE_USER:
				candidateUserNames.add(taskExtensionDTO.getAssignee());
				break;
			case FLOW_SUBMITTER:
				candidateUserNames.add(flowSubmitterUserName);
				break;
			case MULTIPLE_USERS:
				candidateUserNames = taskExtensionDTO.getCandidateUsers().stream().map(CandidateUsersDTO::getUserId).collect(Collectors.toList());
				break;
			case USER_ROLE_GROUPS:
				List<String> roleKeys = taskExtensionDTO.getCandidateGroups().stream().map(CandidateGroupsDTO::getRoleKey).collect(Collectors.toList());
				List<SysUser> sysUserList = sysUserRoleService.selectUserListByRoleKeyList(roleKeys);
				candidateUserNames = sysUserList.stream().map(sysUser -> sysUser.getLoginName()).distinct().collect(Collectors.toList());
				break;
			case DEPARTMENT_HEAD:
			case DEPARTMENT_LEADER:
				// TODO
//                if (taskExtensionDTO.getTaskReviewerScope() == TaskReviewerScopeEnum.DEPARTMENT_HEAD) {
//                    //查询部门主管领导
//                    candidateUserNames = departmentLeaderService.queryUserNames(flowSubmitterDeptId, LeaderTypeEnum.HEAD);
//                    if (candidateUserNames.contains(flowSubmitterUserName)) {
//                        //如果自己就是部门主管领导则分配给再上级部门主管办理
//                        candidateUserNames = departmentLeaderService.queryIterationUserNames(flowSubmitterDeptId, LeaderTypeEnum.HEAD);
//                    }
//                } else if (taskExtensionDTO.getTaskReviewerScope() == TaskReviewerScopeEnum.DEPARTMENT_LEADER) {
//                    //查询部门分管领导
//                    candidateUserNames = departmentLeaderService.queryUserNames(flowSubmitterDeptId, LeaderTypeEnum.LEADER);
//                    if (candidateUserNames.contains(flowSubmitterUserName)) {
//                        //如果自己就是部门分管领导则分配给部门主管领导办理
//                        candidateUserNames = departmentLeaderService.queryUserNames(flowSubmitterDeptId, LeaderTypeEnum.HEAD);
//                    }
//                }
				break;
			default:
				break;
		}
		return flowCustomQueryService.listUserTaskNodeAllReviewerUser(candidateUserNames);
	}

	private ExternalFormExecutor getExternalFormExecutor(String procDefId) {
		return flowProcessDefinitionService.getExternalFormExecutor(procDefId);
	}

	/**
	 * 表单数据在审核后执行数据库更新前进行动态赋值
	 *
	 * @param formData
	 * @param flowTaskVO
	 * @param dto
	 * @return
	 */
	private Map evalJavaScriptByModifyForm(Map formData, FlowTaskVO flowTaskVO, UserTaskExtensionDTO dto) {
		if (Strings.isNotBlank(dto.getFormDataDynamicAssignment())) {
			StringBuffer jsCode = new StringBuffer("function modifyForm(formData,auditPass,flowTask){ " + dto.getFormDataDynamicAssignment() + "  return formData; }");
			try {
				JsContex.get().compile(jsCode.toString());
				JsContex.get().eval(jsCode.toString());
				Object result = JsContex.get().invokeFunction("modifyForm", formData, flowTaskVO.isPass(), flowTaskVO);
				formData = (Map) result;
			} catch (Exception e) {
				log.error("解析动态JS错误", e);
				throw new RuntimeException("解析动态JS错误");
			}
		}
		return formData;
	}
}
