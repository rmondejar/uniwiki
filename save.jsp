<%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, javax.sql.*, javax.naming.*"%>
<jsp:useBean id="wiki" class="uniwiki.bean.WikiBean" scope="application"/>
<html>
<head>
<title>Uniwiki</title>
</head>
<%
String pageName = (String) request.getParameter("page");
String content = (String) request.getParameter("content");
wiki.save(pageName,content);
response.sendRedirect("index.jsp");
%>
</body>
</html>