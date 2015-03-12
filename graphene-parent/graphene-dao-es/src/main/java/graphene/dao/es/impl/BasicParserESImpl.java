package graphene.dao.es.impl;

import graphene.dao.G_Parser;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idlhelper.PropertyHelper;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;

public abstract class BasicParserESImpl<T> implements G_Parser<T> {

	protected ObjectMapper mapper = new ObjectMapper();

	protected List<String> supported;

	protected G_Property addSafeString(final G_PropertyType nodeType, final String... s) {
		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		G_Property gp = null;
		if (ValidationUtils.isValid(coalesc)) {
			final List<G_PropertyTag> tags = new ArrayList<G_PropertyTag>();
			gp = new PropertyHelper(coalesc, coalesc, coalesc, nodeType, tags);
		}
		return gp;
	}

	protected G_Property addSafeStringWithTitle(final G_PropertyType nodeType, final String title, final String... s) {
		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		G_Property gp = null;
		if (ValidationUtils.isValid(coalesc)) {
			final List<G_PropertyTag> tags = new ArrayList<G_PropertyTag>();
			final String titled = StringUtils.coalesc(" ", title + ":", coalesc);
			gp = new PropertyHelper(coalesc, titled, coalesc, nodeType, tags);
		}
		return gp;
	}

	protected <T> T getClassFromJSON(final JsonNode json, final Class<T> clazz) {
		T t = null;
		try {
			t = mapper.readValue(json.findValue("_source").toString(), clazz);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public <T> T getDTO(final G_SearchResult sr, final Class<T> clazz) {
		if (ValidationUtils.isValid(sr)) {
			return (T) ((G_Entity) sr.getResult()).getProperties().get(G_Parser.DTO);
		} else {
			return null;
		}
	}

	public double getScore(final JsonNode n) {
		return ((DoubleNode) n.findValue("_score")).asDouble(0.0d);
	}

	/**
	 * @return the supported
	 */
	public final List<String> getSupported() {
		return supported;
	}

	@Override
	public List<String> getSupportedObjects() {
		return supported;
	}
}
