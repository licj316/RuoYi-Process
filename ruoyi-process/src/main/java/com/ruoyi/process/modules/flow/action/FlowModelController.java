/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.converter.CustomBpmnJsonConverter;
import com.ruoyi.process.core.plugin.flowable.service.FlowCacheService;
import com.ruoyi.process.core.plugin.flowable.util.FileUtil;
import com.ruoyi.process.core.plugin.flowable.util.FlowUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.util.XmlUtil;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 * 流程定义-新增-编辑-导入-部署-删除
 */
@RequestMapping("/flowable")
// @Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
@Slf4j
@Controller
public class FlowModelController extends BaseProcessController {

	private String prefix = "view/modules/flow/model";

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	FlowCacheService flowCacheService;

	CustomBpmnJsonConverter bpmnJsonConverter = new CustomBpmnJsonConverter();
	BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();

	@GetMapping("/design")
	public String design(ModelMap modelMap) {
		modelMap.put("productVersion", "2");
		return "view/modules/flow/model/design.html";
	}

	@GetMapping("/index")
	public String index() {
		return "view/modules/flow/model/index.html";
	}

	@GetMapping("/addModel")
	public String add() {
		return prefix + "/addModel";
	}

	@PostMapping("/addModel")
	@ResponseBody
	public AjaxResult addModel(@RequestParam("categoryId") String categoryId, @RequestParam("name") String name, @RequestParam("key") String key, @RequestParam("description") String description) throws UnsupportedEncodingException {
		Model model = FlowUtils.buildModel(repositoryService.newModel(), categoryId, name, key, description);
		boolean hasOldKey = repositoryService.createModelQuery().modelKey(key).count() > 0;
		if (hasOldKey) {
			return AjaxResult.error("Key 已经存在！请修改！");
		}
		repositoryService.saveModel(model);
		NutMap editorNode = NutMap.NEW();
		editorNode.put("resourceId", model.getId());
		NutMap properties = NutMap.NEW();
		properties.put("process_id", key);
		properties.put("process_namespace", "https://github.com/threefish");
		properties.put("name", name);
		properties.put("documentation", description);
		editorNode.put("properties", properties);
		NutMap stencilSetNode = NutMap.NEW();
		stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
		editorNode.put("stencilset", stencilSetNode);
		repositoryService.addModelEditorSource(model.getId(), Json.toJson(editorNode, JsonFormat.compact()).getBytes(StandardCharsets.UTF_8));
		return AjaxResult.success(model.getId());
	}

	@PostMapping("/listPage")
	@ResponseBody
	public TableDataInfo listPage(HttpServletRequest request, @RequestParam("categoryId") String categoryId) {
		ModelQuery modelQuery = repositoryService.createModelQuery().modelCategory(categoryId);
		// TODO 分页并未实现
		List<Model> list = modelQuery.orderByCreateTime().desc().listPage(0, 1000);
		return getDataTable(list);
	}

