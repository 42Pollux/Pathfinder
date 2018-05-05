package network;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {
	private static KeyPair rsaKeys;
	private static SecretKey aesKey;
	
	// no key signing supported yet, AES keys or only encrypted once with RSA
	// AES 256 requires optional JCE components
	
	public Cryptography() {
		// TODO Auto-generated constructor stub
	}
	
	public static void initialize() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			rsaKeys = keyGen.genKeyPair();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// only set on server side
	public static void setAesKey(SecretKey decrypted_key) {
		aesKey = decrypted_key;
	}
	
	public static String getRSAPublicKey() {
		return Base64.getEncoder().encodeToString(rsaKeys.getPublic().getEncoded());
	}
	
	public static String encryptText(String text, SecretKey key) {
		Cipher c;
		try {
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			
			byte[] encrypted_text_as_bytes = c.doFinal(text.getBytes("UTF8"));
			String encrypted_text = Base64.getEncoder().encodeToString(encrypted_text_as_bytes);
			
			return encrypted_text;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String decryptText(String text) {
		Cipher c;
		try {
			
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(new byte[16]));
			
			byte[] decrypted_text_as_bytes = c.doFinal(Base64.getDecoder().decode(text));
			String decrypted_text = new String(decrypted_text_as_bytes);
			
			return decrypted_text;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static SecretKey encryptKeyRSA(byte[] server_public_key, SecretKey key) {
		Cipher c;
		PublicKey client_key;
		try {
			c = Cipher.getInstance("RSA");
			client_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(server_public_key));
			c.init(Cipher.ENCRYPT_MODE,  client_key);
			byte[] key_bytes = c.doFinal(key.getEncoded());
			
			SecretKey encrypted_key = new SecretKeySpec(key_bytes, 0, key_bytes.length, "AES");
			return encrypted_key;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static SecretKey decryptKeyRSA(byte[] key) {
		Cipher c;
		try {
			c = Cipher.getInstance("RSA");
			c.init(Cipher.DECRYPT_MODE,  rsaKeys.getPrivate());
			byte[] key_bytes = c.doFinal(key);
			
			SecretKey decrypted_key = new SecretKeySpec(key_bytes, 0, key_bytes.length, "AES");
			return decrypted_key;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	// Example of RSA key encryption with key signing
	
//	public static SecretKey encryptKeyRSA(byte[] server_public_key, SecretKey key) {
//		Cipher c;
//		PublicKey client_key;
//		try {
//			c = Cipher.getInstance("RSA");
//			c.init(Cipher.ENCRYPT_MODE,  rsaKeys.getPrivate());
//			client_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(server_public_key));
//			byte[] keyEncryptedOnce = c.doFinal(key.getEncoded());
//			c.init(Cipher.ENCRYPT_MODE,  client_key);
//			byte[] keyEncryptedTwice = c.doFinal(keyEncryptedOnce);
	
//			SecretKey encrypted_key = new SecretKeySpec(keyEncryptedTwice, 0, keyEncryptedTwice.length, "AES");
//			return encrypted_key;
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
//	
//	public static SecretKey decryptKeyRSA(byte[] client_public_key, byte[] key) {
//		Cipher c;
//		PublicKey client_key;
//		try {
//			c = Cipher.getInstance("RSA");
//			c.init(Cipher.DECRYPT_MODE,  rsaKeys.getPrivate());
//			client_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(client_public_key));
//			
//			byte[] keyDecryptedOnce = c.doFinal(key);
//			c.init(Cipher.DECRYPT_MODE,  client_key);
//			byte[] keyDecryptedTwice = c.doFinal(keyDecryptedOnce);
//			
//			SecretKey decrypted_key = new SecretKeySpec(keyDecryptedTwice, 0, keyDecryptedTwice.length, "AES");
//			return decrypted_key;
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	

}
