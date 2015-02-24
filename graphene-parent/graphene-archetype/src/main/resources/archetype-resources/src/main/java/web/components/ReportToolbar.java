package ${package}.web.components;

import graphene.model.idl.G_SymbolConstants;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class ReportToolbar {
	@Property
	@Parameter(required = true, autoconnect = true)
	private String eventId;
	@Property
	@Parameter(required = true, autoconnect = true)
	private String eventTypeName;
	@Inject
	@Symbol(G_SymbolConstants.EXT_PATH)
	private String extPath;

	@Property
	protected String currentDate;
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

	public String getExtLink() {
		return extPath + eventId;
	}
}