	@PostMapping("/delete")
	@ResponseBody
	public AjaxResult delete(@RequestParam("modelId") String modelId) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			if (Strings.isNotBlank(modelData.getDeploymentId())) {
				repositoryService.deleteDeployment(modelData.getDeploymentId(), true);
			}
			repositoryService.deleteModel(modelId);
			return AjaxResult.success("删除成功，模型ID=" + modelId);
		} catch (Exception e) {
			return AjaxResult.errorf("删除模型失败：modelId={0} \r\n {1}", modelId, e.getMessage());
		}
	}

	@PostMapping("/category/edit")
	@ResponseBody
	public AjaxResult editCategory(@RequestParam("modelId") String modelId, @RequestParam("categoryId") String categoryId) {
		try {
			Model model = repositoryService.getModel(modelId);
			NutMap modelJson = Json.fromJson(NutMap.class, model.getMetaInfo());
			modelJson.put("category", categoryId);
			model.setMetaInfo(Json.toJson(modelJson, JsonFormat.compact()));
			model.setCategory(categoryId);
			repositoryService.saveModel(model);
			return AjaxResult.success("修改成功！");
		} catch (Exception e) {
			return AjaxResult.errorf("修改失败！{0}", e.getMessage());
		}
	}

	/**
	 * 如果将项目部署至中文目录下，部署流程时会报错的
	 *
	 * @param modelId
	 * @return
	 */
	@PostMapping("/deploy")
	@ResponseBody
	public AjaxResult deploy(@RequestParam("modelId") String modelId) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel model = new CustomBpmnJsonConverter().convertToBpmnModel(modelNode);
			byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model, Encoding.UTF8);
			String processName = modelData.getName() + ".bpmn20.xml";
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).category(modelData.getCategory()).addString(processName, new String(bpmnBytes)).deploy();
			modelData.setDeploymentId(deployment.getId());
			repositoryService.saveModel(modelData);
			flowCacheService.delCache();
			return AjaxResult.success("部署成功，部署ID=" + modelId);
		} catch (Exception e) {
			return AjaxResult.errorf("根据模型部署流程失败：modelId={0} \r\n {1}", modelId, e.getMessage());
		}
	}

	@PostMapping("/import")
	@ResponseBody
//	@AdaptBy(type = UploadAdaptor.class, args = {"ioc:upload"})
	public AjaxResult fileUploadact(@RequestParam("file") TempFile tf, @RequestParam("categoryId") String categoryId) {
		String fileName = tf.getSubmittedFileName();
		boolean isBpmn20xml = fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"));
		if (isBpmn20xml) {
			try {
				XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
				InputStreamReader xmlIn = new InputStreamReader(tf.getInputStream(), StandardCharsets.UTF_8);
				XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
				BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
				if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
					return AjaxResult.error("找不到定义的流程 " + fileName);
				}
				if (bpmnModel.getLocationMap().size() == 0) {
					BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
					bpmnLayout.execute();
				}
				ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);

				org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
				String key = process.getId();
				String name = process.getName();
				String description = process.getDocumentation();
				if (Strings.isBlank(categoryId)) {
					categoryId = FlowConstant.DEFAULT_CATEGORY;
				}
				if (repositoryService.createModelQuery().modelKey(key).count() > 0) {
					key = R.UU16();
					ObjectNode propertiesNode = (ObjectNode) modelNode.get("properties");
					propertiesNode.put(StencilConstants.PROPERTY_PROCESS_ID, key);
				}
				Model model = FlowUtils.buildModel(repositoryService.newModel(), categoryId, name, key, description);
				repositoryService.saveModel(model);
				repositoryService.addModelEditorSource(model.getId(), modelNode.toString().getBytes(StandardCharsets.UTF_8));
				return AjaxResult.success();
			} catch (BadRequestException e) {
				throw e;
			} catch (Exception e) {

				log.error(String.format("导入失败 {0}", fileName), e);
				return AjaxResult.error("导入失败 " + fileName + ", 错误信息 " + e.getMessage());
			}
		} else {
			return AjaxResult.error("文件名无效，仅支持.bpmn和.bpmn20.xml文件 " + fileName);
		}

	}

	@GetMapping("/export/{modelId}")
	@ResponseBody
	public Object export(@PathVariable("modelId") String modelId, HttpServletResponse response) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			CustomBpmnJsonConverter jsonConverter = new CustomBpmnJsonConverter();
			JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
			Path file = FileUtil.createTempFile();
			Files.write(file, bpmnBytes);
			String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			return file.toFile();
		} catch (Exception e) {
			log.error("导出model的xml文件失败：modelId=" + modelId, e);
			// TODO 返回错误页面，暂时屏蔽了
			return "error";
//			return ViewUtil.toErrorPage("导出model的xml文件失败：modelId={0} {1}", modelId, e.getLocalizedMessage());
		}
	}
}
