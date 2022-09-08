/*******************************************************************************
 * Copyright (c) 1997, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.StringTokenizer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.PagesVersionHandler;

public class ImportGenerator extends CodeGeneratorBase {

	public void startGeneration(int section, JavaCodeWriter writer)
		throws JspCoreException {
		if (section == CodeGenerationPhase.IMPORT_SECTION) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);
					String directiveName = attribute.getNodeName();
					String directiveValue = attribute.getNodeValue();
					if (directiveName.equals("import")) {
						StringTokenizer tokenizer = new StringTokenizer(directiveValue, ",");
						writeDebugStartBegin(writer);
						while (tokenizer.hasMoreTokens()) {
							writer.println("import " + (String) tokenizer.nextToken() + ";");
						}
						writeDebugStartEnd(writer);
					}
				}
			}
		} else if (section == CodeGenerationPhase.STATIC_SECTION && PagesVersionHandler.isPages31OrHigherLoaded()) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);
					String directiveName = attribute.getNodeName();
					String directiveValue = attribute.getNodeValue();
					if (directiveName.equals("import")) {
						StringTokenizer tokenizer = new StringTokenizer(directiveValue, ",");
						writeDebugStartBegin(writer);
						while (tokenizer.hasMoreTokens()) {
							String singleImport = ((String) tokenizer.nextToken()).trim();
							if(singleImport.endsWith(".*")){
								writer.println("importPackage.add(\"" + singleImport.replace(".*", "") + "\");");
							} else {
								writer.println("importClass.add(\"" + singleImport + "\");");
							}
							
						}
						writeDebugStartEnd(writer);
					}
				}
			}
			// Pages 1.10 Directive Packages java.lang.*, jakarta.servlet.*, jakarta.servlet.jsp.*, and jakarta.servlet.http.* are imported implicitly by the JSP container.
			// writer.println("importPackage.add(\"jakarta.servlet.*\");");
			// writer.println("importPackage.add(\"jakarta.servlet.jsp.*\");");
			// writer.println("importPackage.add(\"jakarta.servlet.http.*\");");
			//Caused by: java.lang.IllegalArgumentException: Resource osgi.identity; osgi.identity="gateway.bundle.Thread_Context.WebModule_test-el_test-el.war"; type="osgi.bundle"; version:Version="0.0.0" cannot dynamically import package 'jakarta.servlet.http' since it already has access to it.
		}
	}

	public void endGeneration(int section, JavaCodeWriter writer)
		throws JspCoreException {
	}

}
