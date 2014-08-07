package graphene.web.components.ui;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

@Import(library = {
		"context:core/js/plugin/easy-pie-chart/jquery.easy-pie-chart.min.js",
		"context:core/js/plugin/flot/jquery.flot.cust.js",
		"context:core/js/plugin/flot/jquery.flot.pie.js" })
public class EasyPie {
	@Property
	@Parameter(required = true)
	private String title;
	@Property
	@Parameter(value = "txt-color-green", required = false)
	private String pieStyle;
	@Property
	@Parameter(value = "50", required = false)
	private String dataPieSize;
	@Property
	@Parameter(value = "50", required = false)
	private String dataSize;
	@Property
	private boolean showIcon;
	@Property
	@Parameter(required = true)
	private int dataPercent;

	@Property
	@Parameter(value = "true", required = false)
	private boolean up;

	@Property
	@Parameter(value = "true", required = false)
	private boolean good;

	@SetupRender
	private void setupRender() {
		dataPercent = 100;
	}

	public String getIcon() {
		StringBuffer bf = new StringBuffer();
		if (up) {
			bf.append("fa fa-caret-up ");
		} else {
			bf.append("fa fa-caret-down ");
		}
		if (good) {
			bf.append("icon-color-good");
		} else {
			bf.append("icon-color-bad");
		}
		return bf.toString();
	}
}
