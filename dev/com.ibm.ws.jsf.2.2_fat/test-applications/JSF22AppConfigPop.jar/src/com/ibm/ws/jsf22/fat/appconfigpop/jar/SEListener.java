package com.ibm.ws.jsf22.fat.appconfigpop.jar;

import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class SEListener implements SystemEventListener {

    public SEListener() {
        // Do nothing
    }

    @Override
    public boolean isListenerForSource(Object source) {

        return true;
    }

    @Override
    public void processEvent(SystemEvent event) {
        FacesContext.getCurrentInstance().getExternalContext().log("JSF22:  AOP System event listener called.");

    }
}