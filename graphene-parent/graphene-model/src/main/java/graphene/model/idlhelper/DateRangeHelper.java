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

import graphene.model.idl.G_DateInterval;
import graphene.model.idl.G_DateRange;
import graphene.model.idl.G_Duration;

import java.io.IOException;
import java.util.List;

public class DateRangeHelper extends G_DateRange {
	
	public DateRangeHelper(long start, G_DateInterval interval, long numBins) {
		super(start, numBins, new G_Duration(interval, 1L));
	}
	
	public DateRangeHelper(long start, G_DateInterval interval, long numBins, long numIntervalsPerBin) {
		super(start, numBins, new G_Duration(interval, numIntervalsPerBin));
	}
	
	public DateRangeHelper(long start, long numBins, G_Duration durationPerBin) {
		super(start, numBins, durationPerBin);
	}
	
	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
	
	public static String toJson(G_DateRange date) throws IOException {
		return SerializationHelper.toJson(date);
	}

	public static String toJson(List<G_DateRange> dates) throws IOException {
		return SerializationHelper.toJson(dates, G_DateRange.getClassSchema());
	}
	
	public static G_DateRange fromJson(String json) throws IOException {
		return SerializationHelper.fromJson(json, G_DateRange.getClassSchema());
	}
	
	public static List<G_DateRange> listFromJson(String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_DateRange.getClassSchema());
	}
}
