package com.example.demospringsec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static org.junit.jupiter.api.Assertions.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import com.example.springsecdemo.util.RSAKeyPairGenerator;
import com.example.springsecdemo.util.RSAUtil;

/* There are basically two 2-way(reversible) encryption types:
 * Asymmetric: (RSA, ECC,..) uses public and private key. Ideal when there are 2 endpoints involved, such as VPN client/server, SSH, etc.
 * Symmetric: (AES, 3DES, RC4, Blowfish,..) only uses one private key.
 * * for both there are more algorithms!
 * */
/** RSA(Rivest–Shamir–Adleman) (is asymmetric encryption)
 * (stack overflow) I want to provide my application with simple licensing mechanism based on RSA algorithm.
 * 
 * Ambos comparten public key, pero servidor tiene private key.
 * Cliente encripta con public, servidor desencripta con private.. servidor encripta con private, cliente desencripta con public, esto si la public no se comparte sino con 1 cliente, si la public es algo que usan todos los que tengan la app, entonces hay que usar una solución híbrida (RSA con AES).
 * 		por eso, cada cliente debe tener una public key distinta, sino otro cliente que sabe la public key, puede ver msjes del servidor a otro cliente.
 * 		Para agilizar esto, todos comparten RSA public key, pero cada uno usa una private AES distinta, el servidor sólo debe descifrar la private AES y luego el msj con esa clave (que es mucho más rápido), y responde cifrando con la AES, que es de un cliente particular.
 * 
 * By default, the private key is generated in PKCS#8 format and the public key is generated in X.509 format.
 * Public/Private keys pueden ser de dif tamaño: 512,1024,2048bits.., se entregan Base64 encoded.
 * 
 * Each time we double the keysize (1024/2048/4096) to make it harder, decryption is 6-7 times slower.
 * In large payloads it will impact your system, so we combine RSA with AES (symmetric), how?:
 * 			Encrypt your payload with AES, then encrypt AES key with strong RSA, then send payload and key together.
 * 			The other endpoint will know how to decrypt AES key and then decrypt payload.
 * 			Then server response is encrypted with same AES key.
 * 			THIS IS KNOWN AS HYBRID ENCRYPTION.
 * 
 * * Generate keys:
 * By code, see Java code in this class.
 * By CLI,      https://gitlab.com/help/ssh/README#generating-a-new-ssh-key-pair
 * in Unix-like OS:
 * 			ssh-keygen -t rsa -b 2048 -C "email@example.com"     //The -C flag, with a quoted comment such as an email address, is an optional way to label your SSH keys.
 * in Windows:
 * 			Estas keys son igual que las que generas con Putty, la SSH key
 * 
 * Notas:
 * En mac, el fichero hosts, si requiere conectarse a un endpoint usando SSH, guarda la public-key junto al hostname/ip		
 * 
 * */
/* CERTIFICADO X.509 O SSL CERTIFICATE
 * An X.509 certificate is a digital certificate that uses the widely accepted international X.509 public key infrastructure (PKI) standard to verify that a public key belongs to the user, computer or service identity contained within the certificate.
 * An X.509 certificate contains information about the identity to which a certificate is issued and the identity that issued it. Standard information in an X.509 certificate includes:
 *  Version – which X.509 version applies to the certificate (which indicates what data the certificate must include)
 *  Serial number – the identity creating the certificate must assign it a serial number that distinguishes it from other certificates
 *  Algorithm information – the algorithm used by the issuer to sign the certificate
 *  Issuer distinguished name – the name of the entity issuing the certificate (usually a certificate authority)
 *  Validity period of the certificate – start/end date and time
 *  Subject distinguished name – the name of the identity the certificate is issued to
 *  Subject public key information – the public key associated with the identity
 *  Extensions (optional)*/

public class RSAencryption {

	private static final String PUBLIC_KEY_B64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmdokW+0AtiM3fqjWiL3GKaTe95IhOEg9yQs7nAYiiRPAIYxJL1Shg8GLM5+2MXiun2jIr6EZlqcnDauiyKidwBdp3W8rqgOChGypK2OGTdp39xggn1FvU9tnFWfN5hTudMiJz1e82UbCYxBaCKdLgaPxzTbsY5u9KOMnmN28aHQAaQl2xinlkS7fhraTtUJgA5O47VySBmtuByN31O2n8oK2RJSESu9ndhUHf1wncKkBydMvJWlsI9wLnkmIPRhdBxaj0VKsEhbR3PZw1QNlOf1kqX37Trxus40nc0qS9B33j198JX9leWmoxz+7kHgsKfSXdg/PorCqMa/U8gceMQIDAQAB";
	
