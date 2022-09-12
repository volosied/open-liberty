/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.el50.fat.lambda.coercion.servlets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import componenttest.app.FATServlet;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import jakarta.el.BeanELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELManager;
import jakarta.el.StandardELContext;
import jakarta.servlet.annotation.WebServlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.function.Predicate;

import jakarta.el.ELProcessor;

/**
 * Servlet for testing LambdaExpression coercion in EL 5.0
 * https://github.com/jakartaee/expression-language/issues/45
 */
@WebServlet({ "/EL50LambdaExpressionCoercion" })
public class EL50LambdaExpressionCoercionServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    ELProcessor elp;

    public EL50LambdaExpressionCoercionServlet() {
        super();

        elp = new ELProcessor();
        elp.defineBean("someBean", new SomeBean());
    }

    @Test
    public void testLambdaCoercion() throws Exception {
        String result;
        String expectedResult ="success";
        Object obj = elp.eval("someBean.testLambdaCoercion(param -> true)");
        assertNotNull(obj);
        result = obj.toString();

        assertEquals("The expression did not evaluate to: " + expectedResult + " but was: " + result, expectedResult, result);
    }

    public class SomeBean {

        public String testLambdaCoercion(Predicate<String> filter) {
            return "success";
        }

    }

}
