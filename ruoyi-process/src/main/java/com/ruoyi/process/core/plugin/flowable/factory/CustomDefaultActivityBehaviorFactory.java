/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.factory;

import com.ruoyi.process.core.plugin.flowable.behavior.*;
import org.flowable.bpmn.model.*;
import org.flowable.engine.impl.bpmn.behavior.*;
import org.flowable.engine.impl.bpmn.helper.ClassDelegate;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.nutz.ioc.Ioc;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/3
 */
public class CustomDefaultActivityBehaviorFactory extends DefaultActivityBehaviorFactory {

    // TODO
//    DepartmentLeaderService departmentLeaderService;

    CustomClassDelegateFactory classDelegateFactory;

    public CustomDefaultActivityBehaviorFactory() {// TODO DepartmentLeaderService departmentLeaderService
        // TODO
//        this.departmentLeaderService = departmentLeaderService;
        this.classDelegateFactory = new CustomClassDelegateFactory();
//        if (this.departmentLeaderService == null) {
//            throw new RuntimeException("departmentLeaderService 不能为null!");
//        }
    }

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new CustomUserTaskActivityBehavior(userTask);// TODO departmentLeaderService
    }

    /**
     * 无特定触发器的启动事件
     *
     * @param startEvent
     * @return
     */
    @Override
    public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
        return new CustomNoneStartEventActivityBehavior();
    }

    /**
     * 无特定触发器的结束事件
     *
     * @param endEvent
     * @return
     */
    @Override
    public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent) {
        return new CustomNoneEndEventActivityBehavior();
    }

    @Override
    public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        return new CustomSequentialMultiInstanceBehavior(activity, innerActivityBehavior);
    }

    @Override
    public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        return new CustomParallelMultiInstanceBehavior(activity, innerActivityBehavior);
    }


    @Override
    public ClassDelegate createClassDelegateServiceTask(ServiceTask serviceTask) {
        return classDelegateFactory.create(serviceTask.getId(), serviceTask.getImplementation(),
                createFieldDeclarations(serviceTask.getFieldExtensions()),
                serviceTask.isTriggerable(),
                getSkipExpressionFromServiceTask(serviceTask), serviceTask.getMapExceptions());
    }
}
