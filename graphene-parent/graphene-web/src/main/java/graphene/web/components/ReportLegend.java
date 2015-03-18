package graphene.web.components;

import graphene.dao.StyleService;
import graphene.model.idl.G_LegendItem;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ReportLegend {
	@Inject
	private StyleService style;

	@Property
	private G_LegendItem currentLegendItem;
	@Property
	private List<G_LegendItem> legendItems;

	@SetupRender
	private void setupRender() {
		legendItems = style.getLegendForReports();
	}
}
