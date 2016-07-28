package net.vsmudp.engine.security;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.vsmudp.Application;
import cryptix.jce.provider.CryptixCrypto;

import java.util.concurrent.*;

public class SecurityInitializer implements Callable<DataSecurity>{
	
	static boolean isProviderAdded;
	static Provider pv;
	static final String STR_ALGO_SPEC;
	static SecurityInitializer INSTANCE;
	
	SecretKey key;
	IvParameterSpec iv;
	boolean isMultithreaded;
	boolean isBeingProcessed;
	int token;
	
	static {
		STR_ALGO_SPEC = "Blowfish/CBC/PKCS5Padding";
		pv = null;
		INSTANCE = null;
	}
	
	SecurityInitializer() {
		key = null;
		iv = null;
		token = -1;
		isMultithreaded = isBeingProcessed = false;
	}
	
	public static SecurityInitializer getInitializer() {
		if (INSTANCE == null) INSTANCE = new SecurityInitializer();
		return INSTANCE;
	}
	
	public void setSecretKey(byte[] data) {
		key = new SecretKeySpec(data, "Blowfish");
	}
	public void setInitVector(byte[] data) {
		iv = new IvParameterSpec(data);
	}
	public void setMultithreaded(boolean val) {
		isMultithreaded = val;
	}
	public void setToken(int tok) {
		token = tok;
	}
	
	public Future<DataSecurity> applySetting() throws Exception {
		Future<DataSecurity> res = null;
		if (isMultithreaded == false) {
			call();
		} else {
			Application app = Application.getInstance();
			ExecutorService exec = app.backgroundExecutor();
			res = exec.submit(this);
		}
		return res;
	}
	
	public DataSecurity call() throws Exception {
		
		if (isBeingProcessed == true) return null;
		
		isBeingProcessed = true;
		if (isProviderAdded == false) {
			pv = new CryptixCrypto();
			Security.addProvider(pv);
			isProviderAdded = true;
		}
		
		DataSecurity sec = DataSecurity.getInstance();
		if (shouldInitCipher(sec) == false) return sec;
		
		Cipher cipD = sec.cipherDnc;
		Cipher cipE = sec.cipherEnc;
		
		if (cipD == null) {
			cipD = Cipher.getInstance(STR_ALGO_SPEC, pv);
			cipE = Cipher.getInstance(STR_ALGO_SPEC, pv);
		}
		
		cipD.init(Cipher.DECRYPT_MODE, key, iv);
		cipE.init(Cipher.ENCRYPT_MODE, key, iv);
		
		sec.initToken = token;
		sec.secretKey = key;
		sec.initVector = iv;
		
		if (sec.cipherDnc == null) {
			sec.cipherDnc = cipD;
			sec.cipherEnc = cipE;
		}
		isBeingProcessed = false;
		
		return sec;
	}
	
	private boolean shouldInitCipher(DataSecurity sec) {
		
		if (sec.isUsable() == true) return true;
		else {
		
			SecretKey sKey= sec.secretKey;
			
			boolean isKeyMatched = key.equals(sKey);
			return !isKeyMatched;
		}
	}

}
