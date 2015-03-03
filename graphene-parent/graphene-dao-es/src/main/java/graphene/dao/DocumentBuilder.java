package graphene.dao;

import graphene.model.idl.G_SearchResult;
import graphene.model.query.EntityQuery;

import com.fasterxml.jackson.databind.JsonNode;

public interface DocumentBuilder {
	@Deprecated
	Object buildEntityFromDocument(final int index, final JsonNode hit);

	G_SearchResult buildSearchResultFromDocument(int i, JsonNode currentHit, EntityQuery sq);
}
