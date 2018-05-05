package ui.pathfinder;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {
    private static KeyPair rsa_keys;
    private static PublicKey server_public_key;
    private static SecretKey aes_key;

    // no key signing supported yet, AES keys or only encrypted once with RSA
    // AES 256 requires optional JCE components

    public Cryptography() {
        // TODO Auto-generated constructor stub
    }

    public static void initialize() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            rsa_keys = keyGen.genKeyPair();

            // session key
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128); // for 256 install JCE from oracle
            aes_key = gen.generateKey();

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void setServerPublicKey(String key){
        try{

            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT)));
            server_public_key = publicKey;
        } catch (Exception e){

        }
    }


    public static String getRSAPublicKey() {
        return Base64.encodeToString(rsa_keys.getPublic().getEncoded(), Base64.DEFAULT);
    }

    public static String getAESEncryptedKey(){
        return Base64.encodeToString(encryptKeyRSA(server_public_key.getEncoded(), aes_key).getEncoded(), Base64.DEFAULT);
    }

    public static String encryptText(String text) {
        Cipher c;
        try {
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, aes_key, new IvParameterSpec(new byte[16]));

            byte[] encrypted_text_as_bytes = c.doFinal(text.getBytes("UTF8"));
            String encrypted_text = Base64.encodeToString(encrypted_text_as_bytes, Base64.DEFAULT);

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
            c.init(Cipher.DECRYPT_MODE, aes_key, new IvParameterSpec(new byte[16]));

            byte[] decrypted_text_as_bytes = c.doFinal(Base64.decode(text, Base64.DEFAULT));
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

