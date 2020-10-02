/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.process.modules.flow.domain.FlowConfigExtend;
import com.ruoyi.process.modules.flow.service.FlowConfigExtendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月15日
 * 流程分类管理
 */
@Controller
@RequestMapping("/FlowConfigExtend")
@Slf4j
public class FlowConfigExtendController extends BaseProcessController {

    @Autowired
    FlowConfigExtendService flowConfigExtendService;
    @Autowired
	RepositoryService repositoryService;

    @RequestMapping("/{procDefKey}")
    @ResponseBody
    public AjaxResult flowConfigExtexd(@PathVariable(name = "procDefKey") String procDefKey) {
        return AjaxResult.success(flowConfigExtendService.findByProcDefKey(procDefKey));
    }

    /**
     * 保存
     *
     * @param flowConfigExtend
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(@RequestBody FlowConfigExtend flowConfigExtend) {
        String procDefKey = flowConfigExtend.getProcDefKey();
        Assert.isTrue(StringUtils.isNotBlank(procDefKey), "流程模型ID不能为空");

        FlowConfigExtend flowConfigExtendOrigin = flowConfigExtendService.findByProcDefKey(procDefKey);
        if(null == flowConfigExtendOrigin) {
            flowConfigExtendService.save(flowConfigExtend);
        } else {
            flowConfigExtendService.update(flowConfigExtend);
        }
        return AjaxResult.success("保存成功");
    }
}
