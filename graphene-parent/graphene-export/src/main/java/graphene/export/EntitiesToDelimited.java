package graphene.export;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idlhelper.PropertyHelper;

import java.util.List;

public class EntitiesToDelimited {
	public String toDelimited(final List<G_Entity> lt, final List<String> keys, final String delimiter,
			final String subDelimiter) {
		// String eol = System.getProperty("line.separator");
		final String eol = "\r\n"; // users want windows format - the above
									// would be
									// UNIX

		final StringBuilder result = new StringBuilder();

		for (final G_Entity r : lt) {
			for (final String k : keys) {
				final G_Property property = PropertyHelper.getPropertyByKey((List<G_Property>) r.getProperties()
						.values(), k);
				PropertyHelper.getStringifiedValue(property, subDelimiter);
			}
			result.append(eol);
		}

		return result.toString();

	}
}
