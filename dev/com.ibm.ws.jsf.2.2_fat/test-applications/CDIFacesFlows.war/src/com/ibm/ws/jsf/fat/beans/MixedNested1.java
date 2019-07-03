package com.ibm.ws.jsf.fat.beans;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.faces.flow.Flow;
import javax.faces.flow.builder.FlowBuilder;
import javax.faces.flow.builder.FlowBuilderParameter;
import javax.faces.flow.builder.FlowDefinition;

/**
 * Define a very simple flow via the FlowBuilder API
 */
public class MixedNested1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Produces
    @FlowDefinition
    public Flow defineFlow(@FlowBuilderParameter FlowBuilder flowBuilder) {

        String flowId = "mixedNested1";
        flowBuilder.id("", flowId);
        flowBuilder.viewNode(flowId, "/" + flowId + "/" + flowId + ".xhtml").markAsStartNode();

        flowBuilder.returnNode("goIndex").fromOutcome("/JSF22Flows_index.xhtml");
        flowBuilder.returnNode("goReturn").fromOutcome("/JSF22Flows_return.xhtml");

        // Call the second nested flow, which happens to be defined declaratively;
        // pass a parameter to the flow (the flowScope test value)
        flowBuilder.flowCallNode("callMixedNested2").flowReference("", "mixedNested2").outboundParameter("testValue", "#{flowScope.testValue}");
        return flowBuilder.getFlow();
    }
}
