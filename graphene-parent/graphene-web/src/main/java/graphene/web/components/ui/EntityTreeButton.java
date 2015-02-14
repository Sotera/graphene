package graphene.web.components.ui;

import graphene.dao.StyleService;

import java.util.Map;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class EntityTreeButton {
	@Inject
	protected StyleService style;

	@Property
	@Parameter(required = true, autoconnect = true)
	private String typeName;
	@Property
	@Parameter(required = false, autoconnect = true)
	private String color;

	@Property
	@Parameter(required = true, autoconnect = true)
	private Map<String, Object> additionalProperties;

	@Inject
	private Logger logger;

	public String getStyle() {
		return style.getStyle(color, false);
	}
}
