<%-- 
    Document   : mostrarUserData
    Created on : 11-mar-2018, 13:07:04
    Author     : Leo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        
        <P>ALMACENA DATOS FORMULARIO EN UN BEAN Y LUEGO LOS USA, PUEDE SER EN OTRA PÁGINA, NO TIENE QUE SER ESTA MISMA.
        <br>con setProperty, guardamos los datos de request en los atributos del bean con el mismo nombre!
        <jsp:useBean id="user" class="beans.UserData" scope="session"/>
            <jsp:setProperty name="user" property="*"/>
            
            
            <p>AHORA ACCEDEMOS:</p>
        <br>Tu nombre: <%= user.getNombre()%>
        <br>Tu mail: <%= user.getEmail()%>
        <br>Tu edad: <%= user.getEdad()%>
        
    </body>
</html>
