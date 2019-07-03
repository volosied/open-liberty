package com.ibm.ws.jsf.test;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
 
@FacesComponent (value = "marqueeComponent")
public class MarqueeComponent extends UIComponentBase {
 
	String value = null;
 
	public String getValue() {
		return value;
	}
 
	@Override
	public String getFamily() {
		return "testComponents";
	}
 
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("marquee", this);
		writer.write(getValue());
		writer.endElement("marquee");
	}
 
 
	@Override
	public void encodeEnd(FacesContext arg0) throws IOException {
		super.encodeEnd(arg0);
	}
 
	public void setValue(String value) {
		this.value = value;
	}
 
}