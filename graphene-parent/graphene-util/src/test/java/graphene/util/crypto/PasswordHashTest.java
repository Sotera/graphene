package graphene.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.Test;

public class PasswordHashTest {
	@Test
	public void testSalt() {
		PasswordHash p = new PasswordHash();

		try {
			// System.out.println(p.createHash("password"));
			byte[] s = p.pbkdf2("password".toCharArray(), "salt".getBytes(),
					p.PBKDF2_ITERATIONS, p.HASH_BYTE_SIZE);
			System.out.println(s);
			System.out.println("base64 bytes: "
					+ Base64.encodeBase64String("salt".getBytes()));
			System.out.println("hex of \"salt\".getBytes(): "
					+ p.toHex("salt".getBytes()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * Tests the basic functionality of the PasswordHash class
	 * 
	 * @param args
	 *            ignored
	 */
	@Test
	public void testHash() {
		PasswordHash p = new PasswordHash();
		try {
			// Print out 10 hashes
			for (int i = 0; i < 10; i++)
				System.out.println(p.createHash("p\r\nassw0Rd!"));

			// Test password validation
			boolean failure = false;
			System.out.println("Running tests...");
			for (int i = 0; i < 100; i++) {
				String password = "" + i;
				String hash = p.createHash(password);
				String secondHash = p.createHash(password);
				if (hash.equals(secondHash)) {
					System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
					failure = true;
				}
				String wrongPassword = "" + (i + 1);
				if (p.validatePassword(wrongPassword, hash)) {
					System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
					failure = true;
				}
				if (!p.validatePassword(password, hash)) {
					System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
					failure = true;
				}
			}
			if (failure)
				System.out.println("TESTS FAILED!");
			else
				System.out.println("TESTS PASSED!");
		} catch (Exception ex) {
			System.out.println("ERROR: " + ex);
		}
	}
}