	private static final String PRIVATE_KEY_B64 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCZ2iRb7QC2Izd+qNaIvcYppN73kiE4SD3JCzucBiKJE8AhjEkvVKGDwYszn7YxeK6faMivoRmWpycNq6LIqJ3AF2ndbyuqA4KEbKkrY4ZN2nf3GCCfUW9T22cVZ83mFO50yInPV7zZRsJjEFoIp0uBo/HNNuxjm70o4yeY3bxodABpCXbGKeWRLt+GtpO1QmADk7jtXJIGa24HI3fU7afygrZElIRK72d2FQd/XCdwqQHJ0y8laWwj3AueSYg9GF0HFqPRUqwSFtHc9nDVA2U5/WSpfftOvG6zjSdzSpL0HfePX3wlf2V5aajHP7uQeCwp9Jd2D8+isKoxr9TyBx4xAgMBAAECggEAY9DiW/2Uw5Zvj65MmaS95xC/U9Gr2c8Mvt2Zy+0zBMoakelpJdr7evbbsB+DXe9VI7kg0slrR5GkxbrAPv0ec5z9IxvYMVJboDy7OJo2bVY7FMXDjHmSB87Fv158eDTGTGA7AJmEvVaqEQlv0ENxumc6HpDupuVIwTk23Henn//zyOErlYe3R2rj1crrgLXYIBcrC9y+5JglI5yuB5VU2D384Ow7MLhVpvDOYKUV+eAj5OwGPWX8WvqsdJ7S4NCMg7wsK1L7+V7e/r34GdNb2YyjwKK33OO0JMmDAN3Q3ZR+KC1mkdW2CSG6kcSsn9iX3Mbdz1C8O8JW2J0io0QWkQKBgQDzatcTuWsGrEtZLk1E3NGheWv8TTi0rCqC2xTxotlx97HsMKQs0OxhBW2l2ysH8nGSwBopSkoeZauCWTKlZKZBAIoM/8ZK25r//2PfxStRnA8mFTmYUf1Ji3JIfv1sgFDSDDh6wXGk4Sfhpr5B9J3KFPJVcxKSYVW/QxWGUstypQKBgQChzhQcgWQ42011vFvWbMeboA6owbCAOF46zJRSSEmjHz4e+/mpSb1Wdhsfw8GgGriz1/9EWxBQMxuqVaUgGoo4kAlxnmshgROmrKKnv+icnyNMWmqFCA5wevve8i1WdEAxvJHaTVjyhT6bTehNdHIDf52LNLZHyGMVlwegPnxjnQKBgEmPRvHJ/cLlfFu191nRFXSjufNj7rgCs1IaHMks+mNLhDQpOuPkOxrSxiDyC536MUI01XMW5TEsblkU1Y5PzUIlhJKLFQR4Ou4T4r2z2vRtodJTZbVXSaDZCC9KWRFZ8ZKYaNUH8mzuMdwqRKKv5qM0E0upW7pqubvz7ORLzvmVAoGAOsCuss8VCkQvc0HrAwhKzqqmyAZUxaoyJR2l+d6/xliQ4QVT2XGqK5wFK1qUk0NAsCUNTs/WM5jrQcWJiQe6CuHaXARPGEJbVVk4UrsvhreORKpMJXQQci1mCcVcSxk5OhYjy+XsF1vkqNX4NS/EOpr7JNIGIjiUKgHGRhyKZE0CgYEAv1HESyqagSDo4nqYHUgJEjJxsoBcXPWg5YH0C71UhbZeGJZYkS7TSxMlLEBBgZTbnBB/jWr51L7TCBs4u7/bpBEKoV4GmMK7juNQ4UXbSPiIrUQ03Dqyq5rUSDb8puIl5UThHBvyeZjejqKrPnsnDrPkR3aA6/qmWJ2gp48STug=";
	
	
	//@Test
	void createNewRSAPublicAndPrivateKey() throws IOException, NoSuchAlgorithmException {
		RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator(1024);
        keyPairGenerator.exportKeys("/Users/Leo/Desktop");
	}
	
	@Test
	void encryptThenDecrypt_WithRSA() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException {
		
		//Para poder usar otros algs como   RSA/None/OAEPWithSHA1AndMGF1Padding   tuvimos que agregar un dependency y llamar este método.
		Security.addProvider(new BouncyCastleProvider());
		
		PublicKey publicKey = RSAUtil.getPublicKey(PUBLIC_KEY_B64);
		PrivateKey privateKey = RSAUtil.getPrivateKey(PRIVATE_KEY_B64);
		
		String originalData = "Learn to code and you could raise an empire!";
		
		byte[] encrypted = RSAUtil.encrypt(originalData, publicKey);
		System.out.println("Encrypted msg: " + new String(encrypted));
		String decryptedMsg = RSAUtil.decrypt(encrypted, privateKey);
		System.out.println("Decrypted msg: " + decryptedMsg);

		assertTrue(originalData.equals(decryptedMsg));
		
		//AHORA CIFRAMOS CON LA PRIVADA
		
		encrypted = RSAUtil.encrypt(originalData, privateKey);
		System.out.println("Encrypted msg: " + new String(encrypted));
		decryptedMsg = RSAUtil.decrypt(encrypted, publicKey);		
		System.out.println("Decrypted msg: " + decryptedMsg);

		assertTrue(originalData.equals(decryptedMsg));
	}
	
}
