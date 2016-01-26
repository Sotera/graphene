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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import graphene.util.validator.ValidationUtils;

import java.text.BreakIterator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.lang3.text.WordUtils;

/**
 * <p>
 * Simple utility class for String operations useful across the framework.
 * 
 * <p>
 * Some methods in this class were copied from the Spring Framework so we didn't
 * have to re-invent the wheel, and in these cases, we have retained all
 * license, copyright and author information.
 * 
 * @since 0.9
 */
public class StringUtils {
	/** Constant representing the empty string, equal to &quot;&quot; */
	public static final String EMPTY_STRING = "";

	/**
	 * Constant representing the default delimiter character (comma), equal to
	 * <code>','</code>
	 */
	public static final char DEFAULT_DELIMITER_CHAR = ',';

	/**
	 * Constant representing the default quote character (double quote), equal
	 * to '&quot;'</code>
	 */
	public static final char DEFAULT_QUOTE_CHAR = '"';

	// TODO - complete JavaDoc

	/**
	 * Returns a 'cleaned' representation of the specified argument. 'Cleaned'
	 * is defined as the following:
	 * 
	 * <ol>
	 * <li>If the specified <code>String</code> is <code>null</code>, return
	 * <code>null</code></li>
	 * <li>If not <code>null</code>, {@link String#trim() trim()} it.</li>
	 * <li>If the trimmed string is equal to the empty String (i.e.
	 * &quot;&quot;), return <code>null</code></li>
	 * <li>If the trimmed string is not the empty string, return the trimmed
	 * version</li>.
	 * </ol>
	 * 
	 * Therefore this method always ensures that any given string has trimmed
	 * text, and if it doesn't, <code>null</code> is returned.
	 * 
	 * @param in
	 *            the input String to clean.
	 * @return a populated-but-trimmed String or <code>null</code> otherwise
	 */
	public static String clean(final String in) {
		String out = in;

		if (in != null) {
			out = in.trim();
			if (out.equals(EMPTY_STRING)) {
				out = null;
			}
		}

		return out;
	}

	/**
	 * If the input contains a fraction of uppercase characters above the
	 * threshold, we will apply title case to all the words in the string.
	 * 
	 * @param input
	 * @param threshold
	 * @return
	 */
	public static String cleanUpAllCaps(final String input, final double threshold) {
		int numUpper = 0;
		for (int i = 0; i < input.length(); i++) {
			if (Character.isUpperCase(input.charAt(i))) {
				numUpper++;
			}
		}
		final float fractionUpper = (float) numUpper / (float) input.length();
		if (fractionUpper >= threshold) {
			return WordUtils.capitalizeFully(input);
		} else {
			return input;
		}
	}

	/**
	 * Opposite of tokenizing, we are combining Object.toString() into one
	 * string.
	 * 
	 * @param delimiter
	 * @param values
	 * @return
	 */
	public static String coalesc(final String delimiter, final Object... values) {
		final StringBuffer buf = new StringBuffer();
		for (final Object v : values) {
			if (ValidationUtils.isValid(v)) {
				if (buf.length() > 0) {
					buf.append(delimiter);
				}
				buf.append(v.toString());
			}
		}
		return buf.toString();
	}

	/**
	 * Opposite of tokenizing, we are combining strings into one string.
	 * 
	 * @param delimiter
	 * @param values
	 * @return
	 */
	public static String coalesc(final String delimiter, final String... values) {
		final StringBuffer buf = new StringBuffer();
		for (final String v : values) {
			if (ValidationUtils.isValid(v)) {
				if (buf.length() > 0) {
					buf.append(delimiter);
				}
				buf.append(v);
			}
		}
		return buf.toString();
	}

	/**
	 * Convert a string to a list of strings broken up by end of sentence tokens
	 * using the US locale
	 * 
	 * @param input
	 * @return
	 */
	public static List<String> convertToSentences(final String input) {
		return convertToSentences(input, Locale.US);
	}

