package com.ibm.ws.jsf.fat.beans;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
public class InitializerBean {

    public InitializerBean() {}

    public void initialize() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<Object, Object> inboundParameters = facesContext.getApplication().getFlowHandler().getCurrentFlowScope();

        // See if the inbound-parameter is set correctly.
        String testParameter = (String) inboundParameters.get("testValue");
        if ((testParameter == null) || !testParameter.contains("test string"))
            throw new IllegalArgumentException("initializer:   did NOT find inbound-parameter");
    }
}