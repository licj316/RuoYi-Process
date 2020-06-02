/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.service.impl;

import com.google.common.collect.Lists;
import com.ruoyi.process.core.plugin.flowable.constant.FlowConstant;
import com.ruoyi.process.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.ruoyi.process.modules.flow.mapper.FlowCustomerQueryMapper;
import com.ruoyi.process.modules.flow.service.FlowCustomQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/11
 */
@Service
public class FlowCustomQueryServiceImpl implements FlowCustomQueryService {

    @Autowired
    FlowCustomerQueryMapper flowCustomerQueryMapper;

    @Override
    public FlowSubmitInfoDTO getFlowSubmitInfo(String taskId) {
        FlowSubmitInfoDTO dto = new FlowSubmitInfoDTO();
        List<Map<String, Object>> flowSubmitInfoList = flowCustomerQueryMapper.getFlowSubmitInfo(taskId);
//        List<NutMap> list = queryMapBySql("getFlowSubmitInfo", NutMap.NEW().setv("taskId", taskId));
        flowSubmitInfoList.forEach(map -> {
            String name = map.get("name").toString();
            String value = map.get("val").toString();
            if (Objects.equals(FlowConstant.SUBMITTER, name)) {
                dto.setUserName(value);
            }
            if (Objects.equals(FlowConstant.SUBMITTER_DEPT_ID, name)) {
                dto.setDeptId(value);
            }
        });
        return dto;
    }

    @Override
    public List<Map<String, Object>> listUserTaskNodeAllReviewerUser(List<String> candidateUserNames) {
        if (CollectionUtils.isEmpty(candidateUserNames)) {
            return Lists.newArrayList();
        }
        List<Long> userIdList = candidateUserNames.stream().map(s -> Long.valueOf(s)).collect(Collectors.toList());
        return flowCustomerQueryMapper.listUserTaskNodeAllReviewerUser(userIdList);
//        return queryMapBySql("listUserTaskNodeAllReviewerUser", NutMap.NEW(), Cnd.where("userName", "in", candidateUserNames));
    }
}
