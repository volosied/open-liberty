package com.ibm.ws.jsf22.fat.miscbean.jar;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class EmailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email = null;

    /**
     * Initialisation of Email Bean.
     */
    @PostConstruct
    public void init() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PreDestroy
    public void goodbye() {}

}