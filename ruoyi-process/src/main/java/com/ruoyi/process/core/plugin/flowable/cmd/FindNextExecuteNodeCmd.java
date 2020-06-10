/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.cmd;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.condition.ConditionUtil;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/9
 * 使用示例，一定要放到事务中，否则变量会入库，导致数据紊乱
 * try {
 * Trans.begin();
 * UserTask userTask = managementService.executeCommand(new FindNextUserTaskNodeCmd(execution, bpmnModel, vars));
 * System.out.println(userTask.getId());
 * } finally {
 * Trans.clear(true);
 * }
 */
public class FindNextExecuteNodeCmd implements Command<Pair<String, FlowElement>> {

    private final ExecutionEntity execution;
    private final BpmnModel bpmnModel;
    private Map<String, Object> vars;
    /**
     * 返回下一用户节点
     */
    private Pair<String, FlowElement> nextExecNodeTuple;

    /**
     * @param execution 当前执行实例
     * @param bpmnModel 当前执行实例的模型
     * @param vars      参与计算流程条件的变量
     */
    public FindNextExecuteNodeCmd(ExecutionEntity execution, BpmnModel bpmnModel, Map<String, Object> vars) {
        this.execution = execution;
        this.bpmnModel = bpmnModel;
        this.vars = vars;
    }

    /**
     * @param execution 当前执行实例
     * @param bpmnModel 当前执行实例的模型
     */
    public FindNextExecuteNodeCmd(ExecutionEntity execution, BpmnModel bpmnModel) {
        this.execution = execution;
        this.bpmnModel = bpmnModel;
    }

    @Override
    public Pair<String, FlowElement> execute(CommandContext commandContext) {
        execution.setVariables(vars);
        FlowElement currentNode = bpmnModel.getFlowElement(execution.getActivityId());
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentNode).getOutgoingFlows();
        if (CollectionUtils.isNotEmpty(outgoingFlows)) {
            this.findNextExecNode(outgoingFlows, execution);
        }
        return nextExecNodeTuple;
    }


    void findNextExecNode(List<SequenceFlow> outgoingFlows, ExecutionEntity execution) {
        sw:
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            if (ConditionUtil.hasTrueCondition(outgoingFlow, execution)) {
                if (outgoingFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
                    //只有排他网关才继续
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway) outgoingFlow.getTargetFlowElement();
                    findNextExecNode(exclusiveGateway.getOutgoingFlows(), execution);
                } else if (outgoingFlow.getTargetFlowElement() instanceof UserTask) {
                    nextExecNodeTuple = Pair.of(StencilConstants.STENCIL_TASK_USER, outgoingFlow.getTargetFlowElement());
                    //找到第一个符合条件的userTask就跳出循环
                    break sw;
                } else if (outgoingFlow.getTargetFlowElement() instanceof EndEvent) {
                    nextExecNodeTuple = Pair.of(StencilConstants.STENCIL_EVENT_END_NONE, outgoingFlow.getTargetFlowElement());
                    break sw;
                }
            }
        }
    }
}
