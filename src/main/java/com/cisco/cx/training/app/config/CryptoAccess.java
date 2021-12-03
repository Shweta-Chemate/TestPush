package com.cisco.cx.training.app.config;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class CryptoAccess<T extends Serializable> {
	private static KeyGenerator keyGen;
	private char[] transformation = "AES".toCharArray();
	private SecretKey secretKey;
	private static final int KEY_SIZE=256;

	public static final CryptoAccess<String> CRYPTO_STRING = new CryptoAccess<>();

	public CryptoAccess(){
		secretKey = getkeyGen(transformation).generateKey();
	}

	private static KeyGenerator getkeyGen(char[] transformation) {
		if (CryptoAccess.keyGen!=null) {
			return CryptoAccess.keyGen;
		}
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance(new String(transformation));
			keyGen.init(KEY_SIZE);
			CryptoAccess.keyGen=keyGen;
			return CryptoAccess.keyGen;

		} catch (NoSuchAlgorithmException | InvalidParameterException  e) {
			throw new IllegalStateException("FATAL:cannot create key generator",e);
		}
	}
	public SealedObject seal(T object) {
		try {
			Cipher cipher = Cipher.getInstance(new String(transformation));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
			return new SealedObject(object,cipher);
		} catch (NoSuchAlgorithmException  | NoSuchPaddingException |  InvalidKeyException
				| IllegalBlockSizeException |	IOException e) {
			throw new IllegalStateException("cannot create sealed object for given objects",e);
		}
	}

	@SuppressWarnings("unchecked")
	public T unseal(SealedObject object) {
		try {
			if (object==null) {
				return null;
			}
			Cipher cipher = Cipher.getInstance(new String(transformation));
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
			return (T) object.getObject(cipher);
		} catch (NoSuchAlgorithmException|NoSuchPaddingException|InvalidKeyException 
	|IOException|ClassNotFoundException |IllegalBlockSizeException|BadPaddingException e) {
			throw new IllegalStateException("cannot create sealed object for given objects",e);
		} 
	}
}
