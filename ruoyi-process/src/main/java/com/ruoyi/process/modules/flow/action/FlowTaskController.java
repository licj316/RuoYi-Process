/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.action;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.ruoyi.process.core.plugin.flowable.service.FlowTaskService;
import com.ruoyi.process.core.plugin.flowable.vo.FlowTaskVO;
import com.ruoyi.process.core.plugin.flowable.vo.ProcessDefinitionEntitVO;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/11
 * 流程个人任务相关
 */
@RequestMapping("/flowTask")
@Controller
public class FlowTaskController extends BaseProcessController{

    private String prefix = "view/modules/flow/task";

    @Autowired
    FlowTaskService flowTaskService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    FlowProcessDefinitionService flowProcessDefinitionService;

    @RequestMapping("/index")
    public void task() {
    }

    // 待办任务
    @GetMapping("/todo")
    public String todo() {
        return prefix + "/todo.html";
    }

    // 已发任务
    @GetMapping("/historic")
    public String historic() {
        return prefix + "/historic.html";
    }

    // 已发任务
    @GetMapping("/hasSent")
    public String hasSent() {
        return prefix + "/hasSent.html";
    }

    // 发起任务
    @GetMapping("/process")
    public String process() {
        return prefix + "/process.html";
    }

    @PostMapping("/todoData")
	@ResponseBody
    public TableDataInfo todoData(FlowTaskVO flowTaskVO, HttpServletRequest request) {
        SysUser currUser = getCurrUser();
        List<SysRole> userRoleList = getUserRoleList(currUser.getUserId());
        Set<String> roleIdSet = userRoleList.stream().map(sysRole -> sysRole.getRoleId().toString()).collect(Collectors.toSet());
        //联查变量的情况下，分页功能失效
        return getDataTable(flowTaskService.todoList(flowTaskVO, currUser.getUserId().toString(), roleIdSet, false));
    }

    @RequestMapping("/diagramViewer")
    public String diagramViewer(@RequestParam("definitionId") String definitionId, @RequestParam(value = "instanceId", required = false) String instanceId,
                                @RequestParam(value = "hisInsId", required = false) String hisInsId, HttpServletRequest request) {
        request.setAttribute("definitionId", Strings.sNull(definitionId));
        request.setAttribute("instanceId", Strings.sNull(instanceId));
        request.setAttribute("hisInsId", Strings.sNull(hisInsId));
        return "/view/modules/flow/task/flowDiagramViewer.html";
    }

    @RequestMapping("/historicData")
	@ResponseBody
    public TableDataInfo historicDataList(FlowTaskVO flowTaskVO, HttpServletRequest request, HttpServletResponse response) {
        List<FlowTaskVO> flowTaskVOList = flowTaskService.historicList(flowTaskVO, getCurrUser().getUserId().toString());
        return getDataTable(flowTaskVOList);
    }

    @RequestMapping("/hasSentData")
	@ResponseBody
    public TableDataInfo hasSentDataList(FlowTaskVO flowTaskVO, HttpServletRequest request, HttpServletResponse response) {
        List<FlowTaskVO> flowTaskVOList = flowTaskService.hasSentList(String.valueOf(getCurrUser().getUserId()));
        return getDataTable(flowTaskVOList);
    }

    /**
     * 获取流转历史列表
     *
     * @RequestParam procInsId 流程实例
     * @RequestParam startAct  开始活动节点名称
     * @RequestParam endAct    结束活动节点名称
     */
    @RequestMapping("/histoicFlow")
    public String histoicFlow(@RequestParam("procInsId") String procInsId, @RequestParam("startAct") String startAct, @RequestParam("endAct") String endAct, ModelMap modelMap) {
        if (Strings.isNotBlank(procInsId)) {
            modelMap.put("histoicFlowList", flowTaskService.histoicFlowList(procInsId, startAct, endAct));
        }
        return "/view/modules/flow/task/flowTaskHistoricFlow.html";
    }

