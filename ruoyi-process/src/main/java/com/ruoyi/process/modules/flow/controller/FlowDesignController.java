/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.converter.CustomBpmnJsonConverter;
import com.ruoyi.process.core.plugin.flowable.util.FlowDiagramUtils;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import com.ruoyi.process.core.plugin.flowable.validator.CustomProcessValidatorFactory;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ValuedDataObject;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 */
@Controller
@RequestMapping("/flowDesign")
//@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class FlowDesignController {

    private static final Logger log = LogManager.getLogger(FlowDesignController.class);

    @Autowired
	RepositoryService repositoryService;
    @Autowired
    ISysUserService userAccountService;
    @Autowired
    ISysRoleService roleService;

    @Autowired
    FlowDiagramUtils flowDiagramUtils;

    CustomBpmnJsonConverter customBpmnJsonConverter = new CustomBpmnJsonConverter();

    // 流程管理
    @RequestMapping("/flowable")
    public String flowable() {
        return "";
    }

    @RequestMapping({"/stencil-sets/editor", "/stencilSets/editor"})
    @ResponseBody
    public String NutMap() {
        return Files.read("flowable/stencilset_bpmn.json");
    }

    @GetMapping("/models/{modelId}/editor/json")
    @ResponseBody
    public NutMap getEditorJson(@PathVariable String modelId) {
        NutMap modelNode = new NutMap();
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (Strings.isNotBlank(model.getMetaInfo())) {
                    modelNode = Json.fromJson(NutMap.class, model.getMetaInfo());
                } else {
                    modelNode = new NutMap();
                    modelNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
                }
                modelNode.put(ModelDataJsonConstants.MODEL_ID, model.getId());
                byte[] bytes = repositoryService.getModelEditorSource(model.getId());
                modelNode.put("model", NutMap.WRAP(new String(bytes)));
            } catch (Exception e) {
                log.error("创建JSON模型时出错", e);
                throw new FlowableException("创建JSON模型时出错", e);
            }
        }
        return modelNode;
    }

    @PostMapping("/models/{modelId}/save")
    @ResponseBody
    public AjaxResult saveModel(@PathVariable String modelId, @RequestParam("name") String name, @RequestParam("key") String key, @RequestParam("description") String description, @RequestParam("json_xml") String json) {
        try {
            Model model = repositoryService.getModel(modelId);
            Model oldModel = repositoryService.createModelQuery().modelKey(key).singleResult();
            if (oldModel != null && !oldModel.getId().equals(model.getId())) {
                return AjaxResult.error("Key 已经存在！请修改！");
            }
            NutMap modelJson = Json.fromJson(NutMap.class, model.getMetaInfo());
            NutMap bpmJson = Json.fromJson(NutMap.class, json);
            NutMap propertiesNode = bpmJson.getAs("properties", NutMap.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            BpmnModel bpmnModel = customBpmnJsonConverter.convertToBpmnModel(jsonNode);
            if (bpmnModel.getMainProcess() != null) {
                ValuedDataObject valuedDataObject = bpmnModel.getMainProcess().getDataObjects().stream().filter(va -> FlowConstant.PROCESS_TITLE.equals(va.getId())).findAny().orElse(null);
                if (valuedDataObject == null) {
                    return AjaxResult.errorf("保存失败!流程应该设置标题模版，id为{0}", FlowConstant.PROCESS_TITLE);
                }
            }
            ProcessValidator validator = new CustomProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (errors.size() > 0) {
                ValidationError error = errors.get(0);
                return AjaxResult.errorf("{1} ：[{0}] ", error.getActivityName(), error.getDefaultDescription());
            }
            propertiesNode.put(StencilConstants.PROPERTY_PROCESS_ID, key);
            modelJson.put(ModelDataJsonConstants.MODEL_NAME, Strings.sNull(name));
            modelJson.put("key", Strings.sNull(key));
            modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, Strings.sNull(description));
            model.setMetaInfo(Json.toJson(modelJson, JsonFormat.compact()));
            model.setName(Strings.sNull(name));
            model.setKey(Strings.sNull(key));
            model.setVersion(model.getVersion() + 1);
            //保存前将设计器传回的json转化一下,设计器上的json有些问题
            jsonNode = customBpmnJsonConverter.convertToJson(bpmnModel);
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), jsonNode.toString().getBytes(StandardCharsets.UTF_8));
            return AjaxResult.success("保存成功！");
        } catch (Exception e) {
            log.error("模型保存失败", e);
            return AjaxResult.errorf("模型保存失败:{0}", e.getLocalizedMessage());
        }
    }

    // @AdaptBy(type = JsonAdaptor.class) TODO 暂时注释掉，不知道有什么用
    @PostMapping("/models/validate")
    @ResponseBody
    public List<ValidationError> validate(@RequestBody HashMap body) {
        List<ValidationError> errors = new ArrayList<>();
        try {
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(new StringReader(Json.toJson(body, JsonFormat.compact())));
            BpmnModel bpmnModel = customBpmnJsonConverter.convertToBpmnModel(modelNode);
            ProcessValidator validator = new CustomProcessValidatorFactory().createDefaultProcessValidator();
            errors = validator.validate(bpmnModel);
        } catch (IOException e) {
            log.error(e);
        }
        return errors;
    }

    /**
     * 此方法已经被自定义流程审核范围覆盖
     *
     * @param filter
     * @return
     */
    @RequestMapping("/editor-users")
    @ResponseBody
    public ResultListDataRepresentation getUsers(@RequestParam("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            SysUser sysUser = userAccountService.selectUserByLoginName(filter);
            List<? extends User> matchingUsers = FlowUtils.toFlowableUserList(Lists.newArrayList(sysUser));
            List<UserRepresentation> userRepresentations = new ArrayList<>(matchingUsers.size());
            for (User user : matchingUsers) {
                userRepresentations.add(new UserRepresentation(user));
            }
            return new ResultListDataRepresentation(userRepresentations);
        } else {
            return new ResultListDataRepresentation();
        }
    }

    /**
     * 此方法已经被自定义流程审核范围覆盖
     *
     * @param filter
     * @return
     */
    @RequestMapping("/editor-groups")
    @ResponseBody
    public ResultListDataRepresentation getGroups(@RequestParam("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            SysRole sysRole = roleService.selectRoleById(Long.valueOf(filter));
            List<GroupEntity> result = new ArrayList<>();
            List<? extends GroupEntity> groups = FlowUtils.toFlowableGroupList(Lists.newArrayList(sysRole));
            for (GroupEntity group : groups) {
                result.add(group);
            }
            return new ResultListDataRepresentation(result);
        } else {
            return new ResultListDataRepresentation();
        }
    }

    @PostMapping("/reviewerUsers")
    @ResponseBody
    public List<SysUser> reviewerUsers(@RequestParam("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            return Lists.newArrayList(userAccountService.selectUserByLoginName(filter));
        } else {
            return Arrays.asList();
        }
    }

    @PostMapping("/reviewerUserRoles")
    @ResponseBody
    public List<SysRole> reviewerUserRoles(@RequestParam("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            return Lists.newArrayList(roleService.selectRoleById(Long.valueOf(filter)));
        } else {
            return Arrays.asList();
        }
    }

    @GetMapping("/admin/process-instances/{processInstanceId}/model-json")
    @ResponseBody
    public NutMap getProcessInstanceModelJSON(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("processDefinitionId") String processDefinitionId) {
        return flowDiagramUtils.getHistoryProcessInstanceModelJSON(processInstanceId, processDefinitionId);
    }

    @GetMapping(value = "/admin/process-instances/{processInstanceId}/history-model-json")
    @ResponseBody
    public NutMap getHistoryProcessInstanceModelJSON(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("processDefinitionId") String processDefinitionId) {
        return flowDiagramUtils.getHistoryProcessInstanceModelJSON(processInstanceId, processDefinitionId);
    }


}
