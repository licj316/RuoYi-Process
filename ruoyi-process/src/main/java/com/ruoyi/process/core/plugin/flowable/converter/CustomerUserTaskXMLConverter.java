package com.ruoyi.process.core.plugin.flowable.converter;

import com.ruoyi.process.core.plugin.flowable.converter.parser.UserTaskExtendedPropsParser;
import org.flowable.bpmn.converter.UserTaskXMLConverter;

public class CustomerUserTaskXMLConverter extends UserTaskXMLConverter {

    public CustomerUserTaskXMLConverter() {
        super();
        UserTaskExtendedPropsParser extendedPropsParser = new UserTaskExtendedPropsParser();
        super.childParserMap.put(extendedPropsParser.getElementName(), extendedPropsParser);
    }
}
