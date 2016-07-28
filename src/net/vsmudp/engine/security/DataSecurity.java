package net.vsmudp.engine.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

public class DataSecurity {
	
	Cipher cipherEnc;
	Cipher cipherDnc;
	
	SecretKey secretKey;
	IvParameterSpec initVector;
	
	int initToken;
	
	private static DataSecurity INSTANCE;
	public static final int CIPHER_FOR_ENCRYPTION;
	public static final int CIPHER_FOR_DECRYPTION;
	
	static {
		INSTANCE = null;
		CIPHER_FOR_ENCRYPTION = 15;
		CIPHER_FOR_DECRYPTION = 20;
	}
	
	DataSecurity() {
		cipherEnc = cipherDnc = null;
		secretKey = null;
		initVector = null;
		initToken = 0;
	}
	
	public static DataSecurity getInstance() {
		if (INSTANCE == null) INSTANCE = new DataSecurity();
		return INSTANCE;
	}
	
	public static SecurityInitializer getInitializer() {
		return SecurityInitializer.getInitializer();
	}
	
	public Cipher getCipher(int purpose) {
		Cipher res = null;
		if (purpose == CIPHER_FOR_ENCRYPTION) res = cipherEnc;
		else if (purpose == CIPHER_FOR_DECRYPTION) res = cipherDnc;
		else res = null;
		return res;
	}
	
	public SecretKey getKey() {
		return secretKey;
	}
	
	public IvParameterSpec getInitializationVector() {
		return initVector;
	}
	
	public boolean isUsable() {
		boolean res = false;
		if (cipherEnc == null || cipherDnc == null) res = false;
		else res = true;
		return res;
	}

}
