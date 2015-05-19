package graphene.web.components.ui;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Collection;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ReportNarrative {
	@Property
	@Parameter(required = true, autoconnect = true)
	private Collection<String> sentences;

	@Property
	private String sentence;

	@Property
	@Parameter(required = true, autoconnect = true)
	private Object r;

	public Format getNarrativeFormat() {
		return new Format() {

			@Override
			public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
				final StringBuffer sb = new StringBuffer(obj.toString());
				// TODO Auto-generated method stub
				return toAppendTo.append(sb);
			}

			@Override
			public Object parseObject(final String source, final ParsePosition pos) {
				// TODO Auto-generated method stub
				return source;
			}
		};
	}
}
