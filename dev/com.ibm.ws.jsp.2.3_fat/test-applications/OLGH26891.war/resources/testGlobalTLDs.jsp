<!--
    Copyright (c) 2015 IBM Corporation and others.
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License 2.0
    which accompanies this distribution, and is available at
    http://www.eclipse.org/legal/epl-2.0/
    
    SPDX-License-Identifier: EPL-2.0
   
    Contributors:
        IBM Corporation - initial API and implementation
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="original" uri="io.test.one.tld"%>
<%@ taglib prefix="test1" uri="/WEB-INF/tld/test1.tld"%>
<%@ taglib prefix="test2" uri="/WEB-INF/tld/test2.tld"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PI44611</title>
</head>
<body>

<original:Sample>Original Works!</original:Sample>
<original:Sample>configureWithBothURIs works!</original:Sample>
<original:Sample>forceCustomURI works!</original:Sample>

</body>
</html>