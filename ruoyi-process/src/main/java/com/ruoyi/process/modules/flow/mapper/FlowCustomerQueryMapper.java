package com.ruoyi.process.modules.flow.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowCustomerQueryMapper {

	List<Map<String, Object>> getFlowSubmitInfo(String taskId);

	List<Map<String, Object>> listUserTaskNodeAllReviewerUser(List<Long> userIdList);
}
