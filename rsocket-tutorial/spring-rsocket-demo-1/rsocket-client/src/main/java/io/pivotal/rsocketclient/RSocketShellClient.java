package io.pivotal.rsocketclient;

import io.pivotal.rsocketclient.data.Message;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.UUID;

/**
 * ES UNA APP QUE FUNCIONA COMO UN SHELL
 * Los métodos con @ShellMethod serán registrados por su nombre
 * Si usan camelCase, serán mapeados a kebab-case 
 * help nos mostrará los métodos disponibles
 * */

@Slf4j
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

    
    /** BASIC CONSTRUCTOR (luego se crea la conn en el login method)
     * The requester’s connectTcp() method needs to know the IP address and port of your RSocket server, and you need to tell the requester to block() until a connection is established. After that, you’re ready to communicate with the RSocket server over TCP.
     * */
    /*
    @Autowired
    public RSocketShellClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }*/
    
    
    @Autowired
    public RSocketShellClient(RSocketRequester.Builder builder, @Qualifier("rSocketStrategies") RSocketStrategies strategies) {
        this.rsocketRequesterBuilder = builder;
        this.rsocketStrategies = strategies;
    }

    /** login --username user --password pass
     * 
     * The most relevant lines in terms of adding security are as follows:
     * 
     * The SIMPLE_AUTH class' static variable (1) declares how your user object should be encoded when passed as connection metadata.
     * A new UsernamePasswordMetadata is defined (2), which contains the credentials provided by the user as they login.
     * When connecting (3), the setupMetadata() method passes the user object and the encoding mimetype defined at point (1).
     * A new SimpleAuthenticationEncoder (4) is placed in the RSocketStrategies used for this connection. This object takes care of encoding the UsernamePasswordMetadata (2) into the correct mimetype (1).
     * */
    @ShellMethod("Login with your username and password.")
    public void login(String username, String password) {
        log.info("Connecting using client ID: {} and username: {}", CLIENT_ID, username);
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientHandler());
       
        UsernamePasswordMetadata user = new UsernamePasswordMetadata(username, password); // (2)
        
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("shell-client")
                .setupData(CLIENT_ID)
                .setupMetadata(user, SIMPLE_AUTH) // (3)
                .rsocketStrategies(builder ->
                        builder.encoder(new SimpleAuthenticationEncoder())) // (4)
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("localhost", 7000)
                .block();

        this.rsocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();
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

    private boolean userIsLoggedIn() {
        if (null == this.rsocketRequester || this.rsocketRequester.rsocket().isDisposed()) {
            log.info("No connection. Did you login?");
            return false;
        }
        return true;
    }

    
    /**
     * MÉTODO BÁSICO A PROBAR
     * hacemos en la consola al jecutar esta app:		request-response
     * */
    @ShellMethod("Send one request. One response will be printed.") //use the @ShellMethod annotation over the method signature to activate Spring Shell and declare the help text that users will see if they type help
    public void requestResponse() throws InterruptedException {
        if (userIsLoggedIn()) {
            log.info("\nSending one request. Waiting for one response...");
            Message message = this.rsocketRequester
                    .route("request-response")
                    .data(new Message(CLIENT, REQUEST))
                    .retrieveMono(Message.class)
                    .timeout(Duration.ofMillis(5000))
                    .block();
            log.info("\nResponse was: {}", message);
        }
    }

    
    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
        if (userIsLoggedIn()) {
            log.info("\nFire-And-Forget. Sending one request. Expect no response (check server console log)...");
            this.rsocketRequester
                    .route("fire-and-forget")
                    .data(new Message(CLIENT, FIRE_AND_FORGET))
                    .send()
                    .block();
        }
    }
    

    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream() {
        if (userIsLoggedIn()) {
            log.info("\n\n**** Request-Stream\n**** Send one request.\n**** Log responses.\n**** Type 's' to stop.");
            disposable = this.rsocketRequester
                    .route("stream")
                    .data(new Message(CLIENT, STREAM))
                    .retrieveFlux(Message.class)
                    .subscribe(message -> log.info("Response: {} \n(Type 's' to stop.)", message));
        }
    }

    
    @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
    public void channel() {
        if (userIsLoggedIn()) {
            log.info("\n\n***** Channel (bi-directional streams)\n***** Asking for a stream of messages.\n***** Type 's' to stop.\n\n");

            Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
            Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
            Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));

            Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
                    .doOnNext(d -> log.info("\nSending setting for a {}-second interval.\n", d.getSeconds()));

            disposable = this.rsocketRequester
                    .route("channel")
                    .data(settings)
                    .retrieveFlux(Message.class)
                    .subscribe(message -> log.info("Received: {} \n(Type 's' to stop.)", message));
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



    class ClientHandler {
    	@MessageMapping("client-status")
    	public Flux<String> statusUpdate(String status) {
    		log.info("Connection {}", status);
    		return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
    	}
    }

}


