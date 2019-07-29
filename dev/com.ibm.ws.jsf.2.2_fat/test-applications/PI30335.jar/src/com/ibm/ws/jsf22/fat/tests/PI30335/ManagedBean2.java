package com.ibm.ws.jsf22.fat.tests.PI30335;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

@ManagedBean(name="managedBean2")
public class ManagedBean2 {
	
	private static Logger LOGGER = Logger.getLogger(ManagedBean2.class.getSimpleName());
	
	public ManagedBean2() {
	}
	
	@PostConstruct
	public void init() {
		LOGGER.info("@PostConstruct invoked: " + this.getClass().getSimpleName());
	}
	
	

}
