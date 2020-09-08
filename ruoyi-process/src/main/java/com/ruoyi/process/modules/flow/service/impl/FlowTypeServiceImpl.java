/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.service.impl;


import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.modules.flow.domain.FlowType;
import com.ruoyi.process.modules.flow.mapper.FlowTypeMapper;
import com.ruoyi.process.modules.flow.service.FlowTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月15日 17时07分04秒
 */
@Service("flowTypeService")
public class FlowTypeServiceImpl implements FlowTypeService {

    @Autowired
    FlowTypeMapper flowTypeMapper;

    @Override
    public List<FlowType> findAllOrderByShortNoAsc() {
        return flowTypeMapper.findAllOrderByShortNoAsc();
    }

    @Override
    public String fetchCategoryName(Long categoryId) {
        if (FlowConstant.DEFAULT_CATEGORY.equals(categoryId)) {
            return "未分类";
        }
        return flowTypeMapper.findById(categoryId).getName();
    }

    @Override
    public int deleteById(Long id) {
        return flowTypeMapper.deleteById(id);
    }

    @Override
    public int count(Map<String, Object> params) {
        return 0;
    }

    @Override
    public int save(FlowType flowType) {
        return flowTypeMapper.save(flowType);
    }

    @Override
    public int update(FlowType flowType) {
        return 0;
    }
}
