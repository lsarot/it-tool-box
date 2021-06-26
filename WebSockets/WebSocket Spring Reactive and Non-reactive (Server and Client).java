*** WEBSOCKET SPRING REACTIVE
https://github.com/eugenp/tutorials/blob/master/spring-5-reactive/src/main/java/com/baeldung/websocket/ReactiveWebSocketConfiguration.java
https://www.baeldung.com/spring-5-reactive-websockets
https://howtodoinjava.com/spring-webflux/reactive-websockets/
NO LO PUDE CONFIGURAR!, igualmente usa Flux y no conozco cómo configurar distinto de Flux.interval


*** WEBSOCKET SPRING (NON REACTIVE)

******************** SERVER ********************

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-websocket</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-messaging</artifactId>
</dependency>
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-core</artifactId>
	<version>2.10.2</version>
</dependency>
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.10.2</version>
</dependency>

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * WebSockets is a bi-directional, full-duplex, persistent connection between a web browser and a server. Once a WebSocket connection is established the connection stays open until the client or server decides to close this connection.
 *
 * To communicate with the WebSocket server, the client has to initiate the WebSocket connection by sending an HTTP request to a server with an Upgrade header set properly:   Connection: Upgrade , Upgrade: websocket
 * Please note that the WebSocket URLs use ws and wss schemes, the second one signifies secure WebSockets.
 * The server responds back by sending the Upgrade header in the response if WebSockets support is enabled:   Connection: Upgrade , Upgrade: websocket
 * Once this process (also known as WebSocket handshake) is completed, the initial HTTP connection is replaced by WebSocket connection on top of same TCP/IP connection after which either parties can share data.
 *
 * STOMP:
 * Stream Text-Oriented Messaging Protocol (STOMP) is a simple, interoperable wire format that allows client and servers to communicate with almost all the message brokers.
 * It is an alternative to AMQP (Advanced Message Queuing Protocol) and JMS (Java Messaging Service).
 * STOMP defines a protocol for client/server to communicate using messaging semantics. The semantics are on top of the WebSockets and defines frames that are mapped onto WebSockets frames.
 * Using STOMP gives us the flexibility to develop clients and servers in different programming languages.
 * */

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //los topics a los que se suscriben los clientes comienzan con esto
        config.enableSimpleBroker("/topic");
        //prefijo de los llamados a métodos @MessageMapping.. el cliente usaría "/app/abc", pero el método lo identifca con @MessageMapping("/abc")
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * registers the “/chat” endpoint, enabling Spring’s STOMP support. Keep in mind that we are also adding here an endpoint that works without the SockJS for the sake of elasticity.
     * This endpoint, when prefixed with “/app”, is the endpoint that the ChatController.send() method is mapped to handle.
     * It also enables the SockJS fallback options, so that alternative messaging options may be used if WebSockets are not available. This is useful since WebSocket is not supported in all browsers yet and may be precluded by restrictive network proxies.
     * The fallbacks let the applications use a WebSocket API but gracefully degrade to non-WebSocket alternatives when necessary at runtime.
     * */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //client must connect to "ws://localhost:8010/chat" (it uses http server port)
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();
    }
}


@Controller
public class ChatController {

	@MessageMapping("/chat")
    //@SendTo("/topic/messages") //TO RESPOND BACK TO CLIENTS SUBSCRIBED TO THIS TOPIC.
        //note its not async, we would need to store req or res into a queue and send them back to clients.
        //note we do not have a ref to clients to respond to, there is no easy way to respond later.
        //note it degrades performance if we put a delay of 150ms, so we must enqueue tasks.
    public void send(org.springframework.messaging.Message<Message> message) {
        System.out.println(message.getPayload()+" "+System.currentTimeMillis());
        //message.getHeaders()
        //Object o = message.getHeaders().getReplyChannel(); //null
        try {Thread.sleep(150);} catch (Exception e) {}

        //OR RESPOND (use @SendTo("/topic/abc"))
        //Message response = new Message("SENDER", message.getPayload());
        //return response;
    }

    public static class Message {
        private String from;
        private String text;
        public Message(String from, String text) {this.from = from; this.text = text;}
        public String getText() {return text;}
        public String getFrom() {return from;}
        public void setFrom(String from) {this.from = from;}
        public void setText(String text) {this.text = text;}
        @Override public String toString() {return "Message{from='" + from + ", text='" + text + '}'; }
    }
}


******************** CLIENT ********************

<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-websocket</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-messaging</artifactId>
</dependency>

@Autowired private SecondaryModulesConnService secondaryModulesConnService;

public void testWebSocket() {
	//StandardWebSocketClient provided by any JSR-356 implementation like Tyrus
	//JettyWebSocketClient provided by Jetty 9+ native WebSocket API
	//Any implementation of Spring’s WebSocketClient
	//If our server has SockJs support, then we can modify the client to use SockJsClient instead of StandardWebSocketClient.
	WebSocketClient client = new StandardWebSocketClient();
	WebSocketStompClient stompClient = new WebSocketStompClient(client);

	stompClient.setMessageConverter(new MappingJackson2MessageConverter());

	StompSessionHandler sessionHandler = secondaryModulesConnService;
	try {
	    stompClient.connect("ws://localhost:8010/chat", sessionHandler).get();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    e.printStackTrace();
	}

	//WARMING
	secondaryModulesConnService.sendMessage();

	System.out.println("Time: "+System.currentTimeMillis());
	for (int i = 0; i < 1_000; i++) {
	    secondaryModulesConnService.sendMessage();
	}
}

@Service
public class SecondaryModulesConnService extends StompSessionHandlerAdapter {

    private StompSession session;
    private AtomicInteger cont = new AtomicInteger(0);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        //System.out.println("New session established : " + session.getSessionId());
        session.subscribe("/topic/messages", this);
        //System.out.println("Subscribed to /topic/messages");
        //session.send("/app/chat", getSampleMessage());
        //System.out.println("Message sent to websocket server");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("Got an exception " + exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Message.class;//default if we do not override! (we can use an object and Jackson will parse to/from json)
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        Message msg = (Message) payload;
        System.out.println("Received : " + msg);
    }

    private Message getSampleMessage() {
        Message msg = new Message("CLIENT", ""+cont.incrementAndGet());
        return msg;
    }

    public static class Message {
        private String from;
        private String text;
        public Message(String from, String text) {this.from = from; this.text = text;}
        public String getText() {return text;}
        public String getFrom() {return from;}
        public void setFrom(String from) {this.from = from;}
        public void setText(String text) {this.text = text;}
        @Override public String toString() {return "Message{from='" + from + ", text='" + text + '}'; }
    }

    public void sendMessage() {
        session.send("/app/chat", getSampleMessage());
        //session...
    }    
}
