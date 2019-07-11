package com.ibm.ws.fat.jsf.ajaxDelay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class EmployeeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<String> employees;
    protected String name;

    public EmployeeBean() {
        employees = Arrays.asList("john doe", "john jones", "joe smith");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMatchingEmployees() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.log("AjaxDelayTest getMatchingEmployees");

        if (name == null) {
            return null;
        }
        List<String> matches = new ArrayList<String>();
        for (String emp : employees) {
            if (emp.startsWith(name)) {
                matches.add(emp);
            }
        }
        return matches;
    }

}