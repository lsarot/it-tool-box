package com.example.springsecdemo.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class RSAUtil {

	/**
	 * SI LA PUBLIC Y PRIVATE KEY ESTÁN EN BASE64, CON ESTO RECUPERAMOS LA PUBLIC KEY
	 * */
	public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {e.printStackTrace();}
        return publicKey;
    }
	
	/**
	 * SI LA PUBLIC Y PRIVATE KEY ESTÁN EN BASE64, CON ESTO RECUPERAMOS LA PRIVATE KEY
	 * */
	public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        try {
        	PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        	KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {e.printStackTrace();}
        return privateKey;
    }
	
	//-----------------------
	
	/** CUANDO SE ENCRIPTA CON PUBLIC, SE DESENCRIPTA CON PRIVATE.. Y VICEVERSA
	 *   NOS DEVUELVE BYTES ENCRIPTADOS
	 * @throws NoSuchProviderException 
	 * @throws UnsupportedEncodingException 
	 * */
	public static byte[] encrypt(String data, Key key) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException {
		//Reminder: Cryptographic implementations in the JDK are distributed through several different providers ("Sun", "SunJSSE", "SunJCE", "SunRsaSign") for both historical reasons and by the types of services provided. General purpose applications SHOULD NOT request cryptographic services from specific providers.
		Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-256AndMGF1Padding"); //"RSA/ECB/PKCS1Padding" //Available on: https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data.getBytes("UTF-8"));
	}
	
	public static String decrypt(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(data), "UTF-8");
    }
	
}

/** https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
Every implementation of the Java platform is required to support the following standard Cipher transformations with the keysizes in parentheses:

    AES/CBC/NoPadding (128)
    AES/CBC/PKCS5Padding (128)
    AES/ECB/NoPadding (128)
    AES/ECB/PKCS5Padding (128)
    DES/CBC/NoPadding (56)
    DES/CBC/PKCS5Padding (56)
    DES/ECB/NoPadding (56)
    DES/ECB/PKCS5Padding (56)
    DESede/CBC/NoPadding (168)
    DESede/CBC/PKCS5Padding (168)
    DESede/ECB/NoPadding (168)
    DESede/ECB/PKCS5Padding (168)
    RSA/ECB/PKCS1Padding (1024, 2048)
    RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
    RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)

 * */
/* NOTE:
 * 1. RSA/ECB/PKCS1Padding   has been known to be insecure, you should use:
 * 2. RSA/None/OAEPWithSHA1AndMGF1Padding
 * 3. RSA/ECB/OAEPWithSHA-256AndMGF1Padding   (se lee que ECB es un error de nomenclatura ya que ECB es de alg simétricos como AES)
 * 4. RSA/None/OAEPWithSHA-256AndMGF1Padding
 *
 * 1.   took 122ms to encrypt/decrypt 'hello world' with a 1024 bits key-pair
 * 1.   took 527ms to encrypt/decrypt 'hello world' with a 2048 bits key-pair
 * 4y2. took 257ms to encrypt/decrypt 'hello world' with a 1024 bits key-pair
 * 4y2. took 674ms to encrypt/decrypt 'hello world' with a 2048 bits key-pair
 *
 * consider it would take half the time since client encrypts and server decrypts.
 * 4 with 1024b key pair will work!
 * We use this just to hide the AES key which is faster
 */
