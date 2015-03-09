package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;

import com.fasterxml.jackson.databind.JsonNode;

public interface DocumentBuilder {

	G_SearchResult buildSearchResultFromDocument(final int index, final JsonNode hit, final G_EntityQuery sq);
}
