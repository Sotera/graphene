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

import graphene.model.idl.G_PropertyDescriptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PropertyDescriptorHelper extends G_PropertyDescriptor {
	
	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
	
	public static String toJson(G_PropertyDescriptor descriptor) throws IOException {
		return SerializationHelper.toJson(descriptor);
	}

	public static String toJson(List<G_PropertyDescriptor> descriptors) throws IOException {
		return SerializationHelper.toJson(descriptors, G_PropertyDescriptor.getClassSchema());
	}

	public static String toJson(Map<String, List<G_PropertyDescriptor>> map) throws IOException {
		return SerializationHelper.toJson(map, G_PropertyDescriptor.getClassSchema());
	}
	
	public static G_PropertyDescriptor fromJson(String json) throws IOException {
		return SerializationHelper.fromJson(json, G_PropertyDescriptor.getClassSchema());
	}
	
	public static List<G_PropertyDescriptor> listFromJson(String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_PropertyDescriptor.getClassSchema());
	}
	
	public static Map<String, List<G_PropertyDescriptor>> mapFromJson(String json) throws IOException {
		return SerializationHelper.mapFromJson(json, G_PropertyDescriptor.getClassSchema());
	}
}
