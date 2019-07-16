package com.ibm.ws.jsf22.fat.componenttester;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class CollapsiblePanelController {
    private boolean collapsed = false;

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public String toggle() {
        collapsed = !collapsed;
        return null;
    }

}
