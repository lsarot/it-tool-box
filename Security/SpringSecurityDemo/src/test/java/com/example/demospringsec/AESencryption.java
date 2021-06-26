package com.example.demospringsec;

import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import com.example.springsecdemo.util.AESUtil;
import com.google.common.io.BaseEncoding;

/** AES (Advanced Encryption Standard)
 *  https://www.devglan.com/corejava/java-aes-encypt-decrypt
 *  
 *  Existen 2 tipos básicos de algoritmos (para Two-Way encryption), asimétrico (public/private key) y simétrico (only 1 key).
 *  Some examples of symmetric encryptions are Twofish, Blowfish, 3DES, AES.
 *  AES is faster and more secure than 3DES
 *  AES, al ser simétrico, es mucho más rápido que RSA.
 *  Se usa típicamente en sistemas como bases de datos.
 *  AES is the industry standard as of now as 
 *  		it allows 128 bit, 192 bit and 256 bit encryption (key size). With a secret key of 16, 24, 32 chars length respectively
 *  		
 *  Acepta 2 modos, ECB y CBC (usa un IV, initialization vector. Recomendado)
 *  		ECB: 
 *  				divide la entrada en bloques, los cifra con la key y genera igual cantidad de bloques, pero cifrados.
 *  		CBC:
 *  				divide igual en bloques el texto de extrada, pero el IV permite que N bloques iguales de la entrada generen bloques distintos al cifrar.
 *  
 * */
public class AESencryption {

	@Test
	void encryptThenDecrypt_WithAES() {
		
		String originalString = "Learn to code and you could raise an empire!";
		System.out.println("Original String to encrypt - " + originalString);
		
		AESUtil aesUtil = new AESUtil();
		
		byte[] encrypted = aesUtil.encrypt(originalString);
		System.out.println("Encrypted String - " + encrypted);
		
		
		String warmingguava = encodeUsingGuava(encrypted);
		
		
		long ts = System.currentTimeMillis();
		String b64Str = Base64.getEncoder().encodeToString(encrypted);
		System.out.println(b64Str);
		encrypted = Base64.getDecoder().decode(b64Str);
		System.out.println(System.currentTimeMillis()-ts);
		
		ts = System.currentTimeMillis();
		String hexStr = encodeUsingGuava(encrypted);
		System.out.println(hexStr);
		encrypted = decodeUsingGuava(hexStr);
		System.out.println(System.currentTimeMillis()-ts);
		
		ts = System.currentTimeMillis();
		hexStr = encodeUsingDataTypeConverter(encrypted);
		System.out.println(hexStr);
		//encrypted = decodeUsingDataTypeConverter(hexStr);
		System.out.println(System.currentTimeMillis()-ts);
		
		
		String decrypted = aesUtil.decrypt(encrypted);
		System.out.println("After decryption - " + decrypted);
		
		System.out.println("are equal: " + originalString.equals(decrypted));
	}
	
	
	public String encodeUsingGuava(byte[] bytes) {
	    return BaseEncoding.base16().encode(bytes);
	}

	public byte[] decodeUsingGuava(String hexString) {
	    return BaseEncoding.base16().decode(hexString.toUpperCase());
	}
	
	public String encodeUsingDataTypeConverter(byte[] bytes) {
	    return DatatypeConverter.printHexBinary(bytes);
	}

	public byte[] decodeUsingDataTypeConverter(String hexString) {
	    return DatatypeConverter.parseHexBinary(hexString);
	}
	
	
}
