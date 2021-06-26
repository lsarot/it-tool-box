package com.example.demospringweb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BasicConfigurationIntegrationTest {
 
	 //------------------------------------------- TESTS DE UN TUTORIAL, NO SIRVIÓ INYECTAR TestRestTemplate
	
	@Autowired TestRestTemplate restTemplate;
    URL base;
    @LocalServerPort int port;
 
    //@Before
    public void setUp() throws MalformedURLException {
        //restTemplate = new TestRestTemplate("user", "password");
        base = new URL("http://localhost:" + port);
    }
 
    //@Test
    public void whenLoggedUserRequestsHomePage_ThenSuccess() throws IllegalStateException, IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Baeldung"));
    }
 
    //@Test
    public void whenUserWithWrongCredentials_thenUnauthorizedPage() throws Exception {
        restTemplate = new TestRestTemplate("user", "wrongpassword");
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Unauthorized"));
    }
    
    //------------------------------------------- TESTS API LEO
    
    @Test
    public void testMyApiMethod() 
    		throws ClientProtocolException, IOException {
    	
    	HttpUriRequest request = new HttpGet( "http://localhost:8080/demo-spring-web/say-hi" );
    	
    	// Basic  <the base64-encoded username:password>
    	String encoded = Base64.getEncoder().encodeToString("user:password".getBytes());
    	request.addHeader("Authorization", "Basic " + encoded);
    	HttpResponse response = HttpClientBuilder.create().build().execute( request );
    	String jsonFromResponse = EntityUtils.toString(response.getEntity());
    	
    	assertEquals("HOLA", jsonFromResponse);
    }
    
    /**
     * NECESITA AUTHENTICATION Y USA VALIDACIONES PRE Y POST AUTHORIZE EN UNOS MÉTODOS
     * 
     * Si uso loginForm falla, pq envía form para introducir credenciales, y estas viajan en el body, no en headers como httpBasic
     * */
    @Test
    public void testMyApiMethod2() 
    		throws ClientProtocolException, IOException {
    	
    	HttpUriRequest request = new HttpGet( "http://localhost:8080/demo-spring-web/private/hello" );
    	
    	// Basic  <the base64-encoded username:password>
    	String encoded = Base64.getEncoder().encodeToString("leo:pass".getBytes());
    	request.addHeader("Authorization", "Basic " + encoded);
    	HttpResponse response = HttpClientBuilder.create().build().execute( request );
    	String jsonFromResponse = EntityUtils.toString(response.getEntity());
    	
    	assertEquals("[\"Hello\",\"World\",\"from\",\"Private\"]", jsonFromResponse);
    }
    
    /**
     * NO NECESITA AUTHENTICATION
     * */
    @Test
    public void testMyApiMethod3() 
    		throws ClientProtocolException, IOException {
    	
    	HttpUriRequest request = new HttpGet( "http://localhost:8080/demo-spring-web/public/hello" );
    	
    	HttpResponse response = HttpClientBuilder.create().build().execute( request );
    	String jsonFromResponse = EntityUtils.toString(response.getEntity());
    	
    	assertEquals("[\"Hello\",\"World\",\"from\",\"Public\"]", jsonFromResponse);
    }
    
    //------------------------------------------- TESTS API TERCERO (GITHUB API)
    
    @Test
    public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived()
      throws ClientProtocolException, IOException {
      
        // Given
        String name = "nxuxowx";
        HttpUriRequest request = new HttpGet( "https://api.github.com/users/" + name );
     
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
     
        // Then
        assertThat(
          httpResponse.getStatusLine().getStatusCode(),
          equalTo(org.apache.http.HttpStatus.SC_NOT_FOUND));
    }
    
    @Test
    public void
    givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() 
    		throws ClientProtocolException, IOException {
      
       // Given
       String jsonMimeType = "application/json";
       HttpUriRequest request = new HttpGet( "https://api.github.com/users/eugenp" );
     
       // When
       HttpResponse response = HttpClientBuilder.create().build().execute( request );
     
       // Then
       String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
       assertEquals( jsonMimeType, mimeType );
    }
    
    @Test
    public void
      givenUserExists_whenUserInformationIsRetrieved_thenRetrievedResourceIsCorrect() 
    		  throws ClientProtocolException, IOException {
      
        // Given
        HttpUriRequest request = new HttpGet( "https://api.github.com/users/eugenp" );
     
        // When
        HttpResponse response = HttpClientBuilder.create().build().execute( request );
     
        // Then
        GitHubUser resource = BasicConfigurationIntegrationTest.retrieveResourceFromResponse(response, GitHubUser.class);
        assertEquals("eugenp", resource.getLogin());
    }
    
    
    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) 
    		throws IOException {
	    String jsonFromResponse = EntityUtils.toString(response.getEntity());
	    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    return mapper.readValue(jsonFromResponse, clazz);
	}
    
    public static class GitHubUser {
        private String login;
        public String getLogin() {return login;}
    }
    
}
