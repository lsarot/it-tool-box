package com.example.springsecdemo.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	/*
	 * If the same key is used to encrypt all the plain text and if an attacker finds this key then all the cipher can be decrypted in the similar way.
	 * We can use salt and iterations to improve the encryption process further. (NO LO HICIMOS AQUÍ PARECE)
	 * */
	
	private static final String key = "clave aes prueba";// "aesEncryptionKey"; //note is 16 bytes cause we are using 128bit size
			//For banking-grade encryption, the symmetric keys must be created using a random number generator (RNG) that is certified according to industry standards, such as FIPS 140-2.
	private static final String initVector = "encryptionInitVe";//should be randomized.. also, note is 16 bytes cause we are using 128bit size

	
	private Cipher cipherAES;
	private IvParameterSpec iv;
	
	
	public AESUtil() {
		try {
			//Reminder: Cryptographic implementations in the JDK are distributed through several different providers ("Sun", "SunJSSE", "SunJCE", "SunRsaSign") for both historical reasons and by the types of services provided. General purpose applications SHOULD NOT request cryptographic services from specific providers.
			this.cipherAES = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public byte[] encrypt(String data) {
		byte[] encrypted = null;
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES"); //creo que acepta AES-192, AES-256

			//se puede llamar a init N veces, en modos distintos.
			cipherAES.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			encrypted = cipherAES.doFinal(data.getBytes("UTF-8"));
		} catch (Exception ex) {ex.printStackTrace();}
		return encrypted;
	}
	
	
	public String decrypt(byte[] encrypted) {
		String content = null;
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			cipherAES.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			
			byte[] original = cipherAES.doFinal(encrypted);
			content = new String(original, "UTF-8");
		} catch (Exception ex) {ex.printStackTrace();}
		return content;
	}
	
}
/*
AES ENCRYPTION IV AND KEY
https://crypto.stackexchange.com/questions/3965/what-is-the-main-difference-between-a-key-an-iv-and-a-nonce

* RESUMEN:
* Podemos usar un mismo IV siempre que usemos una key distinta para cada mensaje. IV no tiene que ser privado. Puede ser sólo ceros (0).
* Podemos usar una misma key siempre que usemos distintos IV.
* Lo importante es que el par key+IV nunca se repita.
* Debo usar distintas key y mantener IV, pq el método init depende de la key y del IV también.


A key, in the context of symmetric cryptography, is something you keep secret. Anyone who knows your key (or can guess it) can decrypt any data you've encrypted with it (or forge any authentication codes you've calculated with it, etc.).

(There's also "asymmetric" or public key cryptography, where the key effectively has two parts: the private key, which allows decryption and/or signing, and a public key (derived from the corresponding private key) which allows encryption and/or signature verification.)

An IV or initialization vector is, in its broadest sense, just the initial value used to start some iterated process. The term is used in a couple of different contexts and implies different security requirements in each of them. For example, cryptographic hash functions typically have a fixed IV, which is just an arbitrary constant which is included in the hash function specification and is used as the initial hash value before any data is fed in.

Conversely, most block cipher modes of operation require an IV which is random and unpredictable, or at least unique for each message encrypted with a given key. (Of course, if each key is only ever used to encrypt a single message, one can get away with using a fixed IV.) This random IV ensures that each message encrypts differently, such that seeing multiple messages encrypted with the same key doesn't give the attacker any more information than just seeing a single long message. In particular, it ensures that encrypting the same message twice yields two completely different ciphertexts, which is necessary in order for the encryption scheme to be semantically secure.

In any case, the IV never needs to be kept secret — if it did, it would be a key, not an IV. Indeed, in most cases, keeping the IV secret would not be practical even if you wanted to since the recipient needs to know it in order to decrypt the data (or verify the hash, etc.).

A nonce, in the broad sense, is just "a number used only once". The only thing generally demanded of a nonce is that it should never be used twice (within the relevant scope, such as encryption with a particular key). The unique IVs used for block cipher encryption qualify as nonces, but various other cryptographic schemes make use of nonces as well.

There's some variation about which of the terms "IV" and "nonce" is used for different block cipher modes of operation: some authors use exclusively one or the other, while some make a distinction between them. For CTR mode, in particular, some authors reserve the term "IV" for the full cipher input block formed by the concatenation of the nonce and the initial counter value (usually a block of all zero bits), while others prefer not to use the term "IV" for CTR mode at all. This is all complicated by the fact that there are several variations on how the nonce/IV sent with the message in CTR mode is actually mapped into the initial block cipher input.

Conversely, for modes other than CTR (or related modes such as EAX or GCM), the term "IV" is almost universally preferred over "nonce". This is particularly true for CBC mode since it has requirements on its IV (specifically, that they are unpredictable) which go beyond the usual requirement of uniqueness expected of nonces.
*/
