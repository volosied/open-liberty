package com.ibm.ws.jsf22.fat.ajax.resetValue;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class ResetBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int value1;
    private int value2;

    public void increaseValue() {
        value1++;
    }

    public void reset() {
        value1 = 0;
        value2 = 0;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }
}
