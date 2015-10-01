package graphene.dao.es.impl;

import graphene.dao.HyperGraphBuilder;
import graphene.model.extracted.ExtractedData;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.darpa.vande.generic.V_GenericGraph;

import com.fasterxml.jackson.databind.JsonNode;

public class ExtractedDataParser extends BasicParserESImpl<ExtractedData> {
	public ExtractedDataParser() {
		supported = new ArrayList<String>(1);
		supported.add("docmd");
	}

	@Override
	public G_Entity buildEntityFromDocument(final JsonNode hit, final G_EntityQuery sq) {
		final Map<String, G_Property> map = new HashMap<String, G_Property>();
		final ExtractedData p = getClassFromJSON(hit, ExtractedData.class);

		map.put(DTO, new PropertyHelper(DTO, "DTO Object", p, G_PropertyType.OTHER, G_PropertyTag.RAW));
		map.put(JSON, new PropertyHelper(JSON, "JSON Object", getRawJsonFromSource(hit), G_PropertyType.OTHER,
				G_PropertyTag.RAW));
		final JsonNode id = hit.get("_id");
		final List<G_EntityTag> tags = new ArrayList<G_EntityTag>();
		tags.add(G_EntityTag.FILE);
		final EntityHelper entity = new EntityHelper(id.asText(), tags, null, null, map);
		return entity;
	}

	@Override
	public Map<String, Object> getAdditionalProperties(final Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HyperGraphBuilder getPhgb() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V_GenericGraph parse(final G_SearchResult t, final G_EntityQuery q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPhgb(final HyperGraphBuilder phgb) {
		// TODO Auto-generated method stub

	}

}
