<%-- 
    Document   : home
    Created on : 10-mar-2018, 16:27:40
    Author     : Leo
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page session="true"%><!-- aunque true es por defecto, lo ponemos como recordatorio -->
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Calendar"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false" isThreadSafe="true" language="java"%><!--notar que puedo poner varios page atributes en una sola declaración! SON PARA ESTE JSP (home.jsp)-->
<!--EL es expression language, no queremos desabilitarlo pq JSP lo necesita o algo así-->
<!--isThreadSafe es que pueden acceder concurrentemente a este servlet, pero quizás deba usar bloques synchronized, true by default-->

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Test</title>
    </head>
    <body>
        <h1>PRÁCTICA DE JSP</h1>
        
            <script>
                function modreloj(){
                    var fecha= new Date();            
                    var horas= fecha.getHours();
                    var minutos = fecha.getMinutes();
                    var segundos = fecha.getSeconds();
                    document.getElementById('reloj').innerHTML=''+horas+':'+minutos+':'+segundos+'';
                    setTimeout('modreloj()',1000);
                }
                //otra alternativa:
                //var interval = setInterval( function(){ modreloj(); }, 1000 );//clearInterval(interval) to kill thread
            </script>   
            <body onload="modreloj()">
            <div id="reloj"></div>
        
        <%
            System.out.println("Esto va a consola pq está en un scriptlet");
            Calendar cal = Calendar.getInstance(); //este Calendar se instancia al cargar la página web, cambia si recargamos la pág
            out.println("Fecha actual: ");
            out.println(cal.getTime().toString());
            //NO SE DECLARAN MÉTODOS EN SCRIPTLETS
        %>
        <br>Fecha actual: <%= cal.getTime().toString() %>
        
        <%! Calendar cale = Calendar.getInstance(); //este Calendar se instancia al compilar jsp en el servidor, NO CAMBIARÁ SI RECARGAS LA PÁG WEB, ES ESTÁTICO.
            public String getDate() {
                SimpleDateFormat df = new SimpleDateFormat("YYYY-mm-dd HH:mm:ss");
                return "<br>"+df.format(cale.getTime()); //si llamamos creamos aquí en el método la variable, sí retorna nueva fecha al cargar pág
            }
        %>
        <br>ESTE NO CAMBIA DESDE QUE SE COMPILÓ EL JSP: <%= getDate() %></br>
        ---------------------------------------------------------------------------
        <p>ALGUNOS REQUEST VALUES (info en Headers, Query y Body de la solicitud)
        <%-- ESTO LANZÁ ERROR!-> <%= System.out.println("hola") %> --%>
        <br>contextPath: <%= request.getContextPath() %>
        <br>pathInfo: <%= request.getPathInfo() %>
        <br>HttpMethod: <%= request.getMethod() %>
        <br>queryString: <%= request.getQueryString() %>
        <br>remoteUser: <%= request.getRemoteUser() %>
        <br>remostHost: <%= request.getRemoteHost() %>
        <br>requestURI: <%= request.getRequestURI() %>
        <br>requestedSessionId: <%= request.getRequestedSessionId() %>
        <br>servletPath: <%= request.getServletPath() %>
        <br>requestUrl: <%= request.getRequestURL() %>
        <br>Y MUCHO MÁS QUE PODEMOS OBTENER CON REQUEST...</br>
        ---------------------------------------------------------------------------
        <br>ESTO NO ES DE JSP, ES PROPERTIES DE JAVA, PERO USAMOS UN SCRIPLET PARA PROBAR CÓDIGO JAVA:</br>
        <details>
        <summary><font size="4" style="color: #ff6e00">SYSTEM PROPERTIES AND ELEMENTS (key --> value):</font></summary>
        <%
            Enumeration<?> propNames = System.getProperties().propertyNames();
                //Set<String> pn = System.getProperties().stringPropertyNames();
                    //Iterator<String> iter = pn.iterator();
                //Enumeration<?> elem = System.getProperties().elements(); //son el value de cada property
            while(propNames.hasMoreElements()){
                String key = propNames.nextElement().toString(); %>
                <details>
                    <summary><%= key %></summary>
                    <%= System.getProperty(key) %>
                </details>
            <% } %>
        </details>
        ---------------------------------------------------------------------------
        <br>SESSIONS:</br>
        <%
            int tSeg = 100;
            session.setMaxInactiveInterval(tSeg);
            out.println("Session creation time: "+session.getCreationTime());
            session.setAttribute("usuario", new Integer(18942078));//seteamos un atributo en el scope session
        %>
        
        <%
            String person="Leo";//abajo puedo usar value="<%=person% >"
            request.setAttribute("persona", person);//seteamos un atributo en el scope request
        %>
        <!-- OJO: abajo usamos un parameter, estos trabajan con texto solamente -->
        <jsp:include page="hello.jsp">
            <jsp:param name="nombre" value="${persona}"/>
        </jsp:include>
        <br>
        ---------------------------------------------------------------------------
        <p>ENVIAMOS DATOS DE FORM, GUARDAMOS EN BEAN Y REUSAMOS EL BEAN (todo con useBean y setProperty en el fichero target):
        <form method="post" action="mostrarUserData.jsp">
            <br>Nombre usuario: <input name="nombre" type="text" size="20">
            <br>Edad usuario: <input name="edad" type="text" size="5">
            <br>E-mail usuario: <input name="email" type="text" size="20">
            <br><input type="submit">
        </form>
        ---------------------------------------------------------------------------
        <p>NOTA DE ANÁLISIS SOBRE USO DE FRAMEWORKS:</p>
                ¡ guardó respuesta de usuario desde form en un bean y según valor usó response.sendRedirect("página_elegida"); !
                Some frameworks use much more complicated mechanisms to do something as simple as this. For example, these complicated mechanisms may require configuration files which have to be changed for every change in call-flow, and may involve Java classes with particular complicated rules.
                But an objective review reveals that there is no actual advantage to these over-complicated mechanisms. They merely introduce many more places where things can and do go wrong.
                Sometimes, the desire to use these more complicated mechanisms arises from a misunderstanding - that JSPs are meant for display, therefore they can never be used when their output is not of interest. This is not correct.
                It is true that JSPs are primarily HTML with embedded code. However, they have many advantages, such as requiring less (typically none) configuration, and being easy to modify and deploy. Therefore they can lend themselves to other usages, such as writing simple and straightforward controllers. Ultimately, JSPs are just one tool in your toolbox. You should look at and evaluate each tool objectively, and put them to best advantage. That means using JSPs as controllers, as long as they provide the simplest and most effective means of doing that.
                This is even more important for large projects. Large projects tend to already have a lot of complexity. Adding more complexity via complicated frameworks is not recommended. As seasoned and successful veterans of large projects know, it is specially important in large projects to avoid complexity and find simple and elegant solutions. 
                <br>
        ---------------------------------------------------------------------------
        <p>USO DE TAGLIB PERSONAL:</p>       
        <%@ taglib uri="/WEB-INF/tlds/libreria" prefix="libreria"%>
        <libreria:MiEtiqueta>BODY DE LA ETIQUETA</libreria:MiEtiqueta>
        
        
    </body>
</html>
