package graphene.web.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class BasicDataTable {
	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();
	@Inject
	protected BeanModelSource beanModelSource;

	@Inject
	protected ComponentResources resources;

	public JSONObject getOptions() {

		final JSONObject json = new JSONObject(
				"bJQueryUI",
				"true",
				"bAutoWidth",
				"true",
				"sDom",
				"<\"col-sm-4\"f><\"col-sm-4\"i><\"col-sm-4\"l><\"row\"<\"col-sm-12\"p><\"col-sm-12\"r>><\"row\"<\"col-sm-12\"t>><\"row\"<\"col-sm-12\"ip>>");
		// Sort by score then by date.
		json.put("aaSorting", new JSONArray().put(new JSONArray().put(0).put("desc")));
		new JSONObject().put("aTargets", new JSONArray().put(0, 4));
		final JSONObject sortType = new JSONObject("sType", "formatted-num");
		final JSONArray columnArray = new JSONArray();
		columnArray.put(4, sortType);

		// json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));
		return json;
	}
}
