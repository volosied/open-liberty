package com.ibm.ws.jsf22.fat.PI90507;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

/**
 * Simple session scoped bean
 */
@ManagedBean(name = "bean")
@SessionScoped
public class MyBackingBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private TestActionListener testActionListener;

    public TestActionListener getTestActionListener() {
        return testActionListener;
    }

    public void setTestActionListener(TestActionListener testActionListener) {
        this.testActionListener = testActionListener;
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("MyBackingBean: Session timeout");
    }

}
