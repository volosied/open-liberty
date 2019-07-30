package com.ibm.ws.jsf22.fat.PH01566;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="bean")
@SessionScoped
public class Bean {

	private String welcome = "application initialization succeeded";
	
	public Bean() {
	}

	public String getWelcome() {
		return welcome;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}
	
}
