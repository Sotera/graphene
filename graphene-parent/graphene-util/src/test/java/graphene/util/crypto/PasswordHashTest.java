package graphene.util.crypto;

import graphene.util.crypto.PasswordHash;

import org.testng.annotations.Test;

public class PasswordHashTest {

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
