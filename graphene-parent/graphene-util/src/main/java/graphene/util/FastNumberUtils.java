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

package graphene.util;

/**
 * Faster utilities for dealing with numbers.
 * 
 * Credit: http://nadeausoftware.com/articles/2009/08/
 * java_tip_how_parse_integers_quickly
 * 
 * 
 * Conclusions For the best Java integer parsing performance:
 * 
 * Integer.parseInt is much faster than NumberFormat.parse. A custom parser for
 * radix 10 numbers is much faster than Integer.parseInt. If you have a large
 * number of integers to parse, writing your own parser (or using my code above)
 * can provide a significant performance boost. Just because Java's packages
 * provide a lot of useful methods doesn't mean that those methods have been
 * implemented well. Often, with your better knowledge of what you need to
 * accomplish, you can do better with custom code.
 * 
 * @author djue
 * 
 */
public class FastNumberUtils {
	/**
	 * The following custom parser adheres to the same contract as
	 * FastNumberUtils.parseIntWithCheck( ). Quoting from its javadoc:
	 * 
	 * "Parses the string argument as a signed decimal integer. The characters in the string must all be decimal digits, except that the first character may be an ASCII minus sign '-' ('\u002D') to indicate a negative value. The resulting integer value is returned, exactly as if the argument and the radix 10 were given as arguments to the parseInt(java.lang.String, int) method."
	 * 
	 * 
	 * @param s
	 * @return the converted int value
	 * @throws NumberFormatException
	 */
	public static int parseIntWithCheck(final String s) throws NumberFormatException{
		if (s == null || s.length() == 0) {
			throw new NumberFormatException("Null string");
		}
		// Check for a sign.
		int num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-') {
			if (len == 1) {
				throw new NumberFormatException("Missing digits:  " + s);
			}
			sign = 1;
		} else {
			final int d = ch - '0';
			if (d < 0 || d > 9) {
				throw new NumberFormatException("Malformed:  " + s);
			}
			num = -d;
		}

		// Build the number.
		final int max = (sign == -1) ? -Integer.MAX_VALUE : Integer.MIN_VALUE;
		final int multmax = max / 10;
		int i = 1;
		while (i < len) {
			int d = s.charAt(i++) - '0';
			if (d < 0 || d > 9) {
				throw new NumberFormatException("Malformed:  " + s);
			}
			if (num < multmax) {
				throw new NumberFormatException("Over/underflow:  " + s);
			}
			num *= 10;
			if (num < (max + d)) {
				throw new NumberFormatException("Over/underflow:  " + s);
			}
			num -= d;
		}

		return sign * num;
	}

	/**
	 * 
	 * @param s
	 * @param defaultValue
	 * @return the converted int value, or the default value if something went wrong.
	 */
	public static int parseIntWithCheck(final String s, final int defaultValue) {
		if (s == null || s.length() == 0) {
			return defaultValue;
		}
		// Check for a sign.
		int num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-') {
			if (len == 1) {
				return defaultValue;
			}
			sign = 1;
		} else {
			final int d = ch - '0';
			if (d < 0 || d > 9) {
				return defaultValue;
			}
			num = -d;
		}

		// Build the number.
		final int max = (sign == -1) ? -Integer.MAX_VALUE : Integer.MIN_VALUE;
		final int multmax = max / 10;
		int i = 1;
		while (i < len) {
			int d = s.charAt(i++) - '0';
			if (d < 0 || d > 9) {
				return defaultValue;
			}
			if (num < multmax) {
				return defaultValue;
			}
			num *= 10;
			if (num < (max + d)) {
				return defaultValue;
			}
			num -= d;
		}

		return sign * num;
	}

	/**
	 * All the digit and underflow/overflow checking takes time. If you're
	 * parsing numbers where you know they are valid, you can skip all of this
	 * and go faster. This is safe to do when parsing well-known file formats
	 * and data sets, such as the text files for the US Census. Such files have
	 * already been vetted for bad digits and underflow/overflow. So, here's the
	 * same function stripped of its exception checking:
	 * 
	 * @param s
	 * @return
	 */
	public static int parseIntWithoutCheck(final String s) {
		// Check for a sign.
		int num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-')
			sign = 1;
		else
			num = '0' - ch;

		// Build the number.
		int i = 1;
		while (i < len)
			num = num * 10 + '0' - s.charAt(i++);

		return sign * num;
	}

	/**
	 * 
	 * @param s
	 * @return the converted long
	 * @throws NumberFormatException if something went wrong
	 */
	public static long parseLongWithCheck(final String s) throws NumberFormatException {
		if (s == null || s.length() == 0) {
			throw new NumberFormatException("Null string");
		}
		// Check for a sign.
		long num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-') {
			if (len == 1) {
				throw new NumberFormatException("Missing digits:  " + s);
			}
			sign = 1;
		} else {
			final int d = ch - '0';
			if (d < 0 || d > 9) {
				throw new NumberFormatException("Malformed:  " + s);
			}
			num = -d;
		}

		// Build the number.
		final long max = (sign == -1) ? -Long.MAX_VALUE : Long.MIN_VALUE;
		final long multmax = max / 10;
		int i = 1;
		while (i < len) {
			int d = s.charAt(i++) - '0';
			if (d < 0 || d > 9) {
				throw new NumberFormatException("Malformed:  " + s);
			}
			if (num < multmax) {
				throw new NumberFormatException("Over/underflow:  " + s);
			}
			num *= 10;
			if (num < (max + d)) {
				throw new NumberFormatException("Over/underflow:  " + s);
			}
			num -= d;
		}

		return sign * num;
	}

	/**
	 * 
	 * @param s the string to convert
	 * @param defaultValue
	 * @return the converted long value, or the default if something went wrong.
	 */
	public static long parseLongWithCheck(final String s,
			final long defaultValue) {
		if (s == null || s.length() == 0) {
			return defaultValue;
		}
		// Check for a sign.
		long num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-') {
			if (len == 1) {
				return defaultValue;
			}
			sign = 1;
		} else {
			final int d = ch - '0';
			if (d < 0 || d > 9) {
				return defaultValue;
			}
			num = -d;
		}

		// Build the number.
		final long max = (sign == -1) ? -Long.MAX_VALUE : Long.MIN_VALUE;
		final long multmax = max / 10;
		int i = 1;
		while (i < len) {
			int d = s.charAt(i++) - '0';
			if (d < 0 || d > 9) {
				return defaultValue;
			}
			if (num < multmax) {
				return defaultValue;
			}
			num *= 10;
			if (num < (max + d)) {
				return defaultValue;
			}
			num -= d;
		}

		return sign * num;
	}

	public static long parseLongWithoutCheck(final String s) {
		// Check for a sign.
		long num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-')
			sign = 1;
		else
			num = '0' - ch;

		// Build the number.
		int i = 1;
		while (i < len)
			num = num * 10 + '0' - s.charAt(i++);

		return sign * num;
	}
}
