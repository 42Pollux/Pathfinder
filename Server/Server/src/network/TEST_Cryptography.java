package network;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TEST_Cryptography {

	private KeyPair rsaKeyServer;
	private KeyPair rsaKeyClient;
	private SecretKey aesKey;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		
		Cryptography.initialize();
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		rsaKeyServer = keyGen.genKeyPair();
		rsaKeyClient = keyGen.genKeyPair();
		
		KeyGenerator gen = KeyGenerator.getInstance("AES");
		gen.init(128); // for 256 install JCE from oracle
		aesKey = gen.generateKey();
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testEncryptDecryptText() {
		fail("skipping");
		String msg1 = Cryptography.encryptText("Hello World!", aesKey);
		System.out.println(msg1);
		Cryptography.setAesKey(aesKey);
		String msg2 = Cryptography.decryptText(msg1);
		System.out.println(msg2);
		assertEquals(msg2, "Hello World!");
	}

	@Test
	void testAESEncryptDecryptWithRSAKeys() {
		//fail("skipping");
		// 1. client generates an RSA KeyPair and an AES Key, server already has RSA KeyPair
		// 2. client encrypts AES Key with servers public key
		Cryptography.setServerPublicKey(Base64.getEncoder().encodeToString(Cryptography.rsaKeys.getPublic().getEncoded()));
		System.out.println("aes key before: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
		byte[] encrypted_key = Cryptography.encryptKeyRSA(aesKey);
		System.out.println("aes key meanwhile: " + Base64.getEncoder().encodeToString(encrypted_key));
		// 3. client sends encrypted AES key, server decrypts AES key
		SecretKey decrypted_key = Cryptography.decryptKeyRSA(encrypted_key);
		System.out.println("aes key after: " + Base64.getEncoder().encodeToString(decrypted_key.getEncoded()));
		assertEquals(aesKey, decrypted_key);
	}

}
