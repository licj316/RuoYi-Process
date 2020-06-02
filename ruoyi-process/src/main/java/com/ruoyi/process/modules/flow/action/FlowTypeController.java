/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.action;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseTreeEntity;
import com.ruoyi.common.utils.TreeUtil;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.modules.flow.domain.FlowType;
import com.ruoyi.process.modules.flow.service.FlowTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ModelQuery;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月15日
 * 流程分类管理
 */
@Controller
@RequestMapping("/FlowType")
//@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
@Slf4j
public class FlowTypeController {



    @Autowired
    FlowTypeService flowTypeService;
    @Autowired
	RepositoryService repositoryService;

    @RequestMapping("/tree")
    @ResponseBody
    public List<BaseTreeEntity> flowTypes() {
        List<FlowType> types = flowTypeService.findAllOrderByShortNoAsc();
        FlowType root = FlowType.builder().name("根节点").virtualNode(true).build();
        root.setId("0");
        root.setPid("");
        FlowType defaultType = FlowType.builder().name("未分类").virtualNode(true).build();
        defaultType.setId(FlowConstant.DEFAULT_CATEGORY);
        defaultType.setPid("0");
        defaultType.setPName("根节点");

        types.add(root);
        types.add(defaultType);
        return TreeUtil.createTree(types, "");
    }

    /**
     * 批量删除
     *
     * @param categoryId
     * @return
     */
    @RequestMapping("/del")
    @ResponseBody
//    @RequiresPermissions("FlowType.index.del")
//    @AutoCreateMenuAuth(name = "删除分类", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "sys.flow.model")
    public AjaxResult del(@Param("id") String categoryId) {
        long count = flowTypeService.count(ImmutableMap.of("pid", categoryId));
        if (count > 0) {
            return AjaxResult.error("当前分类下还有其他分类，无法删除！");
        }
        ModelQuery modelQuery = repositoryService.createModelQuery().modelCategory(categoryId);
        if (modelQuery.count() > 0) {
            return AjaxResult.error("当前分类下还有流程模型，无法删除！");
        }
        flowTypeService.deleteById(categoryId);
        return AjaxResult.success("删除成功");
    }

    /**
     * 保存
     *
     * @param data
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
//    @RequiresPermissions("FlowType.index.edit")
//    @Aop(TransAop.READ_UNCOMMITTED)
//    @AutoCreateMenuAuth(name = "新增、修改分类", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "sys.flow.model")
    public AjaxResult save(@Param("::") FlowType data) {// , Errors errors
//        if (errors.hasError()) {
//            return AjaxResult.error(errors.getErrorsList().iterator().next());
//        }
        boolean update = Strings.isNotBlank(data.getId());
        Map<String, String> params = new HashMap<>();
        if (update) {
            params.put("name", data.getName());
            params.put("id", data.getId());
        } else {
            params.put("name", data.getName());
        }
        int count = flowTypeService.count(params);
        if (count > 0) {
            return AjaxResult.errorf("【{0}】名称已经存在!", data.getName());
        } else {
            if (update) {
                flowTypeService.update(data);
            } else {
                flowTypeService.save(data);
            }
            return AjaxResult.success("保存成功");
        }
    }
}
