package graphene.dao;

import com.fasterxml.jackson.databind.JsonNode;

public interface DocumentBuilder {
	Object buildEntityFromDocument(final int index, final JsonNode hit);
}
