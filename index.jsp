<%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, javax.sql.*, javax.naming.*"%>
<jsp:useBean id="wiki" class="uniwiki.bean.WikiBean" scope="application"/>
<html>
<head>
<title>Uniwiki</title>
<style type="text/css">
          @import url("css/tigris.css");
          @import url("css/maven.css");
</style>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Language" content="en-gb">
</head>

      <body class="composite">

        <div id="banner">

          <table border="1" cellpadding="8" cellspacing="0" width="100%">

            <tbody><tr>

              <td style="text-align: center; vertical-align: middle;">

                  <p style="text-align: center;"><i><font size="7">
					<a href="index.html">UniWiki</a></font></i></p>

              </td>

            </tr>

          </tbody></table>

        </div>

        <div id="breadcrumbs">

          <table border="0" cellpadding="4" cellspacing="0" width="12%">
          <form method="post" action="index.jsp">

            <tbody><tr>

            <td>
				<p style="text-align: center"> <u><font size="2">New Page</font></u></p>
				<p style="text-align: center">

			<font size="2">

		<textarea name="page" rows="1" cols="10"></textarea>
		<input type="submit" name="create" value="Create" />
		
		</td>

            </tr>

          </tbody>
          </form>
          </table>

        </div>

        <table border="0" cellpadding="8" cellspacing="0" width="100%"> 

          <tbody><tr valign="top">

            <td id="leftcol" width="126">

              <div id="navcolumn">

    <div>

      <p style="margin-top: 0pt; margin-bottom: 0pt;">

      <strong><span lang="es">Pages</span></strong></p>
		<p style="margin-top: 0pt; margin-bottom: 0pt;">

      <font size="2"></p>

		<p style="margin-top: 0pt; margin-bottom: 0pt;">
		
<% for (String s : wiki.list()) { %>
		
		<small><a href="index.jsp?page=<%=s%>">- <%=s%></a></small><br>
		
<% } %>
	  	
	  	</p>

		</div>

  </div>



            </td>

            <td rowspan="2">

              <div id="bodycol">

                <div class="app">

<%
String pageName = (String) request.getParameter("page");
if (pageName!=null) {

  String content = wiki.load(pageName);
  if (content==null) content = "";

%>
    <div class="h3">

        <h3>

          <a name="Description"><span style="background-repeat: repeat">
			<font size="2"><%=pageName%></font></span></a></h3>

			<font size="2">

		<blockquote>

<p>

<div id="content">
	<form method="post" action="save.jsp">
		<input type="hidden" name="page" value="<%=pageName%>" />
		<p style="text-align: center">
		<textarea name="content" style="width:100%; min-width:600px;" rows="15" cols="15"><%=content%></textarea>
		<input type="submit" name="edit" value="Save" style="float: right" />
		</p>
	</form>
</div>

<%
}
%>

</p></blockquote>

<font size="2">		</font><p></p>

<font size="2">    </font></div>

<font size="2">                </font></div>

<font size="2">              </font></div>

<font size="2">            </font></td>

          </tr>

          </tbody></table>

        <div id="footer">

          <table style="width: 100%;" border="0" cellpadding="4" cellspacing="0">

            <tbody><tr>

              <td>

          <table style="width: 100%;" id="table2" border="0" cellpadding="4" cellspacing="0">

            <tbody><tr>

              <td width="2">

              <td>

<font size="2">@author Rubén Mondéjar &lt;<a href="mailto:ruben.mondejar@urv.cat">ruben.mondejar@urv.cat</a>&gt;</font></td>

            </tr>

          </tbody></table>

        		</td>

            </tr>

          </tbody></table>

        </div>

      </body></html>