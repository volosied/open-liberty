package com.ibm.ws.jsf22.fat.tests.PI30335;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name="managedBean1")
public class ManagedBean1 {
	
	private static Logger LOGGER = Logger.getLogger(ManagedBean1.class.getSimpleName());
	
	@ManagedProperty(value="#{managedBean2}")
	private ManagedBean2 managedBean2;
	
	public ManagedBean1() {
	}
	
	@PostConstruct
	public void init() {
		LOGGER.info("@PostConstruct invoked: " + this.getClass().getSimpleName());
		if (managedBean2 == null) {
			LOGGER.warning("The reference to managedBean2 is null!");
		}
		LOGGER.info("ManagedBean Ref: " + managedBean2);
	}

	/**
	 * @return the managedBean2
	 */
	public ManagedBean2 getManagedBean2() {
		return managedBean2;
	}

	/**
	 * @param managedBean2 the managedBean2 to set
	 */
	public void setManagedBean2(ManagedBean2 managedBean2) {
		LOGGER.info("setManagedBean2(...) invoked: " + this.getClass().getSimpleName());
		this.managedBean2 = managedBean2;
	}

}
