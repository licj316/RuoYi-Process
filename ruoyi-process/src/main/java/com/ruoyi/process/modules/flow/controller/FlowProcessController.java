/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.process.core.plugin.flowable.service.FlowProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 */
@Controller
@RequestMapping("/flow/process")
@Slf4j
public class FlowProcessController extends BaseProcessController{

    @Autowired
    FlowProcessDefinitionService flowProcessDefinitionService;

    // 流程管理
    @GetMapping("/index")
    public String index() {
        return "view/modules/flow/process/index.html";
    }

    @PostMapping("/listPage")
	@ResponseBody
    public TableDataInfo listPage(@RequestParam(value = "catagoryId", required = false) String catagoryId, HttpServletRequest request) {
        return getDataTable(flowProcessDefinitionService.processList(catagoryId));
    }

    // 删除部署流程
    @PostMapping("/deleteDeployment")
    @ResponseBody
    public AjaxResult deleteDeployment(@RequestParam("deploymentId") String deploymentId) {
        flowProcessDefinitionService.deleteDeployment(deploymentId);
        return AjaxResult.success();
    }

    // 激活、挂起
    @PostMapping("/update/{operType}")
    @ResponseBody
    public AjaxResult updateDeployment(@PathVariable("operType") String state, @RequestParam("procDefId") String procDefId) {
        return AjaxResult.success(flowProcessDefinitionService.updateState(state, procDefId));
    }


    /**
     * 读取资源，通过部署ID
     *
     * @param procDefId 流程定义ID
     * @param proInsId  流程实例ID
     * @param resType   资源类型(xml|image)
     * @param response
     * @throws Exception
     */
    @GetMapping("/resource/read")
    public Object export(String procDefId, String proInsId, String resType, HttpServletResponse response) {
        try {
            InputStream resourceAsStream = flowProcessDefinitionService.resourceRead(procDefId, proInsId, resType);
            final String xmlResType = "xml";
            if (xmlResType.equals(resType)) {
                response.setHeader("Content-Type", "text/xml");
            } else {
                response.setHeader("Content-Type", "image/jpeg");
            }
            int cache = 1024;
            byte[] b = new byte[cache];
            int len;
            while ((len = resourceAsStream.read(b, 0, cache)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            return null;
        } catch (Exception e) {
            log.error("读取资源文件失败", e);
            // TODO 需要正确的错误页面
            return "error";
//            return ViewUtil.toErrorPage("查看{1}文件失败：procDefId={2} 错误信息：{3}" + procDefId, resType, e.getLocalizedMessage());
        }
    }

}
