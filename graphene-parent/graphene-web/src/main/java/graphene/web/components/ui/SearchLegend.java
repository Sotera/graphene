package graphene.web.components.ui;

import graphene.dao.StyleService;
import graphene.model.idl.G_CanonicalPropertyType;

import org.apache.tapestry5.ioc.annotations.Inject;

public class SearchLegend {
	@Inject
	private StyleService style;

	public String getStyleFor(final String nodeType, final String searchValue) {
		return style.getStyle(nodeType, false);
	}

	public String getStyleForAddress() {
		return style.getStyle(G_CanonicalPropertyType.ADDRESS.name(), false);
	}

	public String getStyleForEmail() {
		return style.getStyle(G_CanonicalPropertyType.EMAIL_ADDRESS.name(), false);
	}

	public String getStyleForGovId() {
		return style.getStyle(G_CanonicalPropertyType.GOVERNMENTID.name(), false);
	}

	public String getStyleForHighlight() {
		return style.getHighlightStyle();
	}

	public String getStyleForName() {
		return style.getStyle(G_CanonicalPropertyType.NAME.name(), false);
	}

	public String getStyleForOccupation() {
		return style.getStyle(G_CanonicalPropertyType.OCCUPATION.name(), false);
	}

	public String getStyleForOther() {
		return style.getStyle(G_CanonicalPropertyType.OTHER.name(), false);
	}

	public String getStyleForPhone() {
		return style.getStyle(G_CanonicalPropertyType.PHONE.name(), false);
	}

	public String getStyleForVisa() {
		return style.getStyle(G_CanonicalPropertyType.OTHER.name(), false);
	}
}
