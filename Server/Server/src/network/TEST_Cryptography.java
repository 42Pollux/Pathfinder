package network;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

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
		//fail("skipping");
		String msg1 = Cryptography.encryptText("Hello World!", aesKey);
		System.out.println(msg1);
		String msg2 = Cryptography.decryptText(msg1, aesKey);
		System.out.println(msg2);
		assertEquals(msg2, "Hello World!");
	}

	@Test
	void testAESEncryptDecryptWithRSAKeys() {
		// 1. client generates an RSA KeyPair and an AES Key, server already has RSA KeyPair
		// 2. client encrypts AES Key with servers public key
		SecretKey encrypted_key = Cryptography.encryptKeyRSA(rsaKeyServer.getPublic().getEncoded(),  aesKey);
		// 3. client sends encrypted AES key, server decrypts AES key
		SecretKey decrypted_key = Cryptography.decryptKeyRSA(encrypted_key.getEncoded());
		assertEquals(aesKey, decrypted_key);
	}

}
