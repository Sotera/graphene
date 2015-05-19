package ${package}.web.components;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ReportSummary {
	@Property
	protected String currentDate;

	@Property
	@Parameter(required = true, autoconnect = true)
	private String eventId;

	@Property
	@Parameter(required = true, autoconnect = true)
	List<String> datesReceived;
	@Property
	@Parameter(required = true, autoconnect = true)
	List<String> datesOfEvents;
	@Property
	@Parameter(required = true, autoconnect = true)
	List<String> datesFiled;
	@Property
	@Parameter(required = true, autoconnect = true)
	String totalAmount;

	@Property
	@Parameter(required = true, autoconnect = true)
	private String eventTypeName;

}
