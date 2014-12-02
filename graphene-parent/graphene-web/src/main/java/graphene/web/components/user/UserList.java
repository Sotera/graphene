package graphene.web.components.user;


import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.web.model.UserFilteredDataSource;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * This component will trigger the following events on its container (which in this example is the page):
 * {@link UserList.web.components.examples.ajax.gracefulcomponentscrud.PersonList#SELECTED}(Long personId).
 */
// @Events is applied to a component solely to document what events it may trigger. It is not checked at runtime.
@Events({ UserList.SELECTED })
public class UserList {
	public static final String SELECTED = "selected";

	// Parameters

	@Parameter(required = true)
	@Property
	private String partialName;

	@Parameter(required = true)
	@Property
	private String selectedUsername;

	// Screen fields

	@Property
	private G_User person;

	// Generally useful bits and pieces

	@Inject
	private G_UserDataAccess userDataAccess;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Request request;

	// The code

	boolean onSuccessFromFilterForm() {
		// Trigger new event "filter" which will bubble up.
		componentResources.triggerEvent("filter", null, null);
		// We don't want "success" to bubble up, so we return true to say we've handled it.
		return true;
	}

	// Handle event "selected"

	boolean onSelected(Long personId) {
		// Return false, which means we haven't handled the event so bubble it up.
		// This method is here solely as documentation, because without this method the event would bubble up anyway.
		return false;
	}

	// Getters

	public GridDataSource getPersons() {
		return new UserFilteredDataSource(userDataAccess, partialName);
	}

	public boolean isAjax() {
		return request.isXHR();
	}

	public String getLinkCSSClass() {
		if (person != null && person.getUsername().equals(selectedUsername)) {
			return "active";
		}
		else {
			return "";
		}
	}
}
