package com.test.jsf479;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


@ManagedBean(name="things")
@RequestScoped
public class Things implements Serializable {
    
    Collection<Thing> things;

    public Collection<Thing> getThings() {
        return things;
    }

    public void setThings(Collection<Thing> things) {
        this.things = things;
    }
    
    public Things() {
    	// Insert a bunch of Thing into Things.
    	things = new ArrayList<Thing>();
    	Thing t;
        for (int i = 0; i < 10; i++) {
            t = new Thing();
            t.setPropOne("One Thing " + i);
            t.setPropTwo("Two Thing "+i);
            things.add(t);
        }
    }
}
