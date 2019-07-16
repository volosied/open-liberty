package com.ibm.ws.jsf22.fat.miscbean;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class MiscellaneousEmployeeBean {
//    private static final Logger LOGGER = Logger.getLogger(MiscellaneousEmployeeBean.class.getName());

    private String firstName = null;
    private String lastName = null;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void reset() {
        firstName = null;
        lastName = null;
    }
}