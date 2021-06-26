package com.example.rsocketserver;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

import com.example.rsocketserver.data.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RSocketController {

	@Autowired private org.slf4j.Logger log;
	
    static final String SERVER = "Server";
    static final String RESPONSE = "Response";
    static final String STREAM = "Stream";
    static final String CHANNEL = "Channel";

    private final List<RSocketRequester> CLIENTS = new ArrayList<>(); //to handle clients connections when we want server-to-client requests

    //en el constructor se levanta un thread que va llenando una lista con mensajes que se irán enviando
    private List<Message> list;
    
    public RSocketController() {
    	this.list = new ArrayList<>();
    	new Thread(() -> {
				long now = System.currentTimeMillis();
				int i = 0;
				do {
					list.add(new Message(SERVER, STREAM, i));
					i++;
					try {Thread.sleep(1);} catch (Exception e) {}
				} while((System.currentTimeMillis()-now) < 5*60_000);
    	});
    	//.start(); //SÓLO PARA STREAM O CHANNEL CUANDO ENVÍAN Flux.
    }
    
    
    @PreDestroy
    void shutdown() {
        log.info("Detaching all remaining clients...");
        CLIENTS.stream().forEach(requester -> requester.rsocket().dispose());
        log.info("Shutting down.");
    }
    
    
    @MessageMapping("request-response")
    Message requestResponse(Message request) {
            log.info("Received request-response request: {}", request);
            // create a single Message and return it
            return new Message(SERVER, RESPONSE);
    }
    
    
    @MessageMapping("fire-and-forget")
    public void fireAndForget(Message request) {
        log.info("Received fire-and-forget request: {}", request);
    }
    
    
    /**
     * The Flux object returned by the method is part of Project Reactor and is also used in the reactive support of the Spring Framework.
     * RSocket uses Flux because it dramatically simplifies the handling of reactive data streams. Flux is a “Publisher” of data. It describes streams of 0 to N elements and offers a great many operators for processing streaming data — similar to Java 8’s streaming APIs.
     * In the code above, a new Long element gets added to the Flux every second — set via the .interval() call — essentially providing a constant stream of data. The .map() function creates a new message object using the Long as the index value, and on the last line, the call to the .log() method prints all elements flowing through the Flux to the console, including errors, etc.
     * */
    @MessageMapping("stream")
    Flux<Optional<Message>> stream(Message request) {
        log.info("Received stream request: {}", request);
        /*
        return Flux
        		 // create a new indexed Flux emitting one element every second
                .interval(Duration.ofSeconds(1))
                // create a Flux of new Messages using the indexed Flux
                .map(index -> new Message(SERVER, STREAM, index))
                		.log(); //logs the client’s request to the console as soon as it’s received.
        */
        
        //cada x nanos intenta enviar un msg
        return Flux
        		.interval(Duration.ofNanos(100_000)) //1.000.000.000 nanos = 1 sec
        		.map(index -> {
        			if (!list.isEmpty()) {
        				return Optional.of(list.remove(0));
        			}
        			return Optional.empty();
        		});

        /*return Flux
        		//.from(Mono.) //no tiene mucho sentido hacer un flujo para enviar un único item, para eso usamos req-res model.
        		//.from(Flux.)
        		//.fromArray(..)
        		//.fromIterable(list) //no sirve que vaya otro thread llenando el iterable
        		//.fromStream(list.stream()) //no sirve que vaya otro thread llenando el iterable
        		.log();*/
       
        
        //NO SIRVIÓ
        /*
        return Flux.<Optional<Message>>create(emitter -> {
        	emitter.onDispose(() -> {
            	System.out.println("ON DISPOSE");
            });
        	
        	do {
        		try {
        			if (!list.isEmpty()) {
        				emitter.next(Optional.of(list.remove(0)));
        				//emitter.complete();
					}
				} catch (Exception e) {}
        	} while (!emitter.isCancelled());
        });
        */
    }

    
    /**
     * Channels are bi-directional pipes that allow streams of data to flow in either direction. 
     * With channels, a data stream from client-to-server can coexist alongside a data stream from server-to-client. 
     * Channels have many real-world uses. Channels could carry video conferencing streams, send and receive two-way chat messages, synchronize data using deltas and diffs, or provide a means to report, observe, and monitor your system.
     * 
     * Channels in RSocket are no more complicated than streams or request-response. 
     * That said, the scenario you’ll implement below is slightly more complicated than you’ve attempted previously, so it’s best to understand it before you begin.
     * 
     * In the exercise that follows, the server streams messages to the client
     * The client controls the frequency of the messages in the server’s stream.
     * It does this using a stream of ‘delay’ settings. The settings in the client’s stream tell the server how long the pause should be between each message it sends. 
     * Think of it as a message frequency dial. With the frequency setting high, the pause is shorter, so you’ll see lots of server-sent messages. With the frequency setting low, the pause is longer, so you’ll see fewer server-sent messages. With that outcome in mind, let’s start coding.
     * 
     * In the code, the .doOnNext() is listening to the stream of settings coming from the client. 
     * Each time a new delay setting arrives, it writes a message to the log. 
     * The .switchMap() creates a new Flux for each new setting. This new flux emits a new Message object based on the .interval() delay contained in the delay setting. 
     * The server sends these new messages back to the client in the stream.
     * */
    @MessageMapping("channel")
    Flux<Message> channel(final Flux<Duration> settings) {
    	log.info("Received channel request...");
        return settings
        		.doOnNext(setting -> log.info("\nFrequency setting is {} second(s).\n", setting.getSeconds()))
        		.doOnCancel(() -> log.warn("The client cancelled the channel."))    
                .switchMap(setting -> Flux.interval(setting)
							                               .map(index -> new Message(SERVER, CHANNEL, index)))
							                               .log();
    }
    
    
    //--------------------------------------------------------------------------
    
    
    /** To handle server-to-client requests
     * The @ConnectMapping annotation lets you listen to client connection events as they happen. Using this event, you can schedule two pieces of work. The first is to add each new client to the CLIENTS list. The second is to call out to each client and start their telemetry streams.
     * 
     *  This code might feel counter-intuitive — calling onClose() while a client’s connecting and then using the resulting mono to store a reference to the new client. Sometimes, event-driven API’s can feel a bit odd. But think of it as the mono for this RSocket connection sending you an “I’m alive” event. You’re using that creation event to trigger the storage of each client’s reference in the list.
     * */
    @ConnectMapping("shell-client")
    void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String client) {

    	//notar que le llega un RSocketRequester, objeto que usan los client para conectar con server.
    	//notar que guardamos RSocketRequester en lista de clients..  podríamos parar aquí y en el momento que queramos usar ese requester para hacer un request al client.
    	//pero decidimos usarlo ahora y hacer un retrieveFlux (ver abajo)
    	
        requester.rsocket()
                .onClose() //This method returns a reactive Mono object, which contains all the callbacks you need.
                //The mono’s doFirst() method gets called before any calls to subscribe(), but after the initial creation of the mono.
                .doFirst(() -> {
                    // Add all new clients to a client list
                    log.info("Client: {} CONNECTED.", client);
                    CLIENTS.add(requester);
                })
                //RSocket calls the mono’s doOnError() method whenever there is a problem with the connection. This includes situations where the client has chosen to close the connection. You can use the error variable provided to decide what action to take. 
                .doOnError(error -> {
                    // Warn when channels are closed by clients
                    log.warn("Channel to client {} CLOSED", client);
                })
                //The mono’s doFinally() method is triggered when the RSocket connection has closed.
                .doFinally(consumer -> {
                    // Remove disconnected clients from the client list
                    CLIENTS.remove(requester);
                    log.info("Client {} DISCONNECTED", client);
                })
                //subscribe() activates the reactive code you’ve added to the mono and signals that you’re ready to process the events.
                .subscribe();

        
        //CON LO PREVIO REGISTRAMOS EVENT HANDLERS, Y GUARDAMOS REFERENCIA AL CLIENTE
        //LO SIG ES PARA HACER UNA SOLICITUD DE STREAM (Flux) AL CLIENTE QUE HIZO EL LLAMADO
        // Callback to client, confirming connection
        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
                .doOnNext(s -> log.info("Client: {} Free Memory: {}.", client, s))
                //notar que en lado cliente (EN STREAM EXAMPLE) no usamos .doOnNext(), sino que usamos .suscribe(message -> log.info("msg: ", message))
                .subscribe();
    }
    
}
