package com.ibm.ws.jsf22.fat.PI46218Flow1.flows;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.faces.flow.Flow;
import javax.faces.flow.builder.FlowBuilder;
import javax.faces.flow.builder.FlowBuilderParameter;
import javax.faces.flow.builder.FlowDefinition;

public class FlowFactory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Produces @FlowDefinition
    public Flow defineTestFlow(@FlowBuilderParameter FlowBuilder flowBuilder) {
        final String flowId = "test-flow-1";
        flowBuilder.id("", flowId);

        flowBuilder.viewNode("flow1", "/" + flowId + "/flow1.xhtml").markAsStartNode();
        flowBuilder.viewNode("flow2", "/" + flowId + "/flow2.xhtml");
        flowBuilder.returnNode("return-node").fromOutcome("home");

        return flowBuilder.getFlow();
    }
}
