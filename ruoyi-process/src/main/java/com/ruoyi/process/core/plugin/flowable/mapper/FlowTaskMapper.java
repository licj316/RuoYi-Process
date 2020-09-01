package com.ruoyi.process.core.plugin.flowable.mapper;


import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowTaskMapper {

    List<Map<String, Object>> findTodoList(Map<String, Object> params);

    List<Map<String, Object>> findHasSentList(Map<String, Object> params);

    List<Map<String, Object>> findHistoricList(Map<String, Object> params);

}
