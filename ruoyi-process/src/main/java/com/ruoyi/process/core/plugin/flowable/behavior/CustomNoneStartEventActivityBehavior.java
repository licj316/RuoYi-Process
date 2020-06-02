/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.behavior;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/23
 */
public class CustomNoneStartEventActivityBehavior extends NoneStartEventActivityBehavior {

    private static final Logger log = LogManager.getLogger(CustomNoneStartEventActivityBehavior.class);

    @Override
    public void execute(DelegateExecution execution) {
        this.process(execution);
        super.leave(execution);
    }

    public void process(DelegateExecution execution) {
        if (log.isDebugEnabled()) {
            log.debug("流程开始" + execution.getProcessInstanceId());
        }
    }
}
