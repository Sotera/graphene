package graphene.web.components;

import graphene.util.time.JodaTimeUtil;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PropertyOutputContext;
import org.joda.time.DateTime;

public class JodaTimeBlock {

	@Environmental
	private PropertyOutputContext context;
	@Inject
	private Locale locale;

	public Date getJodaDateTime() {
		DateTime dt = (DateTime) context.getPropertyValue();
		Date javaDate = JodaTimeUtil.toJavaDate(dt);

		if (javaDate == null) {
			return null;
		}
		return javaDate;
	}

	public Object getDate() {
		return context.getPropertyValue();
	}

	public DateFormat getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}
}
