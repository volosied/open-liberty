package com.ibm.ws.jsf22.fat.PI90507;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * Custom Action Listener
 */
public class TestActionListener implements ActionListener {

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct from TestActionListener");
    }

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        System.out.println("Process action from TestActionListener");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("Pre destroy from TestActionListener");
    }
}