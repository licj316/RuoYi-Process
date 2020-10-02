package com.ruoyi.process.modules.flow.service.impl;

import com.google.common.collect.ImmutableMap;
import com.ruoyi.process.modules.flow.domain.FlowConfigExtend;
import com.ruoyi.process.modules.flow.domain.FlowInstExtend;
import com.ruoyi.process.modules.flow.mapper.FlowConfigExtendMapper;
import com.ruoyi.process.modules.flow.service.FlowConfigExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class FlowConfigExtendServiceImpl implements FlowConfigExtendService {

    @Autowired
    FlowConfigExtendMapper flowConfigExtendMapper;

    @Override
    public void save(FlowConfigExtend flowConfigExtend) {
        flowConfigExtendMapper.save(flowConfigExtend);
    }

    @Override
    public void update(FlowConfigExtend flowConfigExtend) {
        flowConfigExtendMapper.update(flowConfigExtend);
    }

    @Override
    public FlowConfigExtend findByProcDefKey(String procDefKey) {
        Assert.isTrue(StringUtils.isNotBlank(procDefKey), "流程定义KEY不能为空");
        List<FlowConfigExtend> flowConfigExtendList = flowConfigExtendMapper.findByParams(ImmutableMap.of("procDefKey", procDefKey));
        if (null != flowConfigExtendList && flowConfigExtendList.size() > 0) {
            return flowConfigExtendList.get(0);
        }
        return null;
    }
}
