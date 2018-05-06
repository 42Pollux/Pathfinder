package network;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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

import server.OSDepPrint;

public class Cryptography {
	public static KeyPair rsa_keys; //only for testing public
	private static SecretKey aes_key;
	private static PublicKey server_public_key;
	
	// no key signing supported yet, AES keys or only encrypted once with RSA
	// AES 256 requires optional JCE components
	// defaults may differ from phone to phone, check string encoding and key encoding
	// Cryptography must be an object at least for server code
	
	public Cryptography() {
		// TODO Auto-generated constructor stub
	}
	
	public static void initialize() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			rsa_keys = keyGen.genKeyPair();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sets the server_public_key variable.
	 * 
	 * @param key				base64 encoded public key as string  
	 */
	public static void setServerPublicKey_base64Format(String key){
        try{

            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
            server_public_key = publicKey;
        } catch (Exception e){

        }
    }
	
	/**
	 * Gets the current RSA public key as base64 encoded string.
	 *
	 * @return					RSA public key as base64 string
	 */
	public static String getRSAPublicKey_base64Format() {
        return encodeBase64(rsa_keys.getPublic().getEncoded());
    }
	
	
	
	// ##################################################################################
    // server specific functions
	
	/**
	 * Sets the current aes_key.
	 * 
	 * @param decrypted_key		decrypted key as SecretKey object
	 */
	public static void setAesKey(SecretKey decrypted_key) {
		aes_key = decrypted_key;
	}
	
	
	
	// ##################################################################################
    // AES data encryption (curr: client and server encrypt/decrypt)
	
	/**
	 * Encrypts the given string with AES and ecodes it in base64 format. The aes_key 
	 * must be set for this function to work properly.
	 * 
	 * @param text				string to be encrypted
	 * @return					encrypted base64 string
	 */
	public static String encryptText(String text) {
		Cipher c;
		try {
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, aes_key, new IvParameterSpec(new byte[16]));
			
			byte[] encrypted_text_as_bytes = c.doFinal(text.getBytes("UTF8"));
			String encrypted_text = encodeBase64(encrypted_text_as_bytes);
			
			return encrypted_text;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Decrypts the given base64 encoded string with AES back into plaintext (no
	 * defaults set so far). The aes_key must be set for this function to work
	 * properly.
	 * 
	 * @param text				base64 string to be decrypted
	 * @return					decrypted string
	 */
	public static String decryptText(String text) {
		Cipher c;
		try {
			
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, aes_key, new IvParameterSpec(new byte[16]));
			
			byte[] decrypted_text_as_bytes = c.doFinal(decodeBase64(text));
			String decrypted_text = new String(decrypted_text_as_bytes);
			
			return decrypted_text;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	// ##################################################################################
    // RSA key decryption (curr: client encrypts, server decrypts)
	
	/**
	 * Decrypts an AES key given as byte-array and encrypted with the servers
	 * public key into an SecretKey object.
	 * 
	 * @param key				an RSA encrypted AES key
	 * @return					decrypted AES key as SecretKey object
	 */
	public static SecretKey decryptAESKeyWithRSA(byte[] key) {
		Cipher c;
		OSDepPrint.debug("key length: " + key.length);
		try {
			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.DECRYPT_MODE,  rsa_keys.getPrivate());
			byte[] key_bytes = c.doFinal(key);
			
			SecretKey decrypted_key = new SecretKeySpec(key_bytes, 0, key_bytes.length, "AES");
			return decrypted_key;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	// ##################################################################################
    // Base64 encode/decode functions, platform specific
	
	/**
	 * Encodes a byte-array into a base64 string
	 * 
	 * @param bytes				byte-array to encode
	 * @return					base64 string
	 */
	public static String encodeBase64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	/**
	 * Decodes a base64 string into a byte-array
	 * 
	 * @param str				base64 string
	 * @return					decoded byte-array
	 */
	public static byte[] decodeBase64(String str) {
		return Base64.getDecoder().decode(str);
	}
	
	

}