	/**
	 * Convert a string to a list of strings broken up by end of sentence
	 * tokens.
	 * 
	 * @param input
	 * @param locale
	 * @return
	 */
	public static List<String> convertToSentences(final String input, final Locale locale) {
		final BreakIterator iterator = BreakIterator.getSentenceInstance(locale);
		iterator.setText(input);
		final ArrayList<String> sentences = new ArrayList<String>();

		int start = iterator.first();

		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			sentences.add(input.substring(start, end));
		}
		return sentences;
	}

	public static String firstNonNull(final String... values) {
		return (String) (ValidationUtils.firstNonNull(values));
	}

	public static String firstNonNullDefault(final String defaultString, final String... values) {
		final String s = firstNonNull(values);
		if (s == null) {
			return defaultString;
		} else {
			return s;
		}
	}

	public static String firstNonNullToString(final Object... obs) {
		for (final Object o : obs) {
			if (ValidationUtils.isValid(o)) {
				return o.toString();
			}
		}
		return null;
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of
	 * whitespace.
	 * <p/>
	 * <code>StringUtils.hasLength(null) == false<br/>
	 * StringUtils.hasLength("") == false<br/>
	 * StringUtils.hasLength(" ") == true<br/>
	 * StringUtils.hasLength("Hello") == true</code>
	 * <p/>
	 * Copied from the Spring Framework while retaining all license, copyright
	 * and author information.
	 * 
	 * @param str
	 *            the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(final String str) {
		return ((str != null) && (str.length() > 0));
	}

	/**
	 * Check whether the given String has actual text. More specifically,
	 * returns <code>true</code> if the string not <code>null</code>, its length
	 * is greater than 0, and it contains at least one non-whitespace character.
	 * <p/>
	 * <code>StringUtils.hasText(null) == false<br/>
	 * StringUtils.hasText("") == false<br/>
	 * StringUtils.hasText(" ") == false<br/>
	 * StringUtils.hasText("12345") == true<br/>
	 * StringUtils.hasText(" 12345 ") == true</code>
	 * 
	 * <p>
	 * Copied from the Spring Framework while retaining all license, copyright
	 * and author information.
	 * 
	 * @param str
	 *            the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not <code>null</code>, its
	 *         length is greater than 0, and it does not contain whitespace only
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean hasText(final String str) {
		if (!hasLength(str)) {
			return false;
		}
		final int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static String removeLeadingZeros(final String key) {
		return key.replaceFirst("^0+(?!$)", "");
	}

	/**
	 * A version of the splitter commonly used by rest services in Graphene. We
	 * use some other parameter to see if the splitting should be enabled.
	 * 
	 * @param enable
	 * @param value
	 * @param delimiter
	 * @return
	 */
	public static String[] split(final boolean enable, final String value, final char delimiter) {
		String[] values;
		// Something like valueType.contains("list")
		if (enable) {
			values = split(value, delimiter);
		} else {
			values = new String[] { value };
		}
		return values;
	}

	public static String[] split(final String line) {
		return split(line, DEFAULT_DELIMITER_CHAR);
	}

	public static String[] split(final String line, final char delimiter) {
		return split(line, delimiter, DEFAULT_QUOTE_CHAR);
	}

	public static String[] split(final String line, final char delimiter, final char quoteChar) {
		return split(line, delimiter, quoteChar, quoteChar);
	}

	public static String[] split(final String line, final char delimiter, final char beginQuoteChar,
			final char endQuoteChar) {
		return split(line, delimiter, beginQuoteChar, endQuoteChar, false, true);
	}

	/**
	 * Splits the specified delimited String into tokens, supporting quoted
	 * tokens so that quoted strings themselves won't be tokenized.
	 * 
	 * <p>
	 * This method's implementation is very loosely based (with significant
	 * modifications) on <a href="http://blogs.bytecode.com.au/glen">Glen
	 * Smith</a>'s open-source <a href=
	 * "http://opencsv.svn.sourceforge.net/viewvc/opencsv/trunk/src/au/com/bytecode/opencsv/CSVReader.java?&view=markup"
	 * >CSVReader.java</a> file.
	 * 
	 * <p>
	 * That file is Apache 2.0 licensed as well, making Glen's code a great
	 * starting point for us to modify to our needs.
	 * 
	 * @param aLine
	 *            the String to parse
	 * @param delimiter
	 *            the delimiter by which the <tt>line</tt> argument is to be
	 *            split
	 * @param beginQuoteChar
	 *            the character signifying the start of quoted text (so the
	 *            quoted text will not be split)
	 * @param endQuoteChar
	 *            the character signifying the end of quoted text
	 * @param retainQuotes
	 *            if the quotes themselves should be retained when constructing
	 *            the corresponding token
	 * @param trimTokens
	 *            if leading and trailing whitespace should be trimmed from
	 *            discovered tokens.
	 * @return the tokens discovered from parsing the given delimited
	 *         <tt>line</tt>.
	 */
	public static String[] split(final String aLine, final char delimiter, final char beginQuoteChar,
			final char endQuoteChar, final boolean retainQuotes, final boolean trimTokens) {
		final String line = clean(aLine);
		if (line == null) {
			return null;
		}

		final List<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {

			final char c = line.charAt(i);
			if (c == beginQuoteChar) {
				// this gets complex... the quote may end a quoted block, or
				// escape another quote.
				// do a 1-char lookahead:
				if (inQuotes // we are in quotes, therefore there can be escaped
								// quotes in here.
						&& (line.length() > (i + 1) // there is indeed another
						)
						// character to check.
						&& (line.charAt(i + 1) == beginQuoteChar)) {

					/**
					 * ..and that char. is a quote also. we have two quote chars
					 * in a row == one quote char, so consume them both and put
					 * one on the token. we do *not* exit the quoted text.
					 **/

					sb.append(line.charAt(i + 1));
					i++;
				} else {
					inQuotes = !inQuotes;
					if (retainQuotes) {
						sb.append(c);
					}
				}
			} else if (c == endQuoteChar) {
				inQuotes = !inQuotes;
				if (retainQuotes) {
					sb.append(c);
				}
			} else if ((c == delimiter) && !inQuotes) {
				String s = sb.toString();
				if (trimTokens) {
					s = s.trim();
				}
				tokens.add(s);
				sb = new StringBuilder(); // start work on next token
			} else {
				sb.append(c);
			}
		}
		String s = sb.toString();
		if (trimTokens) {
			s = s.trim();
		}
		tokens.add(s);
		return tokens.toArray(new String[tokens.size()]);
	}

	public static String[] splitKeyValue(final String aLine) throws ParseException {
		final String line = clean(aLine);
		if (line == null) {
			return null;
		}
		String[] split = line.split(" ", 2);
		if (split.length != 2) {
			// fallback to checking for an equals sign
			split = line.split("=", 2);
			if (split.length != 2) {
				final String msg = "Unable to determine Key/Value pair from line [" + line
						+ "].  There is no space from " + "which the split location could be determined.";
				throw new ParseException(msg, 0);
			}

		}

		split[0] = clean(split[0]);
		split[1] = clean(split[1]);
		if (split[1].startsWith("=")) {
			// they used spaces followed by an equals followed by zero or more
			// spaces to split the key/value pair, so
			// remove the equals sign to result in only the key and values in
			// the
			split[1] = clean(split[1].substring(1));
		}

		if (split[0] == null) {
			final String msg = "No valid key could be found in line [" + line + "] to form a key/value pair.";
			throw new ParseException(msg, 0);
		}
		if (split[1] == null) {
			final String msg = "No corresponding value could be found in line [" + line + "] for key [" + split[0]
					+ "]";
			throw new ParseException(msg, 0);
		}

		return split;
	}

	/**
	 * Test if the given String starts with the specified prefix, ignoring
	 * upper/lower case.
	 * 
	 * <p>
	 * Copied from the Spring Framework while retaining all license, copyright
	 * and author information.
	 * 
	 * @param str
	 *            the String to check
	 * @param prefix
	 *            the prefix to look for
	 * @return <code>true</code> starts with the specified prefix (ignoring
	 *         case), <code>false</code> if it does not.
	 * @see java.lang.String#startsWith
	 */
	public static boolean startsWithIgnoreCase(final String str, final String prefix) {
		if ((str == null) || (prefix == null)) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		final String lcStr = str.substring(0, prefix.length()).toLowerCase();
		final String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	public static Collection<? extends String> toCollection(final char[] charArray) {
		final Collection<String> c = new ArrayList<String>();
		for (final Object obj : charArray) {
			c.add(obj.toString());
		}
		return c;
	}

	public static Collection<? extends String> toCollection(final Object[] charArray) {
		final Collection<String> c = new ArrayList<String>();
		for (final Object obj : charArray) {
			c.add(obj.toString());
		}
		return c;
	}

	/**
	 * Returns the array's contents as a string, with each element delimited by
	 * the specified {@code delimiter} argument. Useful for {@code toString()}
	 * implementations and log messages.
	 * 
	 * @param array
	 *            the array whose contents will be converted to a string
	 * @param delimiter
	 *            the delimiter to use between each element
	 * @return a single string, delimited by the specified {@code delimiter}.
	 * @since 1.0
	 */
	public static String toDelimitedString(final Object[] array, final String delimiter) {
		if ((array == null) || (array.length == 0)) {
			return EMPTY_STRING;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append(delimiter);
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Similar to the other method, but allows a variable list of objects.
	 * 
	 * @param delimiter
	 * @param array
	 * @return
	 */
	public static String toDelimitedString(final String delimiter, final Object... array) {
		if ((array == null) || (array.length == 0)) {
			return EMPTY_STRING;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append(delimiter);
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * Trims tokens and omits empty tokens.
	 * <p>
	 * The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * 
	 * <p>
	 * Copied from the Spring Framework while retaining all license, copyright
	 * and author information.
	 * 
	 * @param str
	 *            the String to tokenize
	 * @param delimiters
	 *            the delimiter characters, assembled as String (each of those
	 *            characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 */
	public static String[] tokenizeToStringArray(final String str, final String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>
	 * The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * 
	 * <p>
	 * Copied from the Spring Framework while retaining all license, copyright
	 * and author information.
	 * 
	 * @param str
	 *            the String to tokenize
	 * @param delimiters
	 *            the delimiter characters, assembled as String (each of those
	 *            characters is individually considered as delimiter)
	 * @param trimTokens
	 *            trim the tokens via String's <code>trim</code>
	 * @param ignoreEmptyTokens
	 *            omit empty tokens from the result array (only applies to
	 *            tokens that are empty after trimming; StringTokenizer will not
	 *            consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens (<code>null</code> if the input String was
	 *         <code>null</code>)
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 */
	public static String[] tokenizeToStringArray(final String str, final String delimiters, final boolean trimTokens,
			final boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		final StringTokenizer st = new StringTokenizer(str, delimiters);
		final List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || (token.length() > 0)) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * A shortcut to tokenizeToStringCollection which trims tokens and ignores
	 * empty ones.
	 * 
	 * @param str
	 * @param delimiters
	 * @return A collection of zero or more Strings, or null if the str was null
	 */
	public static Collection<? extends String> tokenizeToStringCollection(final String str, final String delimiters) {

		return tokenizeToStringCollection(str, delimiters, true, true);
	}

	public static Collection<? extends String> tokenizeToStringCollection(final String str, final String delimiters,
			final boolean trimTokens, final boolean ignoreEmptyTokens) {
		if (str == null) {
			return null;
		}
		final StringTokenizer st = new StringTokenizer(str, delimiters);
		final Collection<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || (token.length() > 0)) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	/**
	 * Returns the specified array as a comma-delimited (',') string.
	 * 
	 * @param array
	 *            the array whose contents will be converted to a string.
	 * @return the array's contents as a comma-delimited (',') string.
	 * @since 1.0
	 */
	public static String toString(final Object[] array) {
		return toDelimitedString(array, ",");
	}

	/**
	 * Copy the given Collection into a String array. The Collection must
	 * contain String elements only.
	 * 
	 * <p>
	 * Copied from the Spring Framework while retaining all license, copyright
	 * and author information.
	 * 
	 * @param collection
	 *            the Collection to copy
	 * @return the String array (<code>null</code> if the passed-in Collection
	 *         was <code>null</code>)
	 */
	public static String[] toStringArray(final Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}
}