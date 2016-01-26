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

// Copyright 2012 Jeff Hodges. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package graphene.util.ooab;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ByteArrayFilterTest {

	@Test
	public void testTheBasics() {
		ByteArrayFilter filter = new ByteArrayFilter(2);
		byte[] twentyNineId = new byte[] { 27, 28, 29 };
		byte[] thirtyId = new byte[] { 27, 28, 30 };
		byte[] thirtyThreeId = new byte[] { 27, 28, 33 };
		assertFalse("nothing should be contained at all",
				filter.containsAndAdd(twentyNineId));
		assertTrue("now it should", filter.containsAndAdd(twentyNineId));
		assertFalse("false unless the hash collides",
				filter.containsAndAdd(thirtyId));
		assertTrue("original should still return true",
				filter.containsAndAdd(twentyNineId));
		assertTrue("new array should still return true",
				filter.containsAndAdd(thirtyId));

		// Handling collisions. {27, 28, 33} and {27, 28, 30} hash to the same
		// index using the current
		// hash function inside ByteArrayFilter.
		assertFalse("colliding array returns false",
				filter.containsAndAdd(thirtyThreeId));
		assertTrue("colliding array returns true in second call",
				filter.containsAndAdd(thirtyThreeId));
		assertFalse("original colliding array returns false",
				filter.containsAndAdd(thirtyId));
		assertTrue("original colliding array returns true",
				filter.containsAndAdd(thirtyId));
		assertFalse("colliding array returns false",
				filter.containsAndAdd(thirtyThreeId));
	}

	@Test
	public void testSizeRounding() {
		ByteArrayFilter filter = new ByteArrayFilter(3);
		assertEquals("3 should round to 4", 4, filter.getSize());
		filter = new ByteArrayFilter(4);
		assertEquals("4 should round to 4", 4, filter.getSize());
		filter = new ByteArrayFilter(129);
		assertEquals("129 should round to 256", 256, filter.getSize());
	}

	@Test
	public void testTooLargeSize() {
		int size = (1 << 30) + 1;
		try {
			new ByteArrayFilter(size);
			Assert.fail("should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String msg = "array size may not be larger than 2**31-1, but will be rounded to larger. was "
					+ size;
			assertEquals(msg, e.getMessage());
		}
	}

	@Test
	public void testTooSmallSize() {
		int size = 0;
		try {
			new ByteArrayFilter(size);
			Assert.fail("zero passed in directly should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String msg = "array size must be greater than zero, was 0";
			assertEquals("zero passed in directly should error correctly", msg,
					e.getMessage());
		}
	}
}
