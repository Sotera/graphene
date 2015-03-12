package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementations of this class are responsible for creating a
 * {@link G_SearchResult} from a {@link JsonNode}.
 * 
 * The implementation may delegate to one or more {@link G_Parser} if there are
 * multiple types of documents that must be handled differently.
 * 
 * @author djue
 * 
 */

public interface DocumentBuilder {

	G_SearchResult buildSearchResultFromDocument(final int index, final JsonNode hit, final G_EntityQuery sq);

	G_Parser getParserForObject(String obj);

}
