package com.ruoyi.process.core.plugin.flowable.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowModelMapper {

    List<Map<String, Object>> listPage(Map<String, Object> params);

}
