package com.ibm.ws.jsf.fat.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * This Bean is used to keep track of a an integer count over the life of an application
 */
@Named
@ApplicationScoped
public class CountBean {

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void increment() {
        count++;
    }
}
