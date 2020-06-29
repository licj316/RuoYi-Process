package com.ruoyi.process.modules.flow.controller;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.UUIDUtil;
import com.ruoyi.process.core.plugin.flowable.util.FileUtil;
import com.ruoyi.process.modules.flow.domain.FlowAttachmentDetail;
import com.ruoyi.process.modules.flow.service.FlowAttachmentDetailService;
import com.ruoyi.process.modules.flow.service.FlowAttachmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/flowAttachment")
public class FlowAttachmentController extends BaseProcessController {

	private String prefix = "view/modules/flow/general";

	private static final Logger log = LogManager.getLogger(FlowAttachmentController.class);

	@Autowired
	private FlowAttachmentService flowAttachmentService;

	@Autowired
	private FlowAttachmentDetailService flowAttachmentDetailService;

	@RequestMapping("/detail")
	public String list(@RequestParam("procInsId") String procInsId) {
		return prefix + "/flowAttachment";
	}

	@RequestMapping("/listDetail")
	@ResponseBody
	public TableDataInfo listDetail(@RequestParam("procInsId") String procInsId) {
		List<Map<String, Object>> flowAttachmentList = flowAttachmentService.listDetail(ImmutableMap.of("procInsId", procInsId));
		return getDataTable(flowAttachmentList);
	}

	@RequestMapping("/upload")
	@ResponseBody
	public AjaxResult upload(@RequestParam(value = "file", required = true) MultipartFile file,
							 @RequestParam(value = "flowAttachmentId", required = true) String flowAttachmentId, HttpServletRequest request) {
		try {
			String fileOriginalName = file.getOriginalFilename();
			long fileSize = file.getSize();
			String fileType = FileUtil.getFileType(fileOriginalName);
			String saveFileName = UUIDUtil.getUUID() + "." + FileUtil.getSuffix(fileOriginalName);
			String saveRelativePath = FileUtil.getRelativePath(saveFileName);
			Path saveFullPath = Paths.get(Global.getProfile(), saveRelativePath);
			Files.createDirectories(saveFullPath.getParent());
			File saveFile = saveFullPath.toFile();
			file.transferTo(saveFile);

			FlowAttachmentDetail flowAttachmentDetail = FlowAttachmentDetail.builder()
					.flowAttachmentId(flowAttachmentId)
					.fileName(fileOriginalName)
					.saveFileName(saveFileName)
					.fileType(fileType)
					.fileSize(fileSize)
					.filePath(saveRelativePath)
					.delFlag("0")
					.build();
			flowAttachmentDetail.setCreateBy(getCurrUser().getLoginName());
			flowAttachmentDetail.setCreateTime(new Date());
			flowAttachmentDetailService.save(flowAttachmentDetail);
		} catch (IOException e) {
			e.printStackTrace();
			return error();
		}
		return success();
	}

	@DeleteMapping("/delete/{flowAttachmentDetailId}")
	@ResponseBody
	public AjaxResult delete(@RequestParam(value = "flowAttachmentDetailId", required = true) String flowAttachmentDetailId, HttpServletRequest request) {
		flowAttachmentDetailService.deleteById(flowAttachmentDetailId);
		return success();
	}
}
