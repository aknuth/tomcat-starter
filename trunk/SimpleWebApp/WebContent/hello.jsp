<%@ page import="java.util.*" session="false" pageEncoding="UTF-8"%>
<HTML>
<BODY>
<%
    System.out.println( "Evaluating date now" );
    Date date = new Date();
%>
Hello!  The time is now <%= date %>
</BODY>
</HTML>