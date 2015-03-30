package graphene.export;

import graphene.model.idl.G_Link;
import graphene.model.idl.G_TransactionResults;
import graphene.model.idlhelper.PropertyHelper;
import graphene.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CSVConverter {
	public String toCSV(final G_TransactionResults lt, final String... keys) {
		// String eol = System.getProperty("line.separator");
		final String eol = "\r\n"; // users want windows format - the above
									// would be
									// UNIX

		final StringBuilder result = new StringBuilder();

		for (final G_Link r : lt.getResults()) {
			final List<Object> objectList = new ArrayList<Object>();
			for (final String k : keys) {
				final PropertyHelper prop = PropertyHelper.from(r.getProperties().get(k));
				objectList.add(prop.getValue());
			}
			result.append(StringUtils.coalesc(",", objectList));
			result.append(eol);
		}

		return result.toString();

	}
}
