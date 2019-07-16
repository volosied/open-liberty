package com.ibm.ws.jsf22.fat.cdiflows.beans;

import java.io.Serializable;

import javax.faces.flow.FlowScoped;
import javax.inject.Named;

/**
 * A simple FlowScoped bean used to test basic functionality.
 */
@Named(value = "testBean")
@FlowScoped(value = "simpleBean")
public class TestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String testValue;

    public TestBean() {
        this.testValue = "";
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }

    public String getReturnValue() {
        return "/JSF22Flows_return";
    }

    /* Returns a string for the sake of navigating to the second page in the flow. */
    public String simpleSubmit() {
        return "simpleBean-2";
    }
}
