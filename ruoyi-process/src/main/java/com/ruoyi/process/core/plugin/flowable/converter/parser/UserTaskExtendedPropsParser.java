package com.ruoyi.process.core.plugin.flowable.converter.parser;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.child.BaseChildElementParser;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;

import javax.xml.stream.XMLStreamReader;

public class UserTaskExtendedPropsParser extends BaseChildElementParser {
    @Override
    public String getElementName() {
        return "userTaskExtension";
    }

    @Override
    public void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception {
        System.out.println("UserTaskExtendedPropsParser已调用");
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setName(xtr.getLocalName());
        if (StringUtils.isNotEmpty(xtr.getNamespaceURI())) {
            extensionElement.setNamespace(xtr.getNamespaceURI());
        }
        if (StringUtils.isNotEmpty(xtr.getPrefix())) {
            extensionElement.setNamespacePrefix(xtr.getPrefix());
        }
        extensionElement.setElementText(xtr.getElementText());
        parentElement.addExtensionElement(extensionElement);
    }
}
