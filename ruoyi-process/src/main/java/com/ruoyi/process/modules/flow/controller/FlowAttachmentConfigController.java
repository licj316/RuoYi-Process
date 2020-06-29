package com.ruoyi.process.modules.flow.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.process.modules.flow.service.FlowAttachmentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@RequestMapping("/flowAttachmentConfig")
@Controller
public class FlowAttachmentConfigController extends BaseController {

	private String prefix = "view/modules/flow/attachment";

	@Autowired
	FlowAttachmentConfigService flowAttachmentConfigService;

	@RequestMapping("/list")
	public String list() {
		return prefix + "/flowAttachmentConfigList";
	}

	@PostMapping("/listPage")
	@ResponseBody
	public TableDataInfo listPage(Map<String, Object> params) {
		startPage();
		List<Map<String, Object>> list = flowAttachmentConfigService.listPage(params);
		return getDataTable(list);
	}

}
