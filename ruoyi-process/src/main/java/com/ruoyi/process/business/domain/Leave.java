/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.business.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Leave extends BaseEntity {
    /**
     * ids
     */
    private String id;
    /**
     * 请假原因
     */
    private String reason;

    /**
     * 请假开始日期
     */
    private Date startTime;

    /**
     * 请假结束日期
     */
    private Date endTime;

    /**
     * 假期类别
     */
    private String leaveType;

    /**
     * 部门领导意见
     */
    private String deptLeadText;

    /**
     * 人事部门意见
     */
    private String hrText;

    /**
     * 人事部门意见
     */
    private String generalManagerText;
}
