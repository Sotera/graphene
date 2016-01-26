/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class PasswordHashTest {
	Logger logger = LoggerFactory.getLogger(PasswordHashTest.class);

	/**
	 * Tests the basic functionality of the PasswordHash class
	 * 
	 * @param args
	 *            ignored
	 */
	@Test
	public void testHash() {
		final PasswordHash p = new PasswordHash();
		try {
			// Print out 10 hashes
			for (int i = 0; i < 10; i++) {
				System.out.println(p.createHash("p\r\nassw0Rd!"));
			}

			// Test password validation
			boolean failure = false;
			System.out.println("Running tests...");
			for (int i = 0; i < 100; i++) {
				final String password = "" + i;
				final String hash = p.createHash(password);
				final String secondHash = p.createHash(password);
				if (hash.equals(secondHash)) {
					System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
					failure = true;
				}
				final String wrongPassword = "" + (i + 1);
				if (p.validatePassword(wrongPassword, hash)) {
					System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
					failure = true;
				}
				if (!p.validatePassword(password, hash)) {
					System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
					failure = true;
				}
			}
			if (failure) {
				System.out.println("TESTS FAILED!");
			} else {
				System.out.println("TESTS PASSED!");
			}
		} catch (final Exception ex) {
			System.out.println("ERROR: " + ex);
		}
	}

	@Test
	public void testSalt() {
		final PasswordHash p = new PasswordHash();

		try {
			// System.out.println(p.createHash("password"));
			final byte[] s = p.pbkdf2("password".toCharArray(), "salt".getBytes(), PasswordHash.PBKDF2_ITERATIONS,
					PasswordHash.HASH_BYTE_SIZE);
			System.out.println(s);
			System.out.println("base64 bytes: " + Base64.encodeBase64String("salt".getBytes()));
			System.out.println("hex of \"salt\".getBytes(): " + p.toHex("salt".getBytes()));
		} catch (final NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} catch (final InvalidKeySpecException e) {
			logger.error(e.getMessage());
		}
	}
}
