package graphene.model.idlhelper;

import graphene.model.idl.G_BoundedRange;
import graphene.model.idl.G_DistributionRange;
import graphene.model.idl.G_ListRange;

import java.util.ArrayList;

import com.google.gson.internal.LinkedTreeMap;

public class RangeHelper {
	public static String toString(final Object range) {
		if (range instanceof G_ListRange) {
			return ListRangeHelper.toString(range);
		} else if (range instanceof G_ListRange) {
			return SingletonRangeHelper.toString(range);
		} else if (range instanceof G_BoundedRange) {
			return "Bounded Range";
		} else if (range instanceof G_DistributionRange) {
			return "Distribution Range";
		} else if (range instanceof com.google.gson.internal.LinkedTreeMap) {
			// FIXME: This is a bad range type, caused by bad
			// deserialization.
			String r = "LinkedTreeMap";
			try {
				final LinkedTreeMap ltm = ((LinkedTreeMap) range);
				if (ltm.containsKey("values")) {
					r = ((ArrayList) ltm.get("values")).toString();
				}
				if (ltm.containsKey("value")) {
					r = (String) ltm.get("value");
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
			return r;
		} else {
			return "Range class " + range.getClass().getCanonicalName();
		}
	}

}
