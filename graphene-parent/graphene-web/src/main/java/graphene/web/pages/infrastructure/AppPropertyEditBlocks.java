// Based on http://tapestry.apache.org/tapestry5/guide/beaneditform.html

package graphene.web.pages.infrastructure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.tapestry5.FieldTranslator;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.services.PropertyEditContext;

public class AppPropertyEditBlocks {

	@Property
	@Environmental
	private PropertyEditContext context;

	@Component
	private DateField localDate;
	@Component
	private DateField dateTime;
	
	public DateFormat getDateInputFormat() {
		return new SimpleDateFormat("yyyy mm dd");
	}
	public DateFormat getDateTimeInputFormat() {
		return new SimpleDateFormat("yyyy mm dd HH MM SS");
	}
	public FieldTranslator<?> getLocalDateTranslator() {
		return context.getTranslator(localDate);
	}

	public FieldValidator<?> getLocalDateValidator() {
		return context.getValidator(localDate);
	}
	
	
	public FieldTranslator<?> getDateTimeTranslator() {
		return context.getTranslator(dateTime);
	}

	public FieldValidator<?> getDateTimeValidator() {
		return context.getValidator(dateTime);
	}
}
