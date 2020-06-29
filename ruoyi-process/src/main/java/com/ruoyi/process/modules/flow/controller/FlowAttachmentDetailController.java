package com.ruoyi.process.modules.flow.controller;

import com.ruoyi.process.modules.flow.service.FlowAttachmentDetailService;
import com.ruoyi.process.modules.flow.service.FlowAttachmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/flowAttachmentDetail")
public class FlowAttachmentDetailController {

	private String prefix = "view/modules/flow/general";

	private static final Logger log = LogManager.getLogger(FlowAttachmentDetailController.class);

	@Autowired
	private FlowAttachmentDetailService flowAttachmentDetailService;

	// 流程管理
	@RequestMapping("")
	public String list(@RequestParam("procInsId") String procInsId,@RequestParam("taskId") String taskId) {
		return "/flowAttachmentDetail";
	}

	@RequestMapping("/list")
	@ResponseBody
	public String listPage(@RequestParam("procInsId") String procInsId,@RequestParam("taskId") String taskId) {
		return null;
	}
}
