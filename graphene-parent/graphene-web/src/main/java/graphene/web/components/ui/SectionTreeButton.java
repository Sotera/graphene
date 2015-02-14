package graphene.web.components.ui;

import graphene.dao.StyleService;
import graphene.util.FastNumberUtils;
import graphene.util.validator.ValidationUtils;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class SectionTreeButton {
	@Inject
	protected StyleService style;

	@Property
	@Parameter(required = true, autoconnect = true)
	private String typeName;

	@Property
	@Parameter(autoconnect = true)
	private String typeCount;
	@Property
	@Parameter(required = false, autoconnect = true)
	private String color;

	@Inject
	private Logger logger;

	public String getCardinality() {
		if (ValidationUtils.isValid(typeCount)) {
			final int c = FastNumberUtils.parseIntWithCheck(typeCount);
			if (c > 1) {
				return typeCount;
			} else {
				return null;
			}
		} else {
			return typeCount;
		}
	}

	public String getStyle() {
		return style.getStyle(color, false);
	}
}
