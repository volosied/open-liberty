package com.ibm.ws.jsf22.fat.cdiflows.beans;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.faces.flow.Flow;
import javax.faces.flow.builder.FlowBuilder;
import javax.faces.flow.builder.FlowBuilderParameter;
import javax.faces.flow.builder.FlowDefinition;

/**
 * Define a very simple flow via the FlowBuilder API
 */
public class SimpleFlowBuilder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Produces
    @FlowDefinition
    public Flow defineFlow(@FlowBuilderParameter FlowBuilder flowBuilder) {
        String flowId = "simpleFlowBuilder";
        flowBuilder.id("", flowId);
        flowBuilder.viewNode(flowId, "/" + flowId + "/" + flowId + ".xhtml").markAsStartNode();

        flowBuilder.returnNode("goIndex").fromOutcome("/JSF22Flows_index.xhtml");
        flowBuilder.returnNode("goReturn").fromOutcome("/JSF22Flows_return.xhtml");

        return flowBuilder.getFlow();
    }
}
