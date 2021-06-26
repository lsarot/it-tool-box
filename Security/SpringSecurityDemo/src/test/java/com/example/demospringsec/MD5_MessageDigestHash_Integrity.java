package com.example.demospringsec;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

/** MD5
 * Abajo org.apache.commons.codec.digest.DigestUtils,  permite otras funciones de Hash, más seguras que MD5.
 * 
 * MD5 is a hash algorithm, meaning that it maps an arbitrary-length string to a string of some fixed length. (a 128-bit hash value)
 * The intent is to make it so that it is hard to start with the output of an MD5 hash and to recover some particular input that would hash to that output. 
 * Because there are infinitely many strings and finitely many outputs, it is not an encryption function, and given just the output it's impossible to determine which input produced that output.
 * However, MD5 has many cryptographic weaknesses and has been superseded by a variety of other hash functions (the SHA family). 
 * I would strongly suggest not using MD5 if cryptographic security is desired, since there are much better algorithms out there.
 * 
 * USADO PRINCIPALMENTE PARA VALIDAR QUE UN FICHERO NO SE CORROMPIÓ DURANTE EL ENVÍO (chequeas el MD5 Hash que te dicen con el que te genera a tí cuando lo descargas)
 * 
 * More secure hashing algorithm is SHA-256, and less is SHA-1
 * */
public class MD5_MessageDigestHash_Integrity {

	/**
	 * JDK
	 * */
	@Test
	public void givenPassword_whenHashing_thenVerifying()  throws NoSuchAlgorithmException {
	    String hash = "35454B055CC325EA1AF2126E27707052";
	    String password = "ILoveJava";
	         
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(password.getBytes());
	    //md.update(input);// PUEDE SER LLAMADO N VECES SI ESTAMOS RECIBIENDO UN STREAM POR EJEMPLO
	    byte[] digest = md.digest();
	    String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
	         
	    assertThat(myHash.equals(hash)).isTrue();
	}
	
	/**
	 * JDK
	 * */
	//@Test
	public void givenFile_generatingChecksum_thenVerifying() throws NoSuchAlgorithmException, IOException {
	    String filename = "src/test/resources/test_md5.txt";
	    String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
	         
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(Files.readAllBytes(Paths.get(filename)));
	    byte[] digest = md.digest();
	    String myChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
	         
	    assertThat(myChecksum.equals(checksum)).isTrue();
	}
	
	/**
	 * APACHE COMMONS
	 * */
	@Test
	public void givenPassword_whenHashingUsingCommons_thenVerifying()  {
	    String hash = "35454B055CC325EA1AF2126E27707052";
	    String password = "ILoveJava";
	    
	    String md5Hex = DigestUtils.md5Hex(password).toUpperCase();
	    //DigestUtils.sha256Hex("")
	    //DigestUtils.sha384("")  
	    //...
	    
	    assertThat(md5Hex.equals(hash)).isTrue();
	}
	
	/**
	 * GUAVA
	 * */
	//@Test
	public void givenFile_whenChecksumUsingGuava_thenVerifying() throws IOException {
	    String filename = "src/test/resources/test_md5.txt";
	    String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
	         
	    //HashCode hash = com.google.common.io.Files.hash(new File(filename), Hashing.md5());
	    //String myChecksum = hash.toString().toUpperCase();
	         
	    //assertThat(myChecksum.equals(checksum)).isTrue();
	}
}
