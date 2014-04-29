package graphene.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.services.Heartbeat;

/**
 * CustomError must be used in conjunction with CustomForm, not Form.
 * 
 * Like Label, CustomError is a component that you associate with a field, eg. a TextField. Unlike Label, it generates
 * an empty span element. When CustomError.js detects a client-side validation error in the associated field, it puts
 * the message in the span element, sets the span's class to "msg" and the span's parent's class to "error-msg".
 * 
 */
// The @Import tells Tapestry to put a link to the file in the head of the page so that the browser will pull it in. 
@Import(library = "CustomError.js")
public class CustomError {
	
	// Parameters
	
	/**
	 * The for parameter is used to identify the {@link Field} linked to this label (it is named this way because it
	 * results in the for attribute of the label element).
	 */
	@Parameter(name = "for", required = true, allowNull = false, defaultPrefix = BindingConstants.COMPONENT)
	private Field field;
	
	// Generally useful bits and pieces

	@Environmental
	private Heartbeat heartbeat;

	private Element labelElement;
	
	// The code

	boolean beginRender(MarkupWriter writer) {
		final Field field = this.field;

		labelElement = writer.element("span", "class", "msg");

		// Since we don't know if the field has rendered yet, we need to defer writing the for and id
		// attributes until we know the field has rendered (and set its clientId property). That's
		// exactly what Heartbeat is for.

		Runnable command = new Runnable() {
			public void run() {
				String fieldId = field.getClientId();
				labelElement.forceAttributes("id", fieldId + "-msg");
			}
		};

		heartbeat.defer(command);

		// Return false to prevent the body being rendered
		return false;
	}

	void afterRender(MarkupWriter writer) {
		writer.end();
	}
}
