/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.service;

import com.ruoyi.process.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/11
 * 流程信息自定义查询
 */
public interface FlowCustomQueryService {

    FlowSubmitInfoDTO getFlowSubmitInfo(String taskId);

    List<Map<String, Object>> listUserTaskNodeAllReviewerUser(List<String> candidateUserNames);
}
