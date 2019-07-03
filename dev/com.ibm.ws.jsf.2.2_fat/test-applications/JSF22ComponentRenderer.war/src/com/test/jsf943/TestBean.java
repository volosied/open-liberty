package com.test.jsf943;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="jsf943")
@SessionScoped

public class TestBean implements Serializable{
	
	private Boolean exists= false;
	
	public Boolean getClassExists() {
		try {
			Class<?> act = Class.forName("javax.faces.view.ViewDeclarationLanguageWrapper");
			this.exists = true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			this.exists = false;
			e.printStackTrace();
		} 
		return exists;
	}
	
}