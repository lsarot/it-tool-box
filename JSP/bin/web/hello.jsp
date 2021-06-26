<%-- 
    Document   : hello
    Created on : 10-mar-2018, 20:05:49
    Author     : Leo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Hello page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%
            out.println("Hello " + request.getParameter("nombre"));
            out.println("<br>Session (usuario): "+session.getAttribute("usuario"));//getAttribute devuelve Object, pero al estar entre texto usa el toString
        %>
    </body>
</html>
