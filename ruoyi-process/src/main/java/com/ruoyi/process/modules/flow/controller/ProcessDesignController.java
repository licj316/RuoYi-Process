package com.ruoyi.process.modules.flow.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.process.core.plugin.flowable.service.ProcessDesignService;
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
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yiyoung
 * @date 2020/4/21
 */
@RestController
@RequestMapping("/workflow")
public class ProcessDesignController {

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

        if(StringUtils.isBlank(modelId)) {
            AjaxResult.error("modelId不允许为空！");
        }
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        if(StringUtils.isNotBlank(category)) {
            model.setCategory(category);
        }
        repositoryService.saveModel(model);
        return AjaxResult.success("操作成功！");
    }

    @ResponseBody
    @GetMapping(value = "/deleteModel")
    public void flowDelete(@RequestParam(name = "modelId") String modelId) {
        processDesignService.deleteModel(modelId);
    }

    /**
     * 保存模型
     */
    @RequestMapping(value = "/model/{modelId}/xml/save", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModelXml(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
        processDesignService.saveModelXml(modelId, values);
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
            // TODO 查询用户列表
//            activityList.stream().filter(historicActivityInstance -> "".equals(historicActivityInstance.getActivityType())).map(historicActivityInstance -> historicActivityInstance.getAssignee()).collect(Collectors.toList());
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
    public List<Map<String, String>> getApproverInfo(@Param("reviewerType") String reviewerType, @Param("searchKey") String searchKey) throws Exception {
        List<Map<String, String>> res;
        int size = 0;
        if ("USER_ROLE_GROUPS".equals(reviewerType)) {
            List<Map<String, String>> roleList = new ArrayList<>();
            List<String> roleNameList = Arrays.asList("运营", "采购", "销售", "CEO", "CTO", "CFO");
            size = roleNameList.size();
            for (int i = 0; i < size; i++) {
                roleList.add(ImmutableMap.of("name", roleNameList.get(i), "id", i + ""));
            }
            res = roleList.stream().filter(tmpMap -> tmpMap.get("name").contains(searchKey)).collect(Collectors.toList());
        } else if ("SINGLE_USER".equals(reviewerType) || "MULTIPLE_USERS".equals(reviewerType)) {
            List<Map<String, String>> userList = new ArrayList<>();
            List<String> userNameList = Arrays.asList("张三", "李四", "王五", "小明", "小张", "小王");
            size = userNameList.size();
            for (int i = 0; i < size; i++) {
                userList.add(ImmutableMap.of("name", userNameList.get(i), "id", i + ""));
            }
            res = userList.stream().filter(tmpMap -> tmpMap.get("name").contains(searchKey)).collect(Collectors.toList());
        } else {
            throw new RuntimeException("不正确");
        }
        return res;
    }
}
