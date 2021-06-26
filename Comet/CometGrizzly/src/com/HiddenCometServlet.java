
    package com;
    
    //TUTORIAL ORACLE: https://javaee.github.io/grizzly/comet.html
    //EJEMPLO GRIZZLY OFICIAL: https://docs.oracle.com/cd/E18930_01/html/821-2418/ggrgt.html
    //LIBRERÍAS GRIZZLY: http://grepcode.com/snapshot/maven.java.net/content/groups/promoted/org.glassfish.main.grizzly/glassfish-grizzly-extra-all/4.0-b77
    
    //***ANÁLISIS EN EL FONDO DE ESTE FICHERO
    //AQUÍ USAMOS GRIZZLY, NO COMETD(BAYEUX-PROTOCOL), Y TIPO DE CONEXIÓN LONG-POLLING, PERO SE EXPLICA QUÉ LINEAS QUITAR (SON 2) PARA QUE SEA HTTP-STREAMING

    import java.io.IOException;
    import java.io.PrintWriter;
    import javax.servlet.ServletConfig;
    import javax.servlet.ServletContext;
    import javax.servlet.http.HttpServlet;
    import javax.servlet.ServletException;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    
    /*VIEJA LIBRERÍA
    import com.sun.grizzly.comet.CometContext;
    import com.sun.grizzly.comet.CometEngine;
    import com.sun.grizzly.comet.CometEvent;
    import com.sun.grizzly.comet.CometHandler;*/

    import org.glassfish.grizzly.comet.CometContext;
    import org.glassfish.grizzly.comet.CometEngine;
    import org.glassfish.grizzly.comet.CometEvent;
    import org.glassfish.grizzly.comet.CometHandler;
    import org.glassfish.grizzly.http.server.Response;
    
    import java.io.IOException;
    import java.io.PrintWriter;
    import java.util.concurrent.atomic.AtomicInteger;
    import java.util.logging.Level;
    import java.util.logging.Logger;


    public class HiddenCometServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;
        private String contextPath = null;
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            
            ServletContext context = config.getServletContext();//OBTENEMOS EL CONTEXT
            contextPath = context.getContextPath() + "/hidden_comet";//OBTENEMOS EL CONTEXT PATH Y MODIFICAMOS (/Test_comet es el ctxpath)

            //AL CARGAR EL SERVLET POR PRIMERA VEZ, se registra un shared space con el nombre de esta app(su url de acceso)
                //(shared por las instancias de CometHandler que son los threads de solicitudes; existe un thread pool)
            CometEngine engine = CometEngine.getEngine();
            CometContext cometContext = engine.register(contextPath);//REGISTRAMOS EN EL COMET ENGINE UN NOMBRE (usamos la url por conveniencia)
            cometContext.setExpirationDelay(5 * 1000);//expirationDelay is the long delay before a request is resumed. -1 means never.
        }

        /**
         * Handles the HTTP <code>GET</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //GET LO USAN LOS CLIENTES QUE CONSUMEN, QUE SE REGISTRAN, AL CARGAR LA PÁGINA SIMPLEMENTE
        //cuando un cliente carga la pág se carga este método, se crea instancia del handler y se añade a la lista del cometcontext, el perteneciente a esta app que es un servlet
            System.out.println("doGet");
            CounterHandler handler = new CounterHandler();//MI CLASE MANEJADORA DE COMET QUE EXTIENDE DE COMETHANDLER
            handler.attach(response);//LE ENVIAMOS EL OBJETO HttpServletResponse para que escriba
                    //esto ya no es necesario puesto que se llama automaticamente al método getResponse y setResponse del CometHandler con un objeto donde escribirle

            CometEngine engine = CometEngine.getEngine();
            CometContext<HttpServletResponse> context = engine.getCometContext(contextPath);//BUSCA EL COMET CONTEXT REGISTRADO ANTERIORMENTE EN INIT
            context.addCometHandler(handler);//AÑADE INSTANCIA DE MI COMET HANDLER AL COMET CONTEXT(tiene un thread pool que gestiona)

            System.out.println("instancias: "+context.getCometHandlers().size());
        }

        /**
         * Handles the HTTP <code>POST</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //POST LO USA EL CLIENTE QUE MODIFICA EL CONTADOR CON EL BOTÓN
            System.out.println("doPost");
            counter.incrementAndGet();//es atomic integer, lo incrementamos

            CometEngine engine = CometEngine.getEngine();
            CometContext<HttpServletResponse> context = engine.getCometContext(contextPath);
            context.notify(null);//esto hace que continúe su curso llamando a onEvent del CometHandler
                //Here, what we’re doing is notifying the entire context of an event. This will cause onEvent() to be called for each registered CometHandler
            
            //request.getRequestDispatcher("button.html").forward(request, response);
                    //reenvía la solicitud al resource button.html, el response puede haber sido analizado y modificado en este servlet y se reenvía con los cambios
            request.getRequestDispatcher("button.html").include(request, response);
                    //esto incluye la salida html de este servlet y del otro también.. en este incluímos salida con response.getWriter().write("");
        }

        
        
        
        //-------------------------------------------------- CLASE INTERNA

        private class CounterHandler implements CometHandler<HttpServletResponse> {

            private HttpServletResponse response;//PARA RECIBIR EL OBJETO HttpServletResponse DEL SERVLET
            private CometContext<HttpServletResponse> cometContext;
            private org.glassfish.grizzly.http.server.Response responseWrapperCoyote;//este objeto es un wrapper de coyote response, así dice la doc

            @Override
            public void onInitialize(CometEvent event) throws IOException {}

            @Override
            public void onInterrupt(CometEvent event) throws IOException {
                System.out.println("onInterrupt");
                //removeThisFromContext();
                this.cometContext.setExpirationDelay(5 * 1000);//añado más tiempo. El ejemplo original llamaba a removeThisFromContext
            }

            @Override
            public void onTerminate(CometEvent event) throws IOException {
                System.out.println("onTerminate");
                removeThisFromContext();
            }

            private void removeThisFromContext() throws IOException {//cuando se interrumpe o se termina la conexión en cliente, se llama a onInterrupt o onTerminate
                System.out.println("removeThisFromContext");
                response.getWriter().close();
                CometContext context = CometEngine.getEngine().getCometContext(contextPath);
                context.removeCometHandler(this);//DEREGISTRAMOS EL COMET HANDLER DEL COMET CONTEXT
            }

            public void attach(HttpServletResponse attachment) {
                this.response = attachment;
            }

            @Override
            public void onEvent(CometEvent event) throws IOException {
            //ES LLAMADO CUANDO HACEN NOTIFY ARRIBA
                // If the event type is NOTIFY, the onEvent method gets the updated count (un contador del ejemplo), and writes out JavaScript to the client.         
                if (CometEvent.Type.NOTIFY == event.getType()) {                   
                    int count = counter.get();
                    System.out.println("onEvent");
                    PrintWriter writer = response.getWriter();
                        //write difiere de print en que este suprime io exceptions o algo similar
                        //esta es la llamada a la function declarada en count.html
                            //SE LE RETORNA A BUTTON(es quien hizo la solicitud post) ESTE SCRIPT, QUE LLAMA AL ELEMENTO CON NAME=counter DEL PARENT(es decir index.html) Y A SU  FUNCTION updateCount
                    writer.write("<script type='text/javascript'>" + 
                        "parent.counter.updateCount('" + count + "')" +
                        "</script>\n");
                    writer.flush();

                    //++++USAR long-polling o http-streaming technique
                    //The last line resumes the Comet request and removes it from the list of active CometHandler objects.      
                    //By this line, you can tell that this application uses the long-polling technique.       
                    //If you were to delete this line, the application would be using the HTTP-Streaming technique.
                    //You don't include this line because you do not want to resume the request. Instead, you want the connection to remain open.
                    //nótese que no se reinicia la solicitud desde cliente (como en mi Ajax prolongado) sino desde el mismo servidor
                    event.getCometContext().resumeCometHandler(this);//más rápido llamarlo desde event (si lo tengo) que hacer de nuevo getEngine y getCometContext
                }
            }

            @Override
            public org.glassfish.grizzly.http.server.Response getResponse() {
                System.out.println("getResponse");
                return responseWrapperCoyote;
            }

            @Override
            public void setResponse(org.glassfish.grizzly.http.server.Response rspns) {
                System.out.println("setResponse");
                //ESTA RESPUESTA SE ENVÍA AL OBJETO QUE SE QUEDÓ ESPERANDO, AL QUE LLAMÓ EL doGet
                //LA RESPUESTA SE ENVÍA (con getResponse) CUANDO ES INTERRUMPIDO, ENTONCES DEBERÍA USARSE EL INTERRUPT CUANDO HAYA RESPUESTA
                    //el HttpServletResponse es del tutorial original, método viejo, tenía que pasarse por parámetro
                    //el Response de este método es el nuevo mecanismo
                    //rspns.getWriter().write("HOLA desde setResponse");
                    //response.getWriter().write("HOLA USANDO HttpServletResponse");
                
                this.responseWrapperCoyote = rspns;
            }

            @Override
            public CometContext<HttpServletResponse> getCometContext() {
                System.out.println("getCometContext");
                return this.cometContext;
            }

            @Override
            public void setCometContext(CometContext<HttpServletResponse> cc) {
                System.out.println("setCometContext");
                this.cometContext = cc;
            }

        }

    }

    //--------------------------------------------------
        /*
    FLUJO:
        When you run the example, the following happens:

        The index.html page opens.
        The browser loads three frames: The first one accesses the servlet using an HTTP GET; the second one loads the count.html page, which displays the current count; and the third one loads the button.html page, which is used to send the POST request.
        After clicking the button on the button.html page, the page submits a POST request to the servlet.
        The doPost method calls the onEvent method of the Comet handler and redirects the incremented count along with some JavaScript to the count.html page on the client.
        The updateCount() JavaScript function on the count.html page updates the counter on the page.
        Because this example uses long-polling, the JavaScript code on count.html calls doGet again to resume the connection after the servlet pushes the update.
    
Si es solicitud doGet:
    doGet, setResponse, setCometContext, elDelayEstablecido, onInterrupt, getResponse, onTerminate.

Si es Post
    doPost,onEvent, getCometContext, getResponse

VISTO DESDE EL ANALIZADOR DE TRÁFICO DE RED FIREFOX:
    1.se carga index y este carga 
        button, 
        counter, 
        hidden(el servlet, con un get request)... queda a la espera de respuesta del servidor *
                2.button (en clic) carga 
                    hidden(servlet, pero con post esta vez)...se cargaría la gui de hidden(no tiene),pero..
                    se procesa la solicitud y se le retorna (a button) no un hidden, sino otro button en el cuerpo de la respuesta... es decir se carga un button pero técnicamente la página está en hidden ó se incluye dicho button en el hidden, como un include
                    junto a esa carga de hidden, cuya respuesta contiene un button, se redirigío req y response, donde response es la respuesta al hidden esperando del paso 1
                    esta respuesta es un llamado a count, el cual vuelve a recargar un hidden con get y queda nuevamente en espera ese hidden como en paso 1.
*/