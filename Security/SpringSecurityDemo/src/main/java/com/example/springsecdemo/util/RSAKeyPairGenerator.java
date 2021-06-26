package com.example.springsecdemo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyPairGenerator {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAKeyPairGenerator(int keysize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keysize = keysize <= 1024 ? 
        		1024 : keysize >= 4096 ?
        				4096 : 1024*(keysize/1024);
        keyGen.initialize(keysize);//1024, 2048, 3072, 4096..
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public void exportKeys(String path) throws IOException {
    	writeToFile(path+"/RSA/publicKey", Base64.getEncoder().encodeToString(publicKey.getEncoded()).getBytes());
    	writeToFile(path+"/RSA/privateKey", Base64.getEncoder().encodeToString(privateKey.getEncoded()).getBytes());
    	
    	System.out.println("\nPUBLIC KEY BASE64 ENCODED: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    	System.out.println("\nPRIVATE KEY BASE64 ENCODED: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    	System.out.println("\nPUBLIC KEY X.509 FORMAT: " + publicKey);
    	System.out.println("\nPRIVATE KEY X.509 FORMAT: " + privateKey);
    }
    
    private void writeToFile(String path, byte[] key) throws IOException {
    	File f = new File(path);
    	f.getParentFile().mkdirs();
        try(FileOutputStream fos = new FileOutputStream(f)) {
        	fos.write(key);
        	fos.flush();
        }
    }

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}
    
}