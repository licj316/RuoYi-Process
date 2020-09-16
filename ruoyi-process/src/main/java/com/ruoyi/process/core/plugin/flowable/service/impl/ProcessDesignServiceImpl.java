package com.ruoyi.process.core.plugin.flowable.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.process.core.plugin.flowable.converter.CustomerBpmnXMLConverter;
import com.ruoyi.process.core.plugin.flowable.mapper.FlowModelMapper;
import com.ruoyi.process.core.plugin.flowable.service.ProcessDesignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.Model;
import org.flowable.ui.common.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @Author yiyoung
 * @date 2020/4/21
 */
@Service
@Slf4j
public class ProcessDesignServiceImpl implements ProcessDesignService,ModelDataJsonConstants {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlowModelMapper flowModelMapper;

    /**
     * 保存模型
     *
     * @param key
     * @param name
     * @param category
     * @param descp
     * @throws UnsupportedEncodingException
     */
    @Override
    public void createModel(String key, String name, String category, String descp) throws UnsupportedEncodingException {
        //初始化一个空模型
        Model model = repositoryService.newModel();
        //设置一些默认信息
        String modelName = name;
        String description = descp;
        int revision = 1;
        String modelKey = key;

        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, modelName);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

        model.setName(modelName);
        model.setKey(modelKey);
        model.setMetaInfo(modelNode.toString());

        repositoryService.saveModel(model);
        String id = model.getId();

        //完善ModelEditorSource
//        ObjectNode editorNode = objectMapper.createObjectNode();
//        editorNode.put("id", "canvas");
//        editorNode.put("resourceId", "canvas");
//        ObjectNode stencilSetNode = objectMapper.createObjectNode();
//        stencilSetNode.put("namespace",
//                "http://activiti.org/bpmn");
//        editorNode.put("stencilset", stencilSetNode);

        repositoryService.addModelEditorSource(id, "".getBytes());
    }

    /**
     * 查询模型
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> listModelPage(Map<String, Object> params) {
//        ModelQuery modelQuery = repositoryService.createModelQuery();
//        if(null != flowTypeId) {
//            modelQuery.modelCategory(String.valueOf(flowTypeId));
//        }
//        return modelQuery.list();

        return flowModelMapper.listPage(params);
    }

    /**
     * 删除模型
     *
     * @param modelId
     */
    @Override
    public void deleteModel(String modelId) {
        repositoryService.deleteModel(modelId);
    }

    /**
     * 部署流程
     *
     * @param modelId
     */
    @Override
    public String deployModel(String modelId) throws Exception {
        // 获取模型
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        if (null == bytes) {
            return "模型数据为空，请先设计流程并成功保存，再进行发布。";
        }
//        JsonNode modelNode = objectMapper.readTree(bytes);
//        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
//        if (model.getProcesses().size() == 0) {
//            return "数据模型不符合要求，请至少设计一条主线程流。";
//        }
//        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

//        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        CustomerBpmnXMLConverter customerBpmnXMLConverter = new CustomerBpmnXMLConverter();
//        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
        System.out.println("前=================================");
        System.out.println(new String(bytes));
        XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
        InputStream is = new ByteArrayInputStream(bytes);
        BpmnModel bpmnModel = customerBpmnXMLConverter.convertToBpmnModel(xif.createXMLStreamReader(is));
        List<Process> processes = bpmnModel.getProcesses();
        if (processes.size() == 0) {
            return "数据模型不符合要求，请至少设计一条主线程流。";
        }
        byte[] bpmnXMLBytes = customerBpmnXMLConverter.convertToXML(bpmnModel);

        System.out.println("后=================================");
        System.out.println(new String(bpmnXMLBytes));

        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        System.out.println(modelData.getName());
        DeploymentBuilder deployment1 = repositoryService.createDeployment();
        deployment1.name(modelData.getName());

        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnXMLBytes, "UTF-8"))
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return "success";
    }

    /**
     * 根据生成的ID获取模型流程编辑器
     *
     * @param modelId
     * @return
     */
    @Override
    public JSONObject getEditorXml(@PathVariable String modelId) {
        JSONObject jsonObject = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    jsonObject = JSON.parseObject(model.getMetaInfo());
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put(MODEL_NAME, model.getName());
                }
                jsonObject.put(MODEL_ID, model.getId());
                byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
                String bpmnXml;
                if (modelEditorSource != null && modelEditorSource.length > 0) {
                    bpmnXml = new String(modelEditorSource);
                } else {
                    bpmnXml = "";
                }
                jsonObject.put("bpmnXml", bpmnXml);
            } catch (Exception e) {
                log.error("创建model的json串失败", e);
                throw new FlowableException("无法读取model信息", e);
            }
        } else {
            log.error("创建model的json串失败[{}]", modelId);
            throw new FlowableException("未找到对应模型信息");
        }
        return jsonObject;
    }

    /**
     * 保存模型
     */
    @Override
    public void saveModelXml(@PathVariable String modelId,
                             @RequestBody MultiValueMap<String, String> values) {
        ByteArrayOutputStream outStream = null;
        try {

            Model model = repositoryService.getModel(modelId);
            // 获取模型信息
            ObjectNode modelJson = (ObjectNode) objectMapper
                    .readTree(model.getMetaInfo());
            // 获取value第一个元素
            modelJson.put(MODEL_NAME, model.getName());
            modelJson.put(MODEL_DESCRIPTION, modelJson.get("description"));
            model.setMetaInfo(modelJson.toString());
            // 版本
            model.setVersion(model.getVersion() + 1);
            repositoryService.saveModel(model);
            String bpmnXml = values.getFirst("bpmn_xml");
//            String bpmnXml = BpmnConverterUtil
//                    .converterXmlToJson(values.getFirst("bpmn_xml")).toString();
//            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
//            InputStream is = new ByteArrayInputStream(bpmnXml.getBytes());
//            bpmnXMLConverter.convertToBpmnModel(xif.createXMLStreamReader(is));
            repositoryService.addModelEditorSource(model.getId(), bpmnXml.getBytes("utf-8"));
            repositoryService.addModelEditorSourceExtra(model.getId(), values.getFirst("svg_xml").getBytes("utf-8"));
        } catch (Exception e) {
            log.error("Error saving model", e);
            throw new FlowableException("Error saving model", e);
        }
    }
}
