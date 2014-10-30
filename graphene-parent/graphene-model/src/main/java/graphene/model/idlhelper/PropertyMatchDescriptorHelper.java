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

import graphene.model.idl.G_BoundedRange;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SingletonRange;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PropertyMatchDescriptorHelper extends G_PropertyMatchDescriptor {

	public static PropertyMatchDescriptorHelper from(G_PropertyMatchDescriptor descriptor) {
		if (descriptor instanceof PropertyMatchDescriptorHelper) return (PropertyMatchDescriptorHelper)descriptor;
		
		PropertyMatchDescriptorHelper helper = new PropertyMatchDescriptorHelper();
		helper.setKey(descriptor.getKey());
		helper.setConstraint(descriptor.getConstraint());
		helper.setVariable(descriptor.getVariable());
		helper.setInclude(descriptor.getInclude());
		helper.setRange(descriptor.getRange());
		return helper;
	}
	
	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
	
	public static String toJson(G_PropertyMatchDescriptor descriptor) throws IOException {
		return SerializationHelper.toJson(descriptor);
	}
	
	public static String toJson(List<G_PropertyMatchDescriptor> descriptors) throws IOException {
		return SerializationHelper.toJson(descriptors, G_PropertyMatchDescriptor.getClassSchema());
	}

	public static String toJson(Map<String, List<G_PropertyMatchDescriptor>> map) throws IOException {
		return SerializationHelper.toJson(map, G_PropertyMatchDescriptor.getClassSchema());
	}
	
	public static G_PropertyMatchDescriptor fromJson(String json) throws IOException {
		return SerializationHelper.fromJson(json, G_PropertyMatchDescriptor.getClassSchema());
	}
	
	public static List<G_PropertyMatchDescriptor> listFromJson(String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_PropertyMatchDescriptor.getClassSchema());
	}
	
	public static Map<String, List<G_PropertyMatchDescriptor>> mapFromJson(String json) throws IOException {
		return SerializationHelper.mapFromJson(json, G_PropertyMatchDescriptor.getClassSchema());
	}

	/**
	 * Returns an G_*Range Object representing a set of basic string terms
	 * 
	 * @param terms
	 * 		the terms to represent
	 * @return
	 * 		an G_*Range Object - either a singleton or list
	 */
	public static Object rangeFromBasicTerms(String terms) {
		
		// if quoted, return as one term
		if (terms.charAt(0) == '"') {
			int end = terms.length() 
					- (terms.charAt(terms.length()-1) == '"'? 1: 0);
			
			return G_SingletonRange.newBuilder()
				.setType(G_PropertyType.STRING)
				.setValue(terms.substring(1, end))
				.build();
		}
		
		final String tokens[] = terms.split("\\s+");

		// else break by whitespace
		switch (tokens.length) {
		case 0:
			return null;
			
		case 1:
			return G_SingletonRange.newBuilder()
				.setType(G_PropertyType.STRING)
				.setValue(tokens[0])
				.build();
			
		default:
			return G_ListRange.newBuilder()
				.setType(G_PropertyType.STRING)
				.setValues(Arrays.asList(Arrays.copyOf(tokens, tokens.length, Object[].class)))
				.build();
		}
	}
	
	/**
	 * Returns true if the match descriptor is exclusive, accounting for both the include
	 * property and the NOT constraint. 
	 * 
	 * Match descriptors have a NOT constraint which is redundant with the include boolean
	 * for searches other than pattern searches. Here we interpret "NOT exclude" as "NOT/exclude", since
	 * otherwise the criteria would have no effect at all.
	 * 
	 * @param descriptor
	 * 		The match specification
	 * 
	 * @return
	 * 		true if an exclusion
	 */
	public static boolean isExclusion(G_PropertyMatchDescriptor descriptor) {
		return G_Constraint.NOT.equals(descriptor) || !descriptor.getInclude();
	}
	
	public G_PropertyType getType() {
		Object range = getRange();
		if (range instanceof G_SingletonRange)
			return ((G_SingletonRange)range).getType();
		else if (range instanceof G_ListRange)
			return ((G_ListRange)range).getType();
		else if (range instanceof G_BoundedRange)
			return ((G_BoundedRange)range).getType();
		return null;
	}

	public Object getValue() {
		Object range = getRange();
		if (range instanceof G_SingletonRange) {
			return ((G_SingletonRange)range).getValue();
		}
		else if (range instanceof G_ListRange) {
			return ((G_ListRange)range).getValues().iterator().next();
		}
		else if (range instanceof G_BoundedRange) {
			G_BoundedRange bounded = (G_BoundedRange)range;
			return bounded.getStart() != null ? bounded.getStart() : bounded.getEnd();
		}
		return null;
	}
}
