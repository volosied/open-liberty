package com.ibm.ws.fat.jsf.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Simple bean to test the h:commandButton action on first click when
 * there is one composite component with comments on interface section.
 */
@ManagedBean
@RequestScoped
public class CompositeComponentTestBean {

    public String execAction() {
        return "testCommandButtonExecution";
    }
}
