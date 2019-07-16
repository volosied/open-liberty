package com.ibm.ws.jsf22.fat.componentrenderer.jsf479;

import java.io.Serializable;

public class Thing implements Serializable {

    protected String propOne = "First Property";
    protected String propTwo = "Second Property";

    public Thing() {}

    public String getPropOne() {
        return propOne;
    }

    public void setPropOne(String propOne) {
        this.propOne = propOne;
    }

    public String getPropTwo() {
        return propTwo;
    }

    public void setPropTwo(String propTwo) {
        this.propTwo = propTwo;
    }
}
