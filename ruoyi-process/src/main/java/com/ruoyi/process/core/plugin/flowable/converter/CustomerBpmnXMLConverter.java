package com.ruoyi.process.core.plugin.flowable.converter;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;

import javax.xml.stream.XMLStreamReader;

public class CustomerBpmnXMLConverter extends BpmnXMLConverter {

    static {
        addConverter(new CustomerUserTaskXMLConverter());
    }

    @Override
    public BpmnModel convertToBpmnModel(XMLStreamReader xtr) {

        BpmnModel bpmnModel = super.convertToBpmnModel(xtr);
        return bpmnModel;
    }
}
