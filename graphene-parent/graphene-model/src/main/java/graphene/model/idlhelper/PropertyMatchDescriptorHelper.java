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
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SingletonRange;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PropertyMatchDescriptorHelper extends G_PropertyMatchDescriptor {

	public static PropertyMatchDescriptorHelper from(final G_PropertyMatchDescriptor descriptor) {
		if (descriptor instanceof PropertyMatchDescriptorHelper) {
			return (PropertyMatchDescriptorHelper) descriptor;
		}

		final PropertyMatchDescriptorHelper helper = new PropertyMatchDescriptorHelper();
		helper.setKey(descriptor.getKey());
		helper.setConstraint(descriptor.getConstraint());
		helper.setVariable(descriptor.getVariable());
		helper.setInclude(descriptor.getInclude());
		helper.setSingletonRange(descriptor.getSingletonRange());
		helper.setListRange(descriptor.getListRange());
		helper.setBoundedRange(descriptor.getBoundedRange());

		return helper;
	}

	public static G_PropertyMatchDescriptor fromJson(final String json) throws IOException {
		return SerializationHelper.fromJson(json, G_PropertyMatchDescriptor.getClassSchema());
	}

	/**
	 * Returns true if the match descriptor is exclusive, accounting for both
	 * the include property and the NOT constraint.
	 * 
	 * Match descriptors have a NOT constraint which is redundant with the
	 * include boolean for searches other than pattern searches. Here we
	 * interpret "NOT exclude" as "NOT/exclude", since otherwise the criteria
	 * would have no effect at all.
	 * 
	 * @param descriptor
	 *            The match specification
	 * 
	 * @return true if an exclusion
	 */
	public static boolean isExclusion(final G_PropertyMatchDescriptor descriptor) {
		return G_Constraint.NOT.equals(descriptor.getConstraint()) || !descriptor.getInclude();
	}

	public static List<G_PropertyMatchDescriptor> listFromJson(final String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_PropertyMatchDescriptor.getClassSchema());
	}

	public static Map<String, List<G_PropertyMatchDescriptor>> mapFromJson(final String json) throws IOException {
		return SerializationHelper.mapFromJson(json, G_PropertyMatchDescriptor.getClassSchema());
	}

	/**
	 * Returns an G_*Range Object representing a set of basic string terms
	 * 
	 * @param terms
	 *            the terms to represent
	 * @return an G_*Range Object - either a singleton or list
	 */
	public static Object rangeFromBasicTerms(final String terms) {

		// if quoted, return as one term
		if (terms.charAt(0) == '"') {
			final int end = terms.length() - (terms.charAt(terms.length() - 1) == '"' ? 1 : 0);

			return G_SingletonRange.newBuilder().setType(G_PropertyType.STRING).setValue(terms.substring(1, end))
					.build();
		}

		final String tokens[] = terms.split("\\s+");

		// else break by whitespace
		switch (tokens.length) {
		case 0:
			return null;

		case 1:
			return G_SingletonRange.newBuilder().setType(G_PropertyType.STRING).setValue(tokens[0]).build();

		default:
			return G_ListRange.newBuilder().setType(G_PropertyType.STRING)
					.setValues(Arrays.asList(Arrays.copyOf(tokens, tokens.length, Object[].class))).build();
		}
	}

	public static String toJson(final G_PropertyMatchDescriptor descriptor) throws IOException {
		return SerializationHelper.toJson(descriptor);
	}

	public static String toJson(final List<G_PropertyMatchDescriptor> descriptors) throws IOException {
		return SerializationHelper.toJson(descriptors, G_PropertyMatchDescriptor.getClassSchema());
	}

	public static String toJson(final Map<String, List<G_PropertyMatchDescriptor>> map) throws IOException {
		return SerializationHelper.toJson(map, G_PropertyMatchDescriptor.getClassSchema());
	}

	public PropertyMatchDescriptorHelper() {
		// TODO Auto-generated constructor stub
		setBoundedRange(null);
		setSingletonRange(null);
		setListRange(null);
		setTypeMappings(null);
		setKey(null);
		setConstraint(null);

	}

	public PropertyMatchDescriptorHelper(final String key, final String singletonString) {
		setKey(key);
		setSingletonRange(new SingletonRangeHelper(singletonString, G_PropertyType.STRING));
		setConstraint(G_Constraint.EQUALS);
	}

	public PropertyMatchDescriptorHelper(final String key, final String... singletonString) {
		setKey(key);
		setListRange(new ListRangeHelper(singletonString, G_PropertyType.STRING));
		setConstraint(G_Constraint.EQUALS);
	}

	public G_PropertyType getType() {
		if (ValidationUtils.isValid(getSingletonRange())) {
			return getSingletonRange().getType();
		} else if (ValidationUtils.isValid(getListRange())) {
			return getListRange().getType();
		} else if (ValidationUtils.isValid(getBoundedRange())) {
			return getBoundedRange().getType();
		}
		return null;
	}

	public Object getValue() {
		if (ValidationUtils.isValid(getSingletonRange())) {
			return getSingletonRange().getValue();
		} else if (ValidationUtils.isValid(getListRange())) {
			return getListRange().getValues();
		} else if (ValidationUtils.isValid(getBoundedRange())) {
			return getBoundedRange().getStart() != null ? getBoundedRange().getStart() : getBoundedRange().getEnd();
		}

		return null;
	}

	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
}
