package com.example.rsocketclient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.example.rsocketclient.data.LogMsg;
import com.example.rsocketclient.data.Message;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ES UNA APP QUE FUNCIONA COMO UN SHELL
 * Los métodos con @ShellMethod serán registrados por su nombre
 * Si usan camelCase, serán mapeados a kebab-case 
 * help nos mostrará los métodos disponibles
 * */

/** NOTA:
 * ESTE PROYECTO (server y client - basic) CONTEMPLA TODO LO NECESARIO PARA LOS 4 MODELOS DE CONEXIÓN QUE PERMITE ROCKET, MÁS UN SERVER-TO-CLIENT REQUESTS EXAMPLE AL FINAL
 * EL OTRO PROYECTO (server y client) INCLUYE CAMBIOS PARA RSOCKET SECURITY
 * 		En lado cliente, cambia ligeramente unas líneas del constructor, aunque la mayor parte de la lógica del constructor se pasó al método login,
 * 		los métodos en cliente chequean si está logueado correctamente (en este proyecto están y funciona igual pq lo que hace realmente es revisar si se estableció la conexión!!!)
 * 		En lado servidor se registran unos beans para la parte de security, se recibe en cada método un UserDetails que representa al usuario logueado.
 * 
 * 
 * NOTA:
 * Note: At the time of writing (june2020), RSocket’s security extensions are still a work in progress. You can follow their progress here. In this exercise, we’ll be using Simple Authentication which carries the warning: “Simple Authentication transmits the username and password in clear text.
 * Additionally, it does not protect the authenticity or confidentiality of the payload that is transmitted along with it. This means that the Transport that is used should provide both authenticity and confidentiality to protect both the username and password and corresponding payload.”
 * 
 * 
 * NOTA:
 * ACTIVAR 1 DE LOS CONSTRUCTORES SOLAMENTE
 * EL BASIC CONSTRUCTOR ES SUFICIENTE, el otro inyecta más elementos que permiten usar server-to-client y security
 * 
 * 
 * NOTA:
 * UN PROBLEMA CON LOS QUE USAN FLUX (stream y channel) ES QUE NO SÉ CÓMO ALIMENTAR UN FLUJO DINAMICAMENTE.
 * HASTA EL MOMENTO HEMOS TOMADO DE UNA LISTA QUE OTRO RELLENA, USANDO UN Flux.interval,
 * PERO ESTE DEVUELVE SIEMPRE, TENIENDO QUE ENVIAR AUNQUE SEA UN Optional VACÍO, CAUSANDO UN IMPACTO INNECESARIO
 * NOS DECANTAMOS POR WEBSOCKET
 * */

@ShellComponent //tells spring we are building a Shell-based component
public class RSocketShellClient {

	@Qualifier("RSocketShellClientLogger")
	@Autowired private org.slf4j.Logger log;
	
    private static final String CLIENT = "Client";
    private static final String REQUEST = "Request";
    private static final String FIRE_AND_FORGET = "Fire-And-Forget";
    private static final String STREAM = "Stream";
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final MimeType SIMPLE_AUTH = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    private static Disposable disposable; //para streams conn model

    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;

