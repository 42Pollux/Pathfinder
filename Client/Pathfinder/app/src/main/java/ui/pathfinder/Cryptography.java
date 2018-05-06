package ui.pathfinder;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import android.util.Base64;
import android.util.Log;

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
    // defaults may differ from phone to phone, check string encoding and key encoding

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

    /**
     * Sets the server_public_key variable.
     *
     * @param key				base64 encoded public key as string
     */
    public static void setServerPublicKey_base64Format(String key){
        try{

            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodeBase64(key)));
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
    // client specific functions

    /**
     * Calls the required functions in order to encrypt the aes_key.
     *
     * @return                  encrypted AES key in base64 format
     */
    public static String getAESEncryptedKey_base64Format(){
        return encodeBase64(encryptAESKeyWithRSA(aes_key));
    }



    // ##################################################################################
    // AES data encryption (curr: client and server encrypt/decrypt)

    /**
     * Encrypts the given string with AES and ecodes it in base64 format. The Cryptography
     * class must be initialized in order for this function to work properly.
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
     * defaults set so far). The Cryptography class must be initialized in order
     * for this function to work properly.
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
    // RSA key encryption (curr: client encrypts, server decrypts)

    /**
     * Encrypts an AES key given as SecretKey Object with RSA. The public
     * key of the server must be set for this function to work properly.
     *
     * @param key				AES key as SecretKey object
     * @return					encrypted AES key as byte-array
     */
    public static byte[] encryptAESKeyWithRSA(SecretKey key) {
        Cipher c;
        try {
            c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c.init(Cipher.ENCRYPT_MODE,  server_public_key);
            byte[] key_bytes = c.doFinal(key.getEncoded());

            Log.d("DEBUG1", "key length: " + key_bytes.length);
            return key_bytes;

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
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Decodes a base64 string into a byte-array
     *
     * @param str				base64 string
     * @return					decoded byte-array
     */
    public static byte[] decodeBase64(String str) {
        return Base64.decode(str, Base64.NO_WRAP);
    }



}
