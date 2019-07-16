package com.ibm.ws.jsf22.fat.miscbean.jar;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class AppManagerBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appName = null;

    private String errorMessage = null;

    /**
     * Initialisation of Application Manager.
     */
    @PostConstruct
    public void init() {
        appName = "JSF View Scope Test";
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @PreDestroy
    public void goodbye() {}
}