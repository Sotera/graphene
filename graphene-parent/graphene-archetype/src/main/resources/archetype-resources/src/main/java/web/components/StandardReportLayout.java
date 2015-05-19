package ${package}.web.components;

import ${package}.model.media.Media;

import java.util.Set;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class StandardReportLayout {
	@Parameter
	@Property
	Set<String> datesFiled;

	@Parameter
	@Property
	Set<String> datesOfEvents;

	@Parameter
	@Property
	Set<String> datesReceived;

	@Parameter
	@Property
	String totalAmount;

	@Property
	@Parameter(name = "theEvent")
	Media theEvent;

	@Component(parameters = { "datesFiled=datesFiled", "datesOfEvents=datesOfEvents", "datesReceived=datesReceived",
			"eventId=eventId", "eventTypeName=eventTypeName", "totalAmount=totalAmount" })
	ReportToolbar topToolbar;

	@Component(parameters = { "datesFiled=datesFiled", "datesOfEvents=datesOfEvents", "datesReceived=datesReceived",
			"eventId=eventId", "eventTypeName=eventTypeName", "totalAmount=totalAmount" })
	ReportToolbar bottomToolbar;

	/**
	 * The ID of the event
	 */
	@Parameter
	@Property
	String eventId;

	@Parameter
	@Property
	String eventTypeName = "";

}
