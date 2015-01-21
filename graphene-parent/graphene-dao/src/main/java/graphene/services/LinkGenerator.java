package graphene.services;

import org.apache.tapestry5.Link;

public interface LinkGenerator {

	public abstract Link set(final String schema, final String type, final String match, final String value,
			final int maxResults);

}
