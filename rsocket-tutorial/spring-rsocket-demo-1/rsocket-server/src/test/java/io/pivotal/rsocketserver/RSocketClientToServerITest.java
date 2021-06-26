package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.Message;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ejecutar directamente!
 * No hace falta levantar servidor por separado
 * */

/** CONFIGURE FAILSAFE PLUGIN
 * 
 * It can take a while to run integration tests, and they can fail for unexpected reasons, like when the network is down. So, it makes sense to isolate your integration tests so you can run them selectively. Maven uses the Failsafe plugin to achieve this.
 * To configure failsafe, in your pom.xml, add the plugin configuration below. This configuration tells Maven to use failsafe to run all the tests that end with the suffix ‘ITest.java.’ It also tells Maven to run these tests as part of the integration-test or verify lifecycle phases.
 * 
 * You’ll also want to prevent the integration tests from running alongside your regular unit tests, so add the following surefire configuration to exclude them:
 * 
<plugins>
	<!-- other plugins -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>2.22.0</version>
        <executions>
          <execution>
            <goals>
              <goal>integration-test</goal>
              <goal>verify</goal>
            </goals>
          </execution>
        </executions>
        <configuration>    
          <includes>** / *ITest.java</includes>           //SIN ESPACIOS EN ** / *
        </configuration>
      </plugin>
      <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <configuration>
        <excludes>
          <exclude>** / *ITest.java</exclude>           //SIN ESPACIOS EN ** / *
        </excludes>
      </configuration>
    </plugin>
    
<!-- other plugins -->
</plugins>
 * */

@SpringBootTest // The @SpringBootTest annotation allows Spring Boot to configure everything you need for your test, including RSocket. It saves a lot of time and a great deal of configuration.
public class RSocketClientToServerITest { //CALL EACH INTEGRATION TEST AS <className>ITest.java, is easier to read and allows Maven to filter your integration tests

	private static org.slf4j.Logger log;
	
    private static RSocketRequester requester;
    private static UsernamePasswordMetadata credentials;
    private static MimeType mimeType;


    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder,
    							 //@Value("${spring.rsocket.server.port}") Integer port // The port number comes from the spring.rsocket.server.port value in the application.properties file.
                                 @LocalRSocketServerPort Integer port, // Provides a convenient alternative for @Value("${local.rsocket.server.port}")
                                 @Autowired RSocketStrategies strategies, 
                                 @Autowired org.slf4j.Logger log_) {
    	log = log_;
    	
        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());
        credentials = new UsernamePasswordMetadata("user", "pass");
        mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

        requester = builder
                .setupRoute("shell-client")
                .setupData(UUID.randomUUID().toString())
                .setupMetadata(credentials, mimeType)
                .rsocketStrategies(b ->
                        b.encoder(new SimpleAuthenticationEncoder()))
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("localhost", port)
                .block();
    }
    
    @AfterAll
    public static void tearDownOnce() {
        requester.rsocket().dispose();
    }
    

    @Test
    public void testFireAndForget() {
        // Send a fire-and-forget message
        Mono<Void> result = requester
                .route("fire-and-forget")
                .data(new Message("TEST", "Fire-And-Forget"))
                .retrieveMono(Void.class);

        // Assert that the result is a completed Mono.
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    public void testRequestGetsResponse() {
        // Send a request message
        Mono<Message> result = requester
                .route("request-response")
                .data(new Message("TEST", "Request"))
                .retrieveMono(Message.class);

        // Verify that the response message contains the expected data
        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.RESPONSE);
                    assertThat(message.getIndex()).isEqualTo(0);
                })
                .verifyComplete();
    }

    @Test
    public void testRequestGetsStream() {
        // Send a request message
        Flux<Message> result = requester
                .route("stream")
                .data(new Message("TEST", "Stream"))
                .retrieveFlux(Message.class);

        // Verify that the response messages contain the expected data
        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.STREAM);
                    assertThat(message.getIndex()).isEqualTo(0L);
                })
                .expectNextCount(3)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.STREAM);
                    assertThat(message.getIndex()).isEqualTo(4L);
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void testStreamGetsStream() {
        Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(6)).delayElement(Duration.ofSeconds(0));
        Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(6)).delayElement(Duration.ofSeconds(9));
        Flux<Duration> settings = Flux.concat(setting1, setting2);

        // Send a stream of request messages
        Flux<Message> result = requester
                .route("channel")
                .data(settings)
                .retrieveFlux(Message.class);

        // Verify that the response messages contain the expected data
        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.CHANNEL);
                    assertThat(message.getIndex()).isEqualTo(0L);
                })
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.CHANNEL);
                    assertThat(message.getIndex()).isEqualTo(0L);
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void testNoMatchingRouteGetsException() {
        // Send a request with bad route and data
        Mono<String> result = requester
                .route("invalid")
                .data("anything")
                .retrieveMono(String.class);

        // Verify that an error is generated
        StepVerifier.create(result)
                .expectErrorMessage("No handler for destination 'invalid'")
                .verify(Duration.ofSeconds(5));
    }

    
    
    @Slf4j
    static class ClientHandler {
        @MessageMapping("client-status")
        public Flux<String> statusUpdate(String status) {
            log.info("Connection {}", status);
            return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
        }
    }
    
}
