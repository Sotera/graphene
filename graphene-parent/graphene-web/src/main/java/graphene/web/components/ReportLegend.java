package graphene.web.components;

import graphene.dao.StyleService;
import graphene.model.view.LegendItem;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ReportLegend {
	@Inject
	private StyleService style;

	@Property
	private LegendItem currentLegendItem;
	@Property
	private List<LegendItem> legendItems;

	@SetupRender
	private void setupRender() {
		legendItems = style.getLegendForReports();
	}
}
