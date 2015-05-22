/**
 * Copyright (c) 2013-2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.model.idlhelper;

import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_SingletonRange;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 */
public class SolrUtils {

	private static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");

	private static Pattern TERM_SEPARATOR = Pattern.compile("\\.\\s+|[,;:\\?\\s]+");

	/**
	 * See org.apache.solr.client.solrj.util.ClientUtils See:
	 * {@link org.apache.lucene.queryparser.classic queryparser syntax} for more
	 * information on Escaping Special Characters
	 */
	public static String escapeQueryChars(final String s) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			// These characters are part of the query syntax and must be escaped
			if ((c == '\\') || (c == '+') || (c == '-') || (c == '!') || (c == '(') || (c == ')') || (c == ':')
					|| (c == '^') || (c == '[') || (c == ']') || (c == '\"') || (c == '{') || (c == '}') || (c == '~')
					|| (c == '*') || (c == '?') || (c == '|') || (c == '&') || (c == ';') || (c == '/')
					|| Character.isWhitespace(c)) {
				sb.append('\\');
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Returns a Solr query clause to represent the descriptor supply.
	 * 
	 * @param descriptor
	 *            The match specification
	 * 
	 * @return
	 */
	public static String toSolrClause(final G_PropertyMatchDescriptor descriptor) {

		final String k = descriptor.getKey();

		Collection<Object> values = null;

		if (ValidationUtils.isValid(descriptor.getSingletonRange())) {
			values = Collections.singleton(descriptor.getSingletonRange().getValue());
		} else if (ValidationUtils.isValid(descriptor.getListRange())) {
			values = descriptor.getListRange().getValues();
		} else {
			values = null;
		}

		final StringBuilder s = new StringBuilder();

		// fuzzy?
		if (PropertyMatchDescriptorHelper.isExclusion(descriptor)) {
			s.append("-");
		}

		s.append(k);

		if (((values != null) && G_Constraint.FUZZY_PARTIAL_OPTIONAL.equals(descriptor.getConstraint()))
				|| G_Constraint.FUZZY_REQUIRED.equals(descriptor.getConstraint())) {
			s.append(":(");

			for (final Object v : values) {

				// add each term separately
				final String wsTokens[] = TERM_SEPARATOR.split(v.toString());

				for (final String token : wsTokens) {
					if ((token.indexOf('-') == -1) || NUMBER_PATTERN.matcher(token).find()) {
						s.append(escapeQueryChars(token));
						s.append("~");
						if ((descriptor.getSimilarity() != null) && (descriptor.getSimilarity() != 1.0)) {
							s.append(descriptor.getSimilarity());
						}
						s.append(" ");
					} else {
						final String hyphenTokens[] = token.split("-");
						for (final String seg : hyphenTokens) {
							s.append(escapeQueryChars(seg));
							s.append("~");
							if ((descriptor.getSimilarity() != null) && (descriptor.getSimilarity() != 1.0)) {
								s.append(descriptor.getSimilarity());
							}
							s.append(" ");
						}
					}
				}
			}

			s.setLength(s.length() - 1);
			s.append(")");

		} else { // not | required / equals
			s.append(":(");
			if (values != null) {
				for (final Object v : values) {
					s.append("\"");
					s.append(v);
					s.append("\" ");
				}
			}

			s.setLength(s.length() - 1);
			s.append(")");
		}

		if ((descriptor.getWeight() != null) && (descriptor.getWeight() != 1.0)) {
			s.append("^");
			s.append(descriptor.getWeight());
		}

		return s.toString();
	}

	/**
	 * Returns an OR'd series of Solr clauses to represent the list of terms
	 * specified.
	 * 
	 * @param basicQuery
	 *            A search over default fields supplied by
	 *            basicQueryFieldWeights
	 * 
	 * @param basicQueryFieldWeights
	 *            A map of default field names to weightings, where 1.0 is the
	 *            default weight.
	 * 
	 * @param advancedTerms
	 *            A list of explicitly defined terms
	 * 
	 * @return The solr query string or null if not a valid query.
	 */
	public static String toSolrQuery(final String basicQuery, final Map<String, Double> basicQueryFieldWeights,
			final List<G_PropertyMatchDescriptor> advancedTerms) {

		// copy terms to merge basic with advanced.
		final List<G_PropertyMatchDescriptor> orsAnds = new ArrayList<G_PropertyMatchDescriptor>(advancedTerms.size());
		final List<G_PropertyMatchDescriptor> nots = new ArrayList<G_PropertyMatchDescriptor>(advancedTerms.size());

		// first add basic query terms.
		if ((basicQuery != null) && !basicQuery.isEmpty()) {
			final Object values = PropertyMatchDescriptorHelper.rangeFromBasicTerms(basicQuery);

			if (values != null) {
				for (final String key : basicQueryFieldWeights.keySet()) {
					if (values instanceof G_SingletonRange) {
						orsAnds.add(G_PropertyMatchDescriptor.newBuilder().setConstraint(G_Constraint.OPTIONAL_EQUALS)
								.setKey(key).setSingletonRange((G_SingletonRange) values).build());
					}
					if (values instanceof G_ListRange) {
						orsAnds.add(G_PropertyMatchDescriptor.newBuilder().setConstraint(G_Constraint.OPTIONAL_EQUALS)
								.setKey(key).setListRange((G_ListRange) values).build());
					}
				}
			}
		}

		// now form it into a query
		final StringBuilder query = new StringBuilder();

		// separate ors from nots
		for (final G_PropertyMatchDescriptor term : advancedTerms) {
			(PropertyMatchDescriptorHelper.isExclusion(term) ? nots : orsAnds).add(term);
		}

		// not valid
		if (orsAnds.isEmpty()) {
			return null;
		}

		// orsAnds
		for (final G_PropertyMatchDescriptor term : orsAnds) {
			query.append(toSolrClause(term));
			if (term.getConstraint().equals(G_Constraint.OPTIONAL_EQUALS)
					|| term.getConstraint().equals(G_Constraint.FUZZY_PARTIAL_OPTIONAL)) {
				query.append(" OR ");
			} else {
				query.append(" AND ");
			}

		}

		// trim last OR
		query.setLength(query.length() - 4);

		// nots
		if (!nots.isEmpty()) {
			query.insert(0, '(');
			query.append(')');

			for (final G_PropertyMatchDescriptor term : nots) {
				query.append(" AND ");
				query.append(toSolrClause(term));
			}
		}

		return query.toString();
	}
}
