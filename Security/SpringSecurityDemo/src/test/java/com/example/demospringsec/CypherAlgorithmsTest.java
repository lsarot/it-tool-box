package com.example.demospringsec;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.security.crypto.argon2.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

/* EXISTEN ALGORITMOS ONE-WAY Y TWO-WAY(reversibles)
 * One-way: BCrypt, Scrypt, Pbkdf2, ..
 * Two-way:
 * 		.simétricos (1 key) - AES (mejor que 3DES), 3DES, IDEA, Blowfish, RC4, RC5, RC6 [AES, DES, IDEA, Blowfish, RC5 and RC6 are block ciphers. RC4 is stream cipher.]
 * 				.Block algorithms. Set lengths of bits are encrypted in blocks of electronic data with the use of a specific secret key. As the data is being encrypted, the system holds the data in its memory as it waits for complete blocks.
 * 				.Stream algorithms. Data is encrypted as it streams instead of being retained in the system’s memory.
 * 		.asimétricos (public/private key) - RSA (más fuerte)
 * 
 * 		What is Symmetric Encryption Used For?
 * 			While symmetric encryption is an older method of encryption, it is faster and more efficient than asymmetric encryption, which takes a toll on networks due to performance issues with data size and heavy CPU use. Due to the better performance and faster speed of symmetric encryption (compared to asymmetric), symmetric cryptography is typically used for bulk encryption / encrypting large amounts of data, e.g. for database encryption. In the case of a database, the secret key might only be available to the database itself to encrypt or decrypt.
 * 
 * 		https://www.cryptomathic.com/news-events/blog/symmetric-key-encryption-why-where-and-how-its-used-in-banking
 * 
 * HASH: los de Hash son para integridad: MD5, MD2, SHA1
 * 
 * Empezar viendo estos o reversibles por RSA...
 * */
/** AQUÍ MOSTRAMOS ONE-WAY ENCRYPTION ALGORITHMS **
 * 
 * https://docs.spring.io/spring-security/site/docs/current/reference/html5/
 * 
 * TODOS DEBEN SER TUNEADOS PARA QUE DEMORE 1 SEGUNDO EN EVALUAR EL PASSWORD!!! (otro habla de 250ms)
 * 
 * The basic principles are: don't choose a number of rounds; instead, choose the amount of time password verification will take on your server, then calculate the number of rounds based upon that. You want verification to take as long as you can stand.
 * A reasonable goal would be for password verification/hashing to take 241 milliseconds per password. That still lets your server verify 4 passwords per second (more if you can do it in parallel).
 * */

