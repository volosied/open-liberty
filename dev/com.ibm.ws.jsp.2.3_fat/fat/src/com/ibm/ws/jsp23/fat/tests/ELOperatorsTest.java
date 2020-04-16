/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsp23.fat.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.ibm.ws.fat.util.browser.WebBrowser;

import componenttest.annotation.SkipForRepeat;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

/**
 * All JSP 2.3 tests with all applicable server features enabled.
 *
 * Tests that just need to drive a simple request using our WebBrowser object can be placed in this class.
 *
 * If a test needs httpunit it should more than likely be placed in the JSPServerHttpUnit test class.
 */
@SkipForRepeat("CDI-2.0")
@RunWith(FATRunner.class)
public class ELOperatorsTest extends LoggingTest {

    private static final String EL_APP_NAME = "TestEL";

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("elServer");

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(SHARED_SERVER.getLibertyServer(),
                                      EL_APP_NAME + ".war",
                                      "com.ibm.ws.jsp23.fat.testel.beans",
                                      "com.ibm.ws.jsp23.fat.testel.servlets");
                                      
        SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(EL_APP_NAME);

        SHARED_SERVER.startIfNotStarted();
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        // Stop the server
        if (SHARED_SERVER.getLibertyServer() != null && SHARED_SERVER.getLibertyServer().isStarted()) {
            SHARED_SERVER.getLibertyServer().stopServer();
        }
    }

    /**
     * Test the existing EL 2.2 operators in the context of a JSP.
     *
     * @throws Exception
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    public void testEL22Operators() throws Exception {
        // Each entry in the array is an expected output in the response
        String[] expectedInResponse = {
                                        "<b>Test 1:</b> EL 2.2 Multiplication Operator (Expected:16): 16",
                                        "<b>Test 2:</b> EL 2.2 Addition Operator (+) (Expected:5): 5",
                                        "<b>Test 3:</b> EL 2.2 Subtraction Operator (-) (Expected:1): 1",
                                        "<b>Test 4:</b> EL 2.2 Division Operator (/) (Expected:8.0): 8.0",
                                        "<b>Test 5:</b> EL 2.2 Division Operator (div) (Expected:8.0): 8.0",
                                        "<b>Test 6:</b> EL 2.2 Remainder Operator (%) (Expected:1): 1",
                                        "<b>Test 7:</b> EL 2.2 Remainder Operator (mod) (Expected:1): 1",
                                        "<b>Test 8:</b> EL 2.2 Relational Operator (==) (Expected: true): true",
                                        "<b>Test 9:</b> EL 2.2 Relational Operator (eq) (Expected: false): false",
                                        "<b>Test 10:</b> EL 2.2 Relational Operator (!=) (Expected: true): true",
                                        "<b>Test 11:</b> EL 2.2 Relational Operator (ne) (Expected: false): false",
                                        "<b>Test 12:</b> EL 2.2 Relational Operator (<) (Expected: true): true",
                                        "<b>Test 13:</b> EL 2.2 Relational Operator (lt) (Expected: false): false",
                                        "<b>Test 14:</b> EL 2.2 Relational Operator (>) (Expected: false): false",
                                        "<b>Test 15:</b> EL 2.2 Relational Operator (gt) (Expected: true): true",
                                        "<b>Test 16:</b> EL 2.2 Relational Operator (<=) (Expected: true): true",
                                        "<b>Test 17:</b> EL 2.2 Relational Operator (le) (Expected: false): false",
                                        "<b>Test 18:</b> EL 2.2 Relational Operator (le) (Expected: true): true",
                                        "<b>Test 19:</b> EL 2.2 Relational Operator (>=) (Expected: true): true",
                                        "<b>Test 20:</b> EL 2.2 Relational Operator (ge) (Expected: false): false",
                                        "<b>Test 21:</b> EL 2.2 Relational Operator (ge) (Expected: true): true",
                                        "<b>Test 22:</b> EL 2.2 Logical Operator (&&) (Expected: false): false",
                                        "<b>Test 23:</b> EL 2.2 Logical Operator (&&) (Expected: true): true",
                                        "<b>Test 24:</b> EL 2.2 Logical Operator (and) (Expected: false): false",
                                        "<b>Test 25:</b> EL 2.2 Logical Operator (||) (Expected: true): true",
                                        "<b>Test 26:</b> EL 2.2 Logical Operator (||) (Expected: false): false",
                                        "<b>Test 27:</b> EL 2.2 Logical Operator (or) (Expected: true): true",
                                        "<b>Test 28:</b> EL 2.2 Logical Operator (!) (Expected: false): false",
                                        "<b>Test 29:</b> EL 2.2 Logical Operator (!) (Expected: true): true",
                                        "<b>Test 30:</b> EL 2.2 Logical Operator (not) (Expected: true): true",
                                        "<b>Test 31:</b> EL 2.2 Empty Operator (empty) (Expected: true): true",
                                        "<b>Test 32:</b> EL 2.2 Empty Operator (empty) (Expected: false): false",
                                        "<b>Test 33:</b> EL 2.2 Conditional Operator (A?B:C) (Expected: 2): 2",
                                        "<b>Test 34:</b> EL 2.2 Conditional Operator (A?B:C) (Expected: 3): 3" };

        this.verifyResponse(createWebBrowserForTestCase(), "/TestEL/EL22Operators.jsp", expectedInResponse);
    }

    /**
     * Test the new EL 3.0 operators in the context of a JSP.
     *
     * @throws Exception
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.  
    @Test
    public void testEL30Operators() throws Exception {
        // Each entry in the array is an expected output in the response
        String[] expectedInResponse = {
                                        "<b>Test 1:</b> EL 3.0 String Concatenation Operator (+=) with literals (Expected: xy): xy",
                                        "<b>Test 2:</b> EL 3.0 String Concatenation Operator (+=) with variables (Expected: 12): 12",
                                        "<b>Test 3:</b> EL 3.0 String Concatenation Operator with literals and multiple concatenations (Expected: xyz): xyz",
                                        "<b>Test 4:</b> EL 3.0 String Concatenation Operator with literals and single quotes  (Expected: xyz): xyz",
                                        "<b>Test 5:</b> EL 3.0 String Concatenation Operator with literals and mixed quotes  (Expected: xyz): xyz",
                                        "<b>Test 6:</b> EL 3.0 String Concatenation Operator with literals and escape characters  (Expected: \"x\"yz): \"x\"yz",
                                        "<b>Test 7:</b> EL 3.0 String Concatenation Operator with literals and escape characters  (Expected: 'x'yz): 'x'yz",
                                        "<b>Test 8:</b> EL 3.0 Assignment Operator (=) (Expected:3): 3",
                                        "<b>Test 9:</b> EL 3.0 Assignment Operator (=) (Expected:8): 8",
                                        "<b>Test 10:</b> EL 3.0 Assignment Operator (Expected:3): 3",
                                        "<b>Test 11:</b> EL 3.0 Semi-colon Operator (Expected:8): 8" };

        this.verifyResponse(createWebBrowserForTestCase(), "/TestEL/EL30Operators.jsp?testString1=1&testString2=2", expectedInResponse);
    }

    /**
     * Test the new EL 3.0 LambdaExpressions in the context of a JSP.
     *
     * @throws Exception
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    public void testEL30LambdaExpressions() throws Exception {
        // Each entry in the array is an expected output in the response
        String[] expectedInResponse = {
                                        "<b>LambdaParam_EL3.0_Test 1:</b> --> 9",
                                        "<b>RejectExtraLambdaParam_EL3.0_Test 2:</b> --> 5",
                                        "<b>MultipleLambdaParams_EL3.0_Test 3:</b> --> 17",
                                        "<b>CatchExeptionOnLessParam_EL3.0_Test 4:</b> --> Pass another argument. Only [1] arguments were provided for a lambda expression that requires at least [2]",
                                        "<b>AssignedLambdaExp_EL3.0_Test 5:</b> --> 9",
                                        "<b>NoParam_EL3.0_Test 6:</b> --> 64",
                                        "<b>OptionalParenthesis_EL3.0_Test 7:</b> --> 64",
                                        "<b>PrintFromBody_EL3.0_Test 8:</b> -->",
                                        "<b>ParameterCocerceToString_EL3.0_Test 9:</b> --> -1",
                                        "<b>ParameterCocerceToInt_EL3.0_Test 10:</b> --> 0",
                                        "<b>InvokeFunctionIndirect_EL3.0_Test 11:</b> --> 11",
                                        "<b>InvokeFunctionIndirect2_EL3.0_Test 12:</b> --> 120",
                                        "<b>PassedAsArgumentToMethod_EL3.0_Test 13:</b> -->  Charlie NAME MATCHES: Charlie;",
                                        "<b>Nested1_EL3.0_Test 14:</b> --> 12",
                                        "<b>Nested2_EL3.0_Test 15:</b> --> 2468"
        };
        this.verifyResponse(createWebBrowserForTestCase(), "/TestEL/EL30Lambda.jsp", expectedInResponse);
    }

    /**
     * Test EL 3.0 invocations of Method Expressions in a JSP
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    public void testMethodExpressionInvocations() throws Exception {
        WebBrowser browser = this.createWebBrowserForTestCase();
        this.verifyResponse(browser, "/TestEL/EL30InvocationMethodExpressions.jsp",
                            new String[] { "Get Parent Name Using Value Expression (Expected: \"John Smith Sr.\"): John Smith Sr.",
                                           "Get Child Name Using Value Expression (Expected: \"John Smith Jr.\"): John Smith Jr.",
                                           "Get Object Representation Using Value Expression: toString method of object with current parent name John Smith Sr.",
                                           "Get Parent Name Using Method Expression (Expected: \"Steven Johnson Sr.\"): Steven Johnson Sr.",
                                           "Get Child Name Using Method Expression (Expected: \"Steven Johnson Jr.\"): Steven Johnson Jr.",
                                           "Get Object Representation Using Method Expression: toString method of object with current parent name Steven Johnson Sr."
                            });
    }

    /**
     * Test EL 3.0 Operator Precedence in a JSP
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    @Mode(TestMode.FULL)
    public void testOperatorPrecedence() throws Exception {
        WebBrowser browser = this.createWebBrowserForTestCase();
        this.verifyResponse(browser, "/TestEL/EL30OperatorPrecedences.jsp",
                            new String[] { "<b>Test 1:</b> EL 3.0 [] and . operators left-to-right (Expected:true): true",
                                           "<b>Test 2:</b> EL 3.0 [] and . operators left-to-right (Expected:true): true",
                                           "<b>Test 3:</b> EL 3.0 Parenthesis Operator with - (unary) (Expected:-14): -14",
                                           "<b>Test 4:</b> EL 3.0 Parenthesis Operator with - (unary) (Expected:-10): -10",
                                           "<b>Test 5:</b> EL 3.0 not ! empty operators left-to-right (Expected:true): true",
                                           "<b>Test 6:</b> EL 3.0 Parenthesis Operator with not ! empty operators (Expected:true): true",
                                           "<b>Test 7:</b> EL 3.0 Parenthesis Operator with not ! empty operators (Expected:false): false",
                                           "<b>Test 8:</b> EL 3.0 * / div % mod operators left-to-right (Expected:1.0): 1.0",
                                           "<b>Test 9:</b> EL 3.0 Parenthesis Operator with * / div % mod operators (Expected:16.0): 16.0",
                                           "<b>Test 10:</b> EL 3.0 + - operators left-to-right (Expected:5): 5",
                                           "<b>Test 11:</b> EL 3.0 + - * / div operators (Expected:31.0): 31.0",
                                           "<b>Test 12:</b> EL 3.0 Parenthesis Operator with + - * / div operators (Expected:45.0): 45.0",
                                           "<b>Test 13:</b> EL 3.0 String Concatenation Operator (+=) and + operator (Expected:3abc): 3abc",
                                           "<b>Test 14:</b> EL 3.0 < > <= >= lt gt le ge relational operators left-to-right (Expected:true): true",
                                           "<b>Test 15:</b> EL 3.0 < > relational operators with + - operators (Expected:false): false",
                                           "<b>Test 16:</b> EL 3.0 == != eq ne relational operators left-to-right (Expected:true): true",
                                           "<b>Test 17:</b> EL 3.0 == and <= relational operators (Expected:true): true",
                                           "<b>Test 18:</b> EL 3.0 != and > relational operators (Expected:false): false",
                                           "<b>Test 19:</b> EL 3.0 && and || logical operators (Expected:true): true",
                                           "<b>Test 20:</b> EL 3.0 and or logical operators (Expected:true): true",
                                           "<b>Test 21:</b> EL 3.0 ? and : conditional operators (Expected:2): 2",
                                           "<b>Test 22:</b> EL 3.0 ? and : conditional operators (Expected:3): 3",
                                           "<b>Test 23:</b> EL 3.0 -> (lambda) operator (Expected:60): 60",
                                           "<b>Test 24:</b> EL 3.0 Assignment (=) and Semi-colon (;) operators with concatenation operator (+=) (Expected:13): 13",
                                           "<b>Test 25:</b> EL 3.0 Assignment (=) and Semi-colon (;) operators with lambda operator (->) (Expected:5): 5",
                                           "<b>Test 26:</b> EL 3.0 Assignment (=) and Semi-colon (;) operators with lambda operator (->) (Expected:11): 11"
                            });
    }

    /**
     * Test EL 3.0 Coercion Rules in a JSP
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    public void testEL30CoercionRules() throws Exception {
        WebBrowser browser = this.createWebBrowserForTestCase();
        this.verifyResponse(browser, "/TestEL/EL30CoercionRules.jsp",
                            new String[] { "Testing Coercion of a Value X to Type Y.",
                                           "Test if X is null and Y is not a primitive type and also not a String, return null (Expected:true): true"
                            });
    }

    /**
     * Test EL 3.0 List Operations on Collection Objects in a JSP
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    @Mode(TestMode.FULL)
    public void testEL30ListCollectionObjectOperations() throws Exception {
        // Use the SharedServer to verify a response.
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=filter",
                            "Filter: [4, 3, 5, 3]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=map",
                            "Map: [3, 6, 5, 4, 7, 5, 3]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=flatMap",
                            "FlatMap: [1, 4, 3, 2, 5, 3, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=distinct",
                            "Distinct: [1, 4, 3, 2, 5]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=sorted",
                            "Sorted in Decreasing: [5, 4, 3, 3, 2, 1, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=forEach",
                            "ForEach: 1432531");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=peek",
                            "Debug Peek: 144322531[4, 2]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=iterator",
                            "Iterator: 1 4 3 2 5 3 1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=limit",
                            "Limit: [1, 4, 3]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=substream",
                            "Substream: [3, 2]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=toArray",
                            "ToArray: [1, 4, 3, 2, 5, 3, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=toList",
                            "ToList: [1, 4, 3, 2, 5, 3, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=reduce",
                            "Reduce: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=max",
                            "Max: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=min",
                            "Min: 1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=average",
                            "Average: 2.7142857142857144");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=sum",
                            "Sum: 19");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=count",
                            "Count: 7");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=anyMatch",
                            "AnyMatch: true");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=allMatch",
                            "AllMatch: false");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=noneMatch",
                            "NoneMatch: true");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testListCollectionOperations=findFirst",
                            "FindFirst: 1");
    }

    /**
     * Test EL 3.0 Set Operations on Collection Objects in a JSP
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    public void testEL30SetCollectionObjectOperations() throws Exception {
        // Use the SharedServer to verify a response.
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=filter",
                            "Filter:", "3", "5", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=map",
                            "Map:", "5", "4", "3", "7", "6");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=flatMap",
                            "FlatMap:", "3", "3", "1", "5", "2", "1", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=distinct",
                            "Distinct:", "3", "2", "1", "5", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=sorted",
                            "Sorted in Decreasing:", "5", "4", "3", "2", "1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=forEach",
                            "ForEach:", "3", "2", "1", "5", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=peek",
                            "Debug Peek:", "3", "2", "2", "1", "5", "4", "4", "[2, 4]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=iterator",
                            "Iterator:", "3", "2", "1", "5", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=limit",
                            "Limit:", "3", "2", "1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=substream",
                            "Substream:", "3", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=toArray",
                            "ToArray:", "3", "2", "1", "5", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=toList",
                            "ToList:", "3", "2", "1", "5", "4");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=reduce",
                            "Reduce: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=max",
                            "Max: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=min",
                            "Min: 1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=average",
                            "Average: 3.0");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=sum",
                            "Sum: 15");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=count",
                            "Count: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=anyMatch",
                            "AnyMatch: true");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=allMatch",
                            "AllMatch: false");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=noneMatch",
                            "NoneMatch: true");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testSetCollectionOperations=findFirst",
                            "FindFirst:", "1");
    }

    /**
     * Test EL 3.0 Map Operations on Collection Objects in a JSP
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @Test
    @Mode(TestMode.FULL)
    public void testEL30MapCollectionObjectOperations() throws Exception {
        // Use the SharedServer to verify a response.
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=filter",
                            "Filter: [4, 3, 5, 3]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=map",
                            "Map: [3, 6, 5, 4, 7, 5, 3]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=flatMap",
                            "FlatMap: [1, 4, 3, 2, 5, 3, 1, 1, 4, 3, 2, 5, 3, 1, 1, 4, 3, 2, 5, 3, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=distinct",
                            "Distinct: [1, 4, 3, 2, 5]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=sorted",
                            "Sorted in Decreasing: [5, 4, 3, 3, 2, 1, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=forEach",
                            "ForEach: 1432531");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=peek",
                            "Debug Peek: 144322531[4, 2]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=iterator",
                            "Iterator: 1 4 3 2 5 3 1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=limit",
                            "Limit: [1, 4, 3]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=substream",
                            "Substream: [3, 2]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=toArray",
                            "ToArray: [1, 4, 3, 2, 5, 3, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=toList",
                            "ToList: [1, 4, 3, 2, 5, 3, 1]");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=reduce",
                            "Reduce: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=max",
                            "Max: 5");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=min",
                            "Min: 1");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=average",
                            "Average: 2.7142857142857144");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=sum",
                            "Sum: 19");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=count",
                            "Count: 7");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=anyMatch",
                            "AnyMatch: true");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=allMatch",
                            "AllMatch: false");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=noneMatch",
                            "NoneMatch: true");
        this.verifyResponse("/TestEL/EL30CollectionObjectOperations.jsp?testMapCollectionOperations=findFirst",
                            "FindFirst: 1");
    }


    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.fat.util.LoggingTest#getSharedServer()
     */
    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }

}