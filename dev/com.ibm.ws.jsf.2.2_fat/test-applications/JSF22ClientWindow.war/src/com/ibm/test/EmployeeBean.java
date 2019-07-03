package com.ibm.test;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;

@ManagedBean
@SessionScoped
public class EmployeeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String firstName = null;
    protected String lastName = null;
    protected String windowId = null;

    public EmployeeBean() {}

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

    public String getWindowId() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        ClientWindow clientWindow = externalContext.getClientWindow();
        if (clientWindow != null) {
            windowId = clientWindow.getId();
        }

        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }
}
