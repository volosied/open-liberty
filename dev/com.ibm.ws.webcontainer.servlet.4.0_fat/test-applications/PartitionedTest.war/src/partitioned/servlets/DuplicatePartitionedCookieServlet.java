/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package partitioned.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
@WebServlet(urlPatterns = "/TestDuplicatePartitionedCookie")
public class DuplicatePartitionedCookieServlet extends HttpServlet {
        /**  */
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            PrintWriter pw = resp.getWriter();
            pw.print("Welcome to the TestDuplicatePartitionedCookieServlet!");

            // String testName = req.getParameter("testName");

            resp.setHeader("Set-Cookie", "PartitionedCookieName_SetHeader=PartitionedCookieValue_SetHeader; SameSite=None; Partitioned");
            resp.addHeader("Set-Cookie", "PartitionedCookieName_AddHeader=PartitionedCookieValue_AddHeader; SameSite=None; Partitioned");
            
            //Should be tested on Servlet 6 via addAttribute
            // Cookie cookie = new Cookie("PartitionedCookieName_AddCookie","PartitionedCookieValue_AddCookie");
            // resp.addCookie(cookie);

            // switch(testName){
            //     case "testPartitionedCookie_Basic":
            //         resp.setHeader("Set-Cookie", "PartitionedCookieName_SetHeader=PartitionedCookieValue_SetHeader;");
            //         resp.addHeader("Set-Cookie", "PartitionedCookieName_AddHeader=PartitionedCookieValue_AddHeader;");
                    
            //         cookie = new Cookie("PartitionedCookieName_AddCookie","PartitionedCookieValue_AddCookie");
            //         resp.addCookie(cookie);
            //         break;
            //     case "testParitionedNotSetOnLax":
            //         resp.setHeader("Set-Cookie", "PartitionedCookieName_SetHeader=PartitionedCookieValue_SetHeader;");
            //         resp.addHeader("Set-Cookie", "PartitionedCookieName_AddHeader=PartitionedCookieValue_AddHeader;");
                    
            //         cookie = new Cookie("PartitionedCookieName_AddCookie","PartitionedCookieValue_AddCookie");
            //         resp.addCookie(cookie);
            //     break;
            //     default:
            //         throw new ServletException("No test specified via the testName query!");
            // }

        }
    
}
