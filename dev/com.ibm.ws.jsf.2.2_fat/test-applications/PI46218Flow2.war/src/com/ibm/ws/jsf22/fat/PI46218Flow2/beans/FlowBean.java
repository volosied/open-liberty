package com.ibm.ws.jsf22.fat.PI46218Flow2.beans;

import java.io.Serializable;

import javax.faces.flow.FlowScoped;
import javax.inject.Named;

@Named("flowBean")
@FlowScoped("test-flow-2")
public class FlowBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String _message;

    public String getMessage() {
        return _message;
    }
    
    public void setMessage(String message) {
        _message = message;
    }
}
