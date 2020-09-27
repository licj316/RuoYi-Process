package com.ruoyi.process.modules.flow.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.process.core.plugin.flowable.service.ProcessDesignService;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Model;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author yiyoung
 * @date 2020/4/21
 */
@RestController
@RequestMapping("/workflow")
public class ProcessDesignController extends BaseProcessController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessDesignService processDesignService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 创建模型
     */
    @RequestMapping(value = "/model/insert", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void createModel(@RequestParam String key, @RequestParam String name, @RequestParam String category, @RequestParam String descp) throws UnsupportedEncodingException {
        processDesignService.createModel(key, name, category, descp);
    }

    @RequestMapping(value = "/model/list", method = RequestMethod.GET)
    public AjaxResult listModel(@RequestParam(value = "flowTypeId", required = false) Long flowTypeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("category", flowTypeId);
        List<Map<String, Object>> listModel = processDesignService.listModelPage(params);
        return AjaxResult.success(listModel);
    }

    @PutMapping(value = "/model/edit")
    public AjaxResult listModel(@RequestBody Map<String, Object> params) {
        String modelId = Objects.toString(params.get("modelId"), "");
        String category = Objects.toString(params.get("category"), "");

        if (StringUtils.isBlank(modelId)) {
            AjaxResult.error("modelId不允许为空！");
        }
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        if (StringUtils.isNotBlank(category)) {
            model.setCategory(category);
        }
        repositoryService.saveModel(model);
        return AjaxResult.success("操作成功！");
    }

    @ResponseBody
    @GetMapping(value = "/deleteModel")
    public AjaxResult flowDelete(@RequestParam(name = "modelId") String modelId) {
        processDesignService.deleteModel(modelId);
        return success("保存成功！");
    }

    /**
     * 保存模型
     */
    @RequestMapping(value = "/model/{modelId}/xml/save", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public AjaxResult saveModelXml(@PathVariable String modelId, @RequestBody Map<String, String> values) {
        processDesignService.saveModelXml(modelId, values);
        return success("保存成功！");
    }

    @RequestMapping(value = "/process/tracing/{procInsId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public Map<String, String> getProcessInfo(@PathVariable("procInsId") String procInsId) {
        List<HistoricActivityInstance> activityList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).orderByHistoricActivityInstanceStartTime().asc().list();

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        String deploymentId = historicProcessInstance.getDeploymentId();
        List<String> deploymentResourceNames = repositoryService.getDeploymentResourceNames(deploymentId);
        String xmlResourceName = deploymentResourceNames.stream().filter(s -> s.endsWith(".xml")).findFirst().orElseThrow(() -> new RuntimeException("未查询到该流程的流程图数据！"));
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, xmlResourceName);

        Map<String, String> map = new HashMap<>();
        try {
            String bpmnXML = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            // 查询用户列表
            ObjectMapper objectMapper = new ObjectMapper();

            List<Comment> processComments = taskService.getProcessInstanceComments(procInsId);
            String activityListStr = objectMapper.writeValueAsString(activityList);
            String processCommentsStr = objectMapper.writeValueAsString(processComments);

            map.put("name", historicProcessInstance.getName());
            map.put("bpmnXml", bpmnXML);
            map.put("bpmnName", xmlResourceName.substring(0, xmlResourceName.lastIndexOf(".")));
            map.put("description", historicProcessInstance.getDescription());
            map.put("version", String.valueOf(historicProcessInstance.getProcessDefinitionVersion()));
            map.put("activityList", activityListStr);
            map.put("comments", processCommentsStr);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return map;
        }
    }

    @RequestMapping(value = "/process/{procInsId}/hisTask/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HistoricTaskInstance> getProcessTask(@PathVariable("procInsId") String procInsId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInsId).orderByHistoricTaskInstanceStartTime().desc().list();
        return list;
    }

    /**
     * 根据生成的ID获取模型流程编辑器
     *
     * @param modelId
     * @return
     */
    @RequestMapping(value = "/model/{modelId}/xml", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public JSONObject getEditorXml(@PathVariable String modelId) {
        return processDesignService.getEditorXml(modelId);
    }

    @GetMapping(value = "/model/deploy")
    public String deploy(@RequestParam(name = "modelId") String modelId) throws Exception {
        return processDesignService.deployModel(modelId);
    }

    @GetMapping(value = "/task/reviewer/list")
    public List<Map<String, String>> getApproverInfo(@RequestParam("reviewerType") String reviewerType, @RequestParam(value = "searchKey", required = false) String searchKey) throws Exception {
        List<Map<String, String>> res;
        if ("USER_ROLE_GROUPS".equals(reviewerType)) {
            SysRole sysRole = new SysRole();
            sysRole.setRoleName(searchKey);
            sysRole.setRoleKey(searchKey);
            List<SysRole> sysRoles = sysRoleService.selectRoleList(sysRole);
            res = sysRoles.stream().map(sysRoleTmp -> ImmutableMap.of("name", sysRoleTmp.getRoleName() + "(" + sysRoleTmp.getRoleKey() + ")", "id", String.valueOf(sysRoleTmp.getRoleId()))).collect(Collectors.toList());
        } else if ("SINGLE_USER".equals(reviewerType) || "MULTIPLE_USERS".equals(reviewerType)) {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(searchKey);
            sysUser.setLoginName(searchKey);
            List<SysUser> sysUsers = sysUserService.selectUserList(sysUser);
            res = sysUsers.stream().map(sysUserTmp -> ImmutableMap.of("name", sysUserTmp.getUserName() + "(" + sysUserTmp.getLoginName() + ")", "id", String.valueOf(sysUserTmp.getUserId()))).collect(Collectors.toList());
        } else {
            throw new RuntimeException("查询审批者类型不正确");
        }
        return res;
    }
}
