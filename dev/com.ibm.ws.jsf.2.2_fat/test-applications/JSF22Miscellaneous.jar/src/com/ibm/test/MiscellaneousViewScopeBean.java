package com.ibm.test;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlCommandButton;

@ManagedBean(name = "miscellaneousViewScopeBean")
@ViewScoped
public class MiscellaneousViewScopeBean implements Serializable {
//    private static final Logger LOGGER = Logger.getLogger(MiscellaneousViewScopeBean.class.getName());
    private static final long serialVersionUID = -3118004082493064756L;

    private HtmlCommandButton button;
    private Integer counter = 0;

    public void setCounter(Integer results) {
        this.counter = results;
    }

    public String getCounter() {
        return ("PostConstruct counter = " + counter);
    }

    public HtmlCommandButton getButton() {
        return button;
    }

    public void setButton(HtmlCommandButton button) {
        this.button = button;
    }

    @PostConstruct
    public void init() {
        counter++;
    }

}