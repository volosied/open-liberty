<!--
    Copyright (c) 2023 IBM Corporation and others.
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License 2.0
    which accompanies this distribution, and is available at
    http://www.eclipse.org/legal/epl-2.0/
    
    SPDX-License-Identifier: EPL-2.0
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="test1" uri="/WEB-INF/tld/test1.tld"%>
<%@ taglib prefix="test2" uri="/WEB-INF/tld/test2.tld"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PI44611</title>
</head>
<body>

<test1:Sample>WEB-INF/tld/test1.tld works!</test1:Sample>
<test2:Sample>WEB-INF/tld/test2.tld works!</test2:Sample>

</body>
</html>
