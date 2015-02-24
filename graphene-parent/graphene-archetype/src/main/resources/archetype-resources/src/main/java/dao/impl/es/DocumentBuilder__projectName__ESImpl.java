package ${package}.dao.impl.es;

import graphene.dao.DocumentBuilder;
import graphene.dao.DocumentGraphParser;
import ${package}.model.media.Media;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;

public class DocumentBuilderInstagramESImpl implements DocumentBuilder {
	@Inject
	private Logger logger;

	protected ObjectMapper mapper;

	public DocumentBuilderInstagramESImpl() {
		mapper = new ObjectMapper();
	}

	/**
	 * TODO: Consider some kind of configurable mapping between the string of
	 * _type and the class to cast to.
	 * 
	 * @param index
	 * 
	 * @param hit
	 * @return
	 */
	/**
	 * TODO: Consider some kind of configurable mapping between the string of
	 * _type and the class to cast to.
	 * 
	 * @param index
	 * 
	 * @param hit
	 * @return
	 */
	@Override
	public Object buildEntityFromDocument(final int index, final JsonNode hit) {
		// ////////////////////////////////
		String type = null;
		DoubleNode d = null;
		JsonNode source = null;
		Object o = null;
		try {
			final JsonNode x = hit.get("_type");
			if (x == null) {
				logger.error("Could not find the type of result. There may be something wrong with your ElasticSearch instance");
			}
			type = x.asText();
			d = (DoubleNode) hit.findValue("_score");
			if (d == null) {
				logger.error("Could not find the score of result. There may be something wrong with your ElasticSearch instance");
			}
			Map<String, Object> additionalProperties = null;
			// do something based on the type:

			source = hit.findValue("_source");
			if (ValidationUtils.isValid(source)) {
				o = mapper.readValue(source.toString(), Media.class);
				final Media castObj = (Media) o;
				additionalProperties = castObj.getAdditionalProperties();
				o = castObj;
			} else {
				logger.error("Could not find the source of result. There may be something wrong with your ElasticSearch instance");
			}
			if (additionalProperties != null) {
				additionalProperties.put(DocumentGraphParser.SCORE, d.asDouble());
				additionalProperties.put(DocumentGraphParser.CARDINAL_ORDER, index + 1);
				if (hit.has(DocumentGraphParser.HIGHLIGHT)) {
					additionalProperties.put(DocumentGraphParser.HIGHLIGHT,
							hit.findValue(DocumentGraphParser.HIGHLIGHT));
				}
			}
		} catch (final JsonParseException e) {

			logger.error("Parsing exception " + type + " " + e.getMessage());

		} catch (final JsonMappingException e) {

			logger.error("JSON Mapping Exception " + type + " " + e.getMessage());
			if (source != null) {
				logger.error("Source was \n\n\n" + source.toString() + "\n\n\n");
			}
		} catch (final IOException e) {

			logger.error("IO Exception " + type + " " + e.getMessage());
		}

		return o;
	}
}