    private static String username = null;
    private static String password = null;
    
    
    /** BASIC CONSTRUCTOR
     * The requester’s connectTcp() method needs to know the IP address and port of your RSocket server, and you need to tell the requester to block() until a connection is established. After that, you’re ready to communicate with the RSocket server over TCP.
     * */
    /*@Autowired
    public RSocketShellClient(RSocketRequester.Builder rsocketRequesterBuilder) {
    	this.rsocketRequesterBuilder = rsocketRequesterBuilder;
    }*/
    
    
    /** ESTO SE MODIFICÓ Y SE EXTRAJO LA FUNCIONALIDAD A OTRO MÉTODO
     * ENHANCED CONSTRUCTOR (to handle server-to-client requests)
     * Before the client can respond to messages coming from the server, it must register the ClientHandler with the RSocket connection.
     * Notice the change to the constructor’s method signature to add the RSocketStrategies variable. Spring supplies this variable to your constructor.
     * 
     *  El flujo con este constructor sería:
     *  	.Client registra una clase que permite responder a Server (telemetry data)
     *  	.Client abre una conexión al Server y Server la registra
     *  	.Server solicita un Flux a Client, el cual será manejado por la clase registrada
     *  	.Client comienza a enviar mensajes (telemetry)
     *  	.Server pudiera (no se implementó) enviar requests a Client usando el objeto RSocketRequester que guardó en una lista
     *  
     *  *** NOTE ***
     *  The ability to call out to clients is very powerful. It’s ideal for all kinds of scenarios, including mobile, internet-of-things, or microservices. 
     *  And because all this can happen over TCP or WebSockets, you already have all the infrastructure you need without resorting to heavyweight solutions like message brokers.
     *  You covered a lot of ground here. You learned how to turn servers into ‘requesters’ and clients into ‘responders.’ You discovered how to listen into connection events (con el método < connectShellClientAndAskForTelemetry > del servidor).
     *  You also learned a little bit about how to handle errors and events coming from rsocket connections. 
     *  And, although in this exercise, you chose ‘request-stream’ as your server-client communication, you could use any of the four RSocket interaction modes, depending on your needs.
     * */
    @Autowired
    public RSocketShellClient(RSocketRequester.Builder rsocketRequesterBuilder, @Qualifier("rSocketStrategies") RSocketStrategies strategies, org.slf4j.Logger log) {
    	this.rsocketRequesterBuilder = rsocketRequesterBuilder;
    	rsocketStrategies = strategies;
    	if (this.log == null)
    		this.log = log;
    }
    
    
    /**.frameDecoder(PayloadDecoder.ZERO_COPY)
     * By default to make RSocket easier to use it copies the incoming Payload.
     * Copying the payload comes at cost to performance and latency.
     * If you want to use zero copy you must disable this.
     * To disable copying you must include a payloadDecoder argument in your RSocketFactory.
     * This will let you manage the Payload without copying the data from the underlying transport.
     * You must free the Payload when you are done with them or you will get a memory leak.
     * Used correctly this will reduce latency and increase performance.
     * WE SHALL READ HOW TO RELEASE THE PAYLOAD!
     */
    public RSocket rSocket() {
        return RSocketFactory
            .connect()
            .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE)
            .frameDecoder(PayloadDecoder.ZERO_COPY)
            //.transport(WebsocketClientTransport.create("localhost", 7000))
            .transport(TcpClientTransport.create("localhost",7000))
            .start()
            .block();
    }
    
    //--------------------------------------------------------------------------
    
    /** 
     * login --username user --password pass
     * se exigen los parámetros, por eso consideramos * como comodín de que no queremos seguridad
     * */
    @ShellMethod("Login with your username and password.")
    public void login(String username, String password) {
        
    	if (username == null || password == null || username.equals("") || password.equals(""))
    		log.info("Connecting using client ID: {}", CLIENT_ID);
    	else {
    		log.info("Connecting using client ID: {} and username: {}", CLIENT_ID, username);
    		this.username = username;
    		this.password = password;
    	}
    	
    	//To configure with .frameDecoder(PayloadDecoder.ZERO_COPY)
        //this.rsocketRequester = RSocketRequester.wrap(rSocket(), MimeTypeUtils.APPLICATION_JSON, MimeTypeUtils.APPLICATION_JSON, rsocketStrategies);

        //TCP conn
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000)//only accepts rsocket port
                .block();

        //WEBSOCKET conn
        /*this.rsocketRequester = rsocketRequesterBuilder
                .connectWebSocket(URI.create("ws://localhost:7000/rsocket"))//accepts rsocket and web ports 7000,8010
                .block();*/
        
    	// CON LO ANTERIOR YA SE CONECTA. EL SIGUIENTE PASO (opcional pero mejor tenerlo) ES REGISTRAR CÓMO MANEJAR EVENTOS ERROR Y DESCONEXIÓN
 		this.rsocketRequester.rsocket()
 			.onClose()
 			.doOnError(error -> log.warn("Connection CLOSED"))
 			.doFinally(consumer -> log.info("Client DISCONNECTED"))
 			.subscribe();
    }

    
    private boolean userIsLoggedIn() {
        if (null == this.rsocketRequester || this.rsocketRequester.rsocket().isDisposed()) {
            log.info("No connection. Did you login?");
            return false;
        }
        return true;
    }
    
    
    @PreDestroy
    @ShellMethod("Logout and close your connection")
    public void logout() {
        if (userIsLoggedIn()) {
            this.s();
            this.rsocketRequester.rsocket().dispose();
            log.info("Logged out.");
        }
    }
    
    
    @ShellMethod("Stops Streams or Channels.")
    public void s() {
        if (userIsLoggedIn() && null != disposable) {
            log.info("Stopping the current stream.");
            disposable.dispose();
            log.info("Stream stopped.");
        }
    }
    
    //--------------------------------------------------------------------------
    
    /** REQUEST-RESPONSE
     * hacemos en la consola al jecutar esta app:		request-response
     * */
    @ShellMethod("Send one request. One response will be printed.") //use the @ShellMethod annotation over the method signature to activate Spring Shell and declare the help text that users will see if they type help
    public void requestResponse() throws InterruptedException {
        if (userIsLoggedIn()) {
            log.info("\nSending one request. Waiting for one response...");
            Message message = this.rsocketRequester
                    .route("request-response") //This route name matches the @MessageMapping annotation on the method in the RSocketController (server app)
                    .data(new Message(CLIENT, REQUEST))
                    .retrieveMono(Message.class) //defines request-response model
                    .timeout(Duration.ofMillis(5000))
                    .block();
            log.info("\nResponse was: {}", message);
        }
    }

    
    /** FIRE-AND-FORGET
     * once the Message object is sent, the client can continue doing other work, the server can also continue without sending a response
     * 
     * Publisher<Void> es devuelto en un ejemplo con client Rest Api.
     * 
     * fire-and-forget con modo tcp seguramente es un req-res async, y del otro lado se responde inmediatamente sin importar el resultado, siendo así mucho más rápido a que si tiene que esperar la ejecución del otro lado.
     * */
    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
    	if (userIsLoggedIn()) {
	    	log.info("\nFire-And-Forget. Sending one request. Expect no response (check server log)...");
	    	
			this.rsocketRequester
            //.route("fire-and-forget")
            //.data(new Message(CLIENT, FIRE_AND_FORGET, i))
            .route("log-message")
			.data(new LogMsg(this.getClass().getTypeName(), LogLevel.WARN, "WARN test message", "Checked exception during blablabla"))
            .send() //Notice that there is no .retrieveMono() call. Instead, the fire-and-forget specific .send() method sends the message to the server, while .block() subscribes and waits for completion. Remember, nothing happens in reactive code without a subscription.
            .block();
    	}
    }

    
    /** STREAM
     * 
     * Ejemplo de un client Api Rest:
     * @GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)        our client (for the browser, mobile, other..) is also a REST server, it defines response media type as MediaType.TEXT_EVENT_STREAM_VALUE, because it will generate a flux.
     * public Publisher<MarketData> feed(@PathVariable("stock") String stock) {...}
     * */
    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream() {
        if (userIsLoggedIn()) {
            log.info("\n\n**** Request-Stream\n**** Send one request. Sending one request. Waiting for unlimited responses.\n**** Log responses.\n**** Type 's' to stop.");
            disposable = this.rsocketRequester
                    .route("stream")
                    .data(new Message(CLIENT, STREAM))
                    .retrieveFlux(Optional.class) //defines stream model
                    .subscribe(message -> {
                    	if (message.isPresent())
                    		log.info("Response: {} \n(Type 's' to stop.)", message);
                    });
        }
    }
    
    
    /** CHANNEL (bi-directional stream)
     * 
     * Notar que la dif con stream es que stream envía 1 sólo objeto, y channel un Producer (Flux)
     * claro el método del servidor recibe un Flux también
     * */
    @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
    public void channel() {
        if (userIsLoggedIn()) {
            log.info("\n\n***** Channel (bi-directional streams)\n***** Asking for a stream of messages.\n***** Type 's' to stop.\n\n");

            Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
            Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
            Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));

            Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
                    .doOnNext(d -> log.info("\nSending setting for a {}-second interval.\n", d.getSeconds()));

            
        	List<String> list = new ArrayList<>();
        	new Thread(() -> {
    				long now = System.currentTimeMillis();
    				int i = 0;
    				do {
    					list.add(i + "" + System.currentTimeMillis());
    					i++;
    					try {Thread.sleep(1);} catch (Exception e) {}
    				} while((System.currentTimeMillis()-now) < 5*60_000);
        	}).start();
            
            
            disposable = this.rsocketRequester
                    //.route("channel")
                    .route("channel2")
                    //.data(settings)
                    .data(Flux.interval(Duration.ofNanos(1_000_000)) //1.000.000.000 nanos = 1 sec
				        		.map(index -> {
				        			if (!list.isEmpty()) {
				        				return Optional.of(list.remove(0));
				        			}
				        			return Optional.empty();
				        		}))
                    .retrieveFlux(Message.class)
                    .subscribe(message -> {
                    	log.info("Received: {} \n(Type 's' to stop.)", message);
                    });
        }
    }

    
    //--------------------------------------------------------------------------

    //SERVER-TO-CLIENT MODEL
    //Server-to-Client no deja de usar los otros 4 modelos, simplemente que el servidor ahora tiene una ref al cliente.
    //la ventaja puede ser que deja guardado en servidor nuestros datos de conexión, y si cambia nuestra IP se la refresca al servidor.
    //no manteniendo una conexión persistente
    //es ideal como un listener sobre algún evento en el servidor, por ejemplo si llega un msje para este cliente, el servidor lo comunica sin que el cliente deba enviar request al servidor cada tanto tiempo.
    //para conexión persistente es mejor stream o channel.
    //notar que si estoy comunicando mis servidores, aunque hayan pocos eventos en un channel, se usarán pocas conexiones. Server to client es ideal cuando hay pocos eventos y muchos clientes, donde es mejor guardar una lista de clientes y yo servidor te aviso cuando llegue algo para tí.
    
    /** FOR SERVER-TO-CLIENT REQUESTS EXAMPLE
     * https://spring.io/blog/2020/05/12/getting-started-with-rsocket-servers-calling-clients
     * 
     * The client uses this class and this method to capture and respond to requests coming in from the server. The response itself is a stream of messages. Every 5 seconds, the client tells the server its current free memory. Think of this as client telemetry data.
     * For this to work, you must ‘register’ this class with your RSocket connection.
     * */
    class ClientHandler {
    	@MessageMapping("client-status")
    	public Flux<String> statusUpdate(String status) {
    		log.info("Connection {}", status);
    		return Flux.interval(Duration.ofSeconds(5))
    				.map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
    	}
    }
    
    
    /**
     * hacer login con username y password cuando habilitamos security del otro lado!
     * igual debemos hacer login con username=* pq el método shell exige todos los parametros!
     * */
    @ShellMethod("Server to client.")
    public void serverToClient() {
    	if (userIsLoggedIn()) {
    		s();
    		logout();
    		
    		if (username == null || password == null || username.equals("*"))
    			log.info("Connecting using client ID: {}", CLIENT_ID);
    		else
    			log.info("Connecting using client ID: {} and username: {}", CLIENT_ID, username);
    		
    		//PARA SERVER-TO-CLIENT REQUESTS
    		// REGISTRAMOS CLASES QUE MANEJEN LLAMADAS (métodos con @MessageMapping) Y OBTENEMOS UN SOCKET ACEPTADOR DE LLAMADAS (solicitudes)
    		// (1) you create a new SocketAcceptor using the RSocket strategies plus a new ClientHandler instance
    		List<Object> handlers = new ArrayList<>();
    		handlers.add(new ClientHandler());
    		RSocketMessageHandler handler = new RSocketMessageHandler();
    		handler.setHandlers(handlers);
    		handler.setRSocketStrategies(rsocketStrategies);
    		handler.afterPropertiesSet();
    		SocketAcceptor responder = handler.responder();
    		// (when using SpringBoot 2.3.0+) SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());
    		
    		// CONFIGURAMOS LA CONEXIÓN, REGISTRAMOS EL SocketAcceptor ANTERIOR Y LLAMAMOS UN MÉTODO DEL OTRO LADO ENVIANDO UN MENSAJE DE UNA VEZ (el id de este cliente)
    		// (2) use the RSocketRequesterBuilder to register the new SocketAcceptor
    		RSocketRequester.Builder b = rsocketRequesterBuilder
    				.setupRoute("shell-client")
    				.setupData(CLIENT_ID)
    				.rsocketFactory(connector -> connector.acceptor(responder));
    		// (when using SpringBoot 2.3.0+) .rsocketConnector(connector -> connector.acceptor(responder))
    		
    		// SI ES SIN USERNAME-PASSWORD
    		if (username == null || username.equals("*")) {
    			this.rsocketRequester = b
    					.rsocketStrategies(rsocketStrategies)
    					.connectTcp("localhost", 7000)
    					.block();
    		} else {
    			/*
			 	UsernamePasswordMetadata user = new UsernamePasswordMetadata(username, password);
			 	this.rsocketRequester = b
			 		.rsocketStrategies(builder -> builder.encoder(new SimpleAuthenticationEncoder()))
			 		.setupMetadata(user, SIMPLE_AUTH)
			 		.connectTcp("localhost", 7000)
			 		.block();
			 		*/
    		}
    		
    		// CON LO ANTERIOR YA SE CONECTA. EL SIGUIENTE PASO (opcional pero mejor tenerlo) ES REGISTRAR CÓMO MANEJAR EVENTOS ERROR Y DESCONEXIÓN
    		// (3) make sure that disconnection is handled gracefully by handling the RSocket onClose() events
    		this.rsocketRequester.rsocket()
    		.onClose()
    		.doOnError(error -> log.warn("Connection CLOSED"))
    		.doFinally(consumer -> log.info("Client DISCONNECTED"))
    		.subscribe();
    	}
    }

}