    /**
     * 获取流程列表
     *
     * @RequestParam category 流程分类
     */
    @RequestMapping("/processDataList")
    @ResponseBody
    public TableDataInfo processDataList(String category, HttpServletRequest request) {
        List<ProcessDefinitionEntitVO> processDefinitionEntitVOList = flowProcessDefinitionService.processList(category);
        return getDataTable(processDefinitionEntitVOList);
    }


    /**
     * 获取当前流程节点扩展属性信息
     *
     * @RequestParam taskDefKey 当前节点key
     * @RequestParam procDefId  流程定义ID
     */
    @RequestMapping("/getUserTaskExtension")
	@ResponseBody
    public UserTaskExtensionDTO getUserTaskExtension(@RequestParam("taskDefKey") String taskDefKey, @RequestParam("procDefId") String procDefId) {
        //节点流程信息
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(taskDefKey, procDefId);
        if (dto == null) {
            return UserTaskExtensionDTO.NEW();
        }
        return dto;
    }


    /**
     * 签收任务
     *
     * @RequestParam taskId 任务ID
     */
    @PostMapping("/claim")
//    @TryCatchMsg("该任务已被其他人,签收失败!")
	@ResponseBody
    public AjaxResult claim(String taskId) {
        try {
            flowTaskService.claim(taskId, getCurrUser().getUserId().toString());
            return AjaxResult.success("签收成功");
        } catch (Exception e) {
        	throw new RuntimeException("该任务已被其他人,签收失败!");
        }
    }

    /**
     * 委托任务
     *
     * @RequestParam taskId 任务ID
     */
    @PostMapping("/delegate")
    @ResponseBody
//    @TryCatchMsg("委托失败!")
    public AjaxResult delegate(String taskId, String userName) {
        if (Strings.isBlank(userName) || Strings.isBlank(taskId)) {
            return AjaxResult.error("参数异常");
        }
        flowTaskService.delegateTask(taskId, userName);
        return AjaxResult.success("委托成功");
    }

    /**
     * 取消签收任务
     *
     * @RequestParam taskId 任务ID
     */
    @PostMapping("/unclaim")
	@ResponseBody
//    @TryCatchMsg("取消签收失败！")
    public AjaxResult unclaim(String taskId) {
        flowTaskService.unclaim(taskId, getCurrUser().getUserId().toString());
        return AjaxResult.success("取消签收成功");
    }


    /**
     * 转派任务
     *
     * @RequestParam taskId   任务ID
     * @RequestParam userName 接收人
     * @RequestParam reason   原因
     */
    @PostMapping("/transfer")
	@ResponseBody
//    @TryCatchMsg("转派失败!")
    public AjaxResult transferTask(String taskId, String userName, String reason) {
        if (Strings.isBlank(reason)) {
            return AjaxResult.error("请输入转派原因");
        }
        if (Strings.isBlank(userName) || Strings.isBlank(taskId)) {
            return AjaxResult.error("参数异常");
        }
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(getCurrUser().getUserId().toString());
        flowTaskService.transferTask(taskId, userName, reason);
        return AjaxResult.success("委托成功");
    }


    /**
     * 删除任务
     *
     * @RequestParam taskId 流程实例ID
     * @RequestParam reason 删除原因
     */
    @PostMapping("/deleteTask")
	@ResponseBody
//    @TryCatchMsg("删除失败！${errorMsg}")
    public AjaxResult deleteTask(@RequestBody Map<String, String> params) {
        String procInsId = params.get("procInsId");
        String taskId = params.get("taskId");
        String reason = params.get("reason");

        if (Strings.isBlank(reason)) {
            return AjaxResult.error("删除失败，请填写删除原因！");
        } else {
//            flowTaskService.deleteTask(taskId, reason);
            runtimeService.deleteProcessInstance(procInsId, reason);
        }
        return AjaxResult.success("删除任务成功，任务ID=" + taskId);
    }
}
