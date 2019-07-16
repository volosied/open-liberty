package com.ibm.ws.jsf22.fat.appconfigpop;

/**
 * Simple test bean, defined in ACPInitializer
 */
public class AddedBean {

    private String message = "SuccessfulAddedBeanTest";

    public String getMessage() {

        return message;
    }

    public void setMessageBean(String message) {
        this.message = message;
    }
}
