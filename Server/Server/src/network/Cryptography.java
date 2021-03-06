/**
 * @author pollux
 *
 */
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

import helper.OSDepPrint;

public class Cryptography {
	private static KeyPair rsa_keys;
	private SecretKey aes_key;
	
	// no key signing supported yet, AES keys or only encrypted once with RSA
	// AES 256 requires optional JCE components
	// defaults may differ from phone to phone, check string encoding and key encoding
	// initial vectory must be different each time and not zero all the time
	
	public Cryptography() {
		
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
	 * Gets the current RSA public key as base64 encoded string.
	 *
	 * @return					RSA public key as base64 string
	 */
	public String getRSAPublicKey_base64Format() {
        return encodeBase64(rsa_keys.getPublic().getEncoded());
    }
	
	
	
	// ##################################################################################
    // server specific functions
	
	/**
	 * Sets the current aes_key.
	 * 
	 * @param decrypted_key		decrypted key as SecretKey object
	 */
	public void setAESKey(SecretKey decrypted_key) {
		this.aes_key = decrypted_key;
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
	public String encryptText(String text) {
		Cipher c;
		try {
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, this.aes_key, new IvParameterSpec(new byte[16]));
			
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
	public String decryptText(String text) {
		Cipher c;
		try {
			
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, this.aes_key, new IvParameterSpec(new byte[16]));
			
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
	public SecretKey decryptAESKeyWithRSA(byte[] key) {
		Cipher c;
		//OSDepPrint.debug("key length: " + key.length);
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
	public String encodeBase64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	/**
	 * Decodes a base64 string into a byte-array
	 * 
	 * @param str				base64 string
	 * @return					decoded byte-array
	 */
	public byte[] decodeBase64(String str) {
		return Base64.getDecoder().decode(str);
	}
	
	

}