//https://www.baeldung.com/junit-5-test-order
@TestMethodOrder(OrderAnnotation.class) //Alphanumeric or one can implement MethodOrderer for custom one
public class CypherAlgorithmsTest {

	
	private String passw = "myPassword";
	private long ts;
	private String result;
	private PasswordEncoder encoder;
	private boolean enableAfterEach = true;
	
	
	@BeforeEach
	void beforeEach() {
		ts = System.currentTimeMillis();
	}
	
	
	@AfterEach
	void afterEach() {
		if (enableAfterEach) {
			System.out.println(result);
			long a = System.currentTimeMillis();
			assertTrue(encoder.matches(passw, result));
			System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
			System.out.println();
		}
	}
	
	
	/** PasswordEncoderFactories.createDelegatingPasswordEncoder(); no se puede tunear manualmente,
	 * pero podemos crear un Map con uno tuneado y tener el delegating psw encoder a partir de este map.
	 * */
	@Order(1)
	@Test
	void DelegatingWithVariousOptions() {
		String idForEncode = "bcrypt";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(idForEncode, new BCryptPasswordEncoder(12));
		encoders.put("noop", NoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder()); //This algorithm is a good choice when FIPS certification is required.
		encoders.put("scrypt", new SCryptPasswordEncoder());
		encoders.put("sha256", new StandardPasswordEncoder());
		encoder = new DelegatingPasswordEncoder(idForEncode, encoders); //first is used for encoding new psw

		result = encoder.encode(passw);
		System.out.println("Delegating psw encoder MAP: - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	/**
	 * El delegating encoder usa por defecto BCrypt, 
	 * y parece que ya está tuned pq tarda mucho más en verificar la constraseña que en los otros.
	 * 
	 *  El delegating antepone un {bcrypt} u otro para marcar el tipo de algoritmo usado, eso no supone un riesgo de seguridad pq el formato del encripted psw es bien reconocible igualmente.
	 * */
	@Order(2)
	@Test
	void DelegatingPasswordEncoder() {
		encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		result = encoder.encode(passw);
		System.out.println("Delegating psw encoder: - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(3)
	@Test
	void BCryptOriginal() {
		encoder = new BCryptPasswordEncoder();
        result = encoder.encode(passw);
		System.out.println("BCrypt: (with strength 10) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(4)
	@Test
	void BCryptTunedOtherWay() {
        SecureRandom sr = new SecureRandom(new SecureRandom().generateSeed(20));
        encoder = new BCryptPasswordEncoder(12, sr); //strength 4-31, default is 10
        result = encoder.encode(passw);
		System.out.println("BCrypt with SecureRandom: (with strength 12) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(5)
	@Test
	void BCryptPasswordEncoder4() {
		//The BCryptPasswordEncoder implementation uses the widely supported bcrypt algorithm to hash the passwords. 
		//In order to make it more resistent to password cracking, bcrypt is deliberately slow. Like other adaptive one-way functions,
		//it should be tuned to take about 1 second to verify a password on your system.
		// Create an encoder with strength x
		encoder = new BCryptPasswordEncoder(4); //4-31
		result = encoder.encode(passw);
		System.out.println("BCrypt: (with strength 4) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(6)
	@Test
	void BCryptPasswordEncoder11() {
		// Create an encoder with strength x
		encoder = new BCryptPasswordEncoder(11); //4-31
		result = encoder.encode(passw);
		System.out.println("BCrypt: (with strength 11) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(7)
	@Test
	void BCryptPasswordEncoder12() {
		// Create an encoder with strength x
		encoder = new BCryptPasswordEncoder(12); //4-31
		result = encoder.encode(passw);
		System.out.println("BCrypt: (with strength 12) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(8)
	@Test
	void BCryptPasswordEncoder16() {
		// Create an encoder with strength x
		encoder = new BCryptPasswordEncoder(16); //4-31
		result = encoder.encode(passw);
		System.out.println("BCrypt: (with strength 16) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(9)
	@Test
	void BCryptPasswordEncoder31() {
		// Create an encoder with strength x
		encoder = new BCryptPasswordEncoder(31); //4-31
		result = encoder.encode(passw);
		System.out.println("BCrypt: (with strength 31) - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	@Order(10)
	@Test
	void BCryptOtherOptionToModifyWorkFactor() {
		enableAfterEach = false;
		/*
		BCrypt implements OpenBSD-style Blowfish password hashing using the scheme described in "A Future-Adaptable Password Scheme" by Niels Provos and David Mazieres.
		This password hashing system tries to thwart off-line password cracking using a computationally-intensive hashing algorithm, based on Bruce Schneier's Blowfish cipher. The work factor of the algorithm is parameterised, so it can be increased as computers get faster.
		Usage is really simple. To hash a password for the first time, call the hashpw method with a random salt, like this:
		 */
		String pw_hash = BCrypt.hashpw(passw, BCrypt.gensalt()); //default is 10
		System.out.println("BCrypt mod work factor: (with strength 10) - encode time: " + (System.currentTimeMillis()-ts));
		System.out.println(pw_hash);
		//To check whether a plaintext password matches one that has been hashed previously, use the checkpw method:
		long a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
		
		//The gensalt() method takes an optional parameter (log_rounds) that determines the computational complexity of the hashing:
		String weak_salt = BCrypt.gensalt(4);
		String stronger_salt_11 = BCrypt.gensalt(11);
		String stronger_salt_12 = BCrypt.gensalt(12);
		String stronger_salt_13 = BCrypt.gensalt(13);
		String stronger_salt_14 = BCrypt.gensalt(14);
		String strongest_salt = BCrypt.gensalt(31);
		//The amount of work increases exponentially (2**log_rounds), so each increment is twice as much work. The default log_rounds is 10, and the valid range is 4 to 31.
		System.out.println("Salts:\n"+weak_salt+" (4/31)\n"+stronger_salt_11+" (11/31)\n"+stronger_salt_12+" (12/31)\n"+stronger_salt_13+" (13/31)\n"+stronger_salt_14+" (14/31)\n"+strongest_salt+" (31/31)\n");
		
		long ts0 = System.currentTimeMillis();
		pw_hash = BCrypt.hashpw(passw, weak_salt);
		System.out.println("BCrypt mod work factor: (with strength 4) - encode time: " + (System.currentTimeMillis()-ts0));
		System.out.println(pw_hash);
		a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
		
		ts0 = System.currentTimeMillis();
		pw_hash = BCrypt.hashpw(passw, stronger_salt_11);
		System.out.println("BCrypt mod work factor: (with strength 11) - encode time: " + (System.currentTimeMillis()-ts0));
		System.out.println(pw_hash);
		a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
		
		ts0 = System.currentTimeMillis();
		pw_hash = BCrypt.hashpw(passw, stronger_salt_12);
		System.out.println("BCrypt mod work factor: (with strength 12) - encode time: " + (System.currentTimeMillis()-ts0));
		System.out.println(pw_hash);
		a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
		
		ts0 = System.currentTimeMillis();
		pw_hash = BCrypt.hashpw(passw, stronger_salt_13);
		System.out.println("BCrypt mod work factor: (with strength 13) - encode time: " + (System.currentTimeMillis()-ts0));
		System.out.println(pw_hash);
		a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
		
		ts0 = System.currentTimeMillis();
		pw_hash = BCrypt.hashpw(passw, stronger_salt_14);
		System.out.println("BCrypt mod work factor: (with strength 14) - encode time: " + (System.currentTimeMillis()-ts0));
		System.out.println(pw_hash);
		a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
		
		ts0 = System.currentTimeMillis();
		pw_hash = BCrypt.hashpw(passw, strongest_salt);
		System.out.println("BCrypt mod work factor: (with strength 31) - encode time: " + (System.currentTimeMillis()-ts0));
		System.out.println(pw_hash);
		a = System.currentTimeMillis();
		assertTrue(BCrypt.checkpw(passw, pw_hash));
		System.out.println("Time to verify: " + (System.currentTimeMillis()-a));
		System.out.println();
	}
	
	
	@Order(11)
	@Test
	void Argon2PasswordEncoder() {
		//The Argon2PasswordEncoder implementation uses the Argon2 algorithm to hash the passwords. 
		//Argon2 is the winner of the Password Hashing Competition. In order to defeat password cracking on custom hardware,
		//Argon2 is a deliberately slow algorithm that requires large amounts of memory. Like other adaptive one-way functions, 
		//it should be tuned to take about 1 second to verify a password on your system. 
		//The current implementation if the Argon2PasswordEncoder requires BouncyCastle.
		// Create an encoder with all the defaults
		encoder = new Argon2PasswordEncoder();
		result = encoder.encode(passw);
		System.out.println("Argon2: - encode time: " + (System.currentTimeMillis()-ts));
	}
	
	
	//// Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
			//This algorithm is a good choice when FIPS certification is required.
	
	////SCryptPasswordEncoder encoder = new SCryptPasswordEncoder();
	
	
	/* BEST TUNED:
	 * Strength 10-13
	10: ~100ms
	11: ~185
	12: ~370
	13: ~750
	14: ~1600
	
	encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	Delegating psw encoder: - encode time: 115
	Time to verify: 104
	
	encoder = new BCryptPasswordEncoder();
	BCrypt: (with strength 10) - encode time: 118
	Time to verify: 109
	
	SecureRandom sr = new SecureRandom(new SecureRandom().generateSeed(20));
	encoder = new BCryptPasswordEncoder(12, sr); //strength 4-31, default is 10
	BCrypt with SecureRandom: (with strength 12) - encode time: 363
	Time to verify: 392
	
	encoder = new BCryptPasswordEncoder(11); //4-31
	BCrypt: (with strength 11) - encode time: 184
	Time to verify: 188
	
	encoder = new BCryptPasswordEncoder(12); //4-31
	BCrypt: (with strength 12) - encode time: 387
	Time to verify: 386
	
	
	String salt = BCrypt.gensalt(11);
	pw_hash = BCrypt.hashpw(passw, salt);
	BCrypt.checkpw(passw, pw_hash);
	
	BCrypt mod work factor: (with strength 10) - encode time: 96
	Time to verify: 97
	BCrypt mod work factor: (with strength 11) - encode time: 185
	Time to verify: 186
	BCrypt mod work factor: (with strength 12) - encode time: 359
	Time to verify: 364
	BCrypt mod work factor: (with strength 13) - encode time: 734
	Time to verify: 769
	BCrypt mod work factor: (with strength 14) - encode time: 1594
	Time to verify: 1595
	 * */
	
	
	/** OTROS TEMAS DE INTERÉS
	 * 
	 * Cross Site Request Forgery (CSRF)
	 * 
	 * 		Un site malicioso tiene un form o con JS envía un request automático.
	 * 			<form method="post" action="https://bank.example.com/transfer">
	 * 		si no has cerrado sesión con tu banco, se enviará la SESSION de la cookie a tu banco y quizás le hagas una transferencia.
	 * 
	 * Spring provides two mechanisms to protect against CSRF attacks:
     *		The Synchronizer Token Pattern (se envía por parámetro o header, nunca en la coockie, pq se setearía junto al request del attacker)
     *		Specifying the SameSite Attribute on your session cookie
	 * 
	 * When to use CSRF protection ?
	 * 		When should you use CSRF protection? Our recommendation is to use CSRF protection for any request that could be processed by a browser by normal users.
	 * 		If you are only creating a service that is used by non-browser clients, you will likely want to disable CSRF protection.
	 * 
	 * 
	 * Cross Site Scripting (XSS)
	 * 
	 * 		Un site fue víctima de un ataque, pero no profundizamos en el tema.
	 * */
	
}
