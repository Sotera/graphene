/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.web.components;

//This is based on Tapestry's Output component.

import java.text.Format;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;
import org.joda.time.base.AbstractPartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A component for formatting for output of JodaTime objects. It supports
 * subclasses of AbstractInstant and AbstractPartial. If the component is
 * represented in the template using an element, then the element (plus any
 * informal parameters) will be output around the formatted value.
 */
@SupportsInformalParameters
public class JodaTimeOutput {
	/**
	 * The value to be output (before formatting). If the formatted value is
	 * blank, no output is produced.
	 */
	@Parameter(required = true)
	private Object value;

	/** The format to be applied to the object. */
	@Parameter(required = false)
	private DateTimeFormatter formatter;

	/** The format to be applied to the object. */
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String style;

	/** The format to be applied to the object. */
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String pattern;

	/**
	 * This is declared so we catch slip-ups - an error will point the developer
	 * to formatter instead.
	 */
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private Format format;

	/**
	 * The element name, derived from the component template. This can even be
	 * overridden manually if desired (for example, to sometimes render a
	 * surrounding element and other times not).
	 */
	@Parameter("componentResources.elementName")
	private String elementName;

	@Inject
	private ComponentDefaultProvider defaultProvider;

	@Inject
	private ComponentResources componentResources;

	Binding defaultValue() {
		return defaultProvider.defaultBinding("value", componentResources);
	}

	void setupRender() {

		if (format != null) {
			throw new IllegalArgumentException(
					"JodaTimeOutput does not allow \"format\" parameter.  Valid parameters are \"style\", \"formatter\", and \"pattern\".  Formatter type is DateTimeFormatter.");
		}

		int formatParams = 0;

		if (style != null) {
			formatParams += 1;
		}
		if (formatter != null) {
			formatParams += 1;
		}
		if (pattern != null) {
			formatParams += 1;
		}

		if (formatParams > 1) {
			throw new IllegalArgumentException(
					"JodaTimeOutput can optionally receive \"style\" parameter, \"formatter\" parameter, or \"pattern\" parameter, but no more than one of them.  Received  "
							+ formatParams + " of them.");
		}

	}

	boolean beginRender(MarkupWriter writer) {

		String formatted = (value == null ? "" : format(value));

		if (InternalUtils.isNonBlank(formatted)) {
			if (elementName != null) {
				writer.element(elementName);

				componentResources.renderInformalParameters(writer);
			}

			writer.write(formatted);

			if (elementName != null)
				writer.end();
		}

		return false;
	}

	private String format(Object value) {
		String formatted = "";

		if (value != null) {

			// If value is an AbstractInstant - includes DateTime and
			// DateMidnight
			if (value instanceof Long) {
				DateTime d = new DateTime((long) value);
				if (style != null) {
					formatted = DateTimeFormat.forStyle(style).print(d);
				} else if (formatter != null) {
					formatted = d.toString(formatter);
				} else if (pattern != null) {
					formatted = DateTimeFormat.forPattern(pattern).print(d);
				} else {
					formatted = value.toString();
				}
			} else if (value instanceof AbstractInstant) {
				AbstractInstant ai = ((AbstractInstant) value);
				if (style != null) {
					formatted = DateTimeFormat.forStyle(style).print(ai);
				} else if (formatter != null) {
					formatted = ai.toString(formatter);
				} else if (pattern != null) {
					formatted = DateTimeFormat.forPattern(pattern).print(ai);
				} else {
					formatted = value.toString();
				}
			}

			// Else if value is an AbstractPartial - includes LocalDate,
			// LocalTime,
			// LocalDateTime, YearMonthDay, and TimeOfDay

			else if (value instanceof AbstractPartial) {
				AbstractPartial ap = ((AbstractPartial) value);
				if (style != null) {
					formatted = DateTimeFormat.forStyle(style).print(ap);
				} else if (formatter != null) {
					formatted = ap.toString(formatter);
				} else if (pattern != null) {
					formatted = DateTimeFormat.forPattern(pattern).print(ap);
				} else {
					formatted = value.toString();
				}
			}

			// Else value is an unsupported type

			else {
				throw new IllegalArgumentException(
						"JodaTimeOutput received a value of the wrong type.  Supported types are subclasses of AbstractInstant and AbstractPartial or instances of Long.  Type found is "
								+ value.getClass().getName() + ".");
			}
		}

		return formatted;
	}

	// For testing.

	void setup(Object value, String style, DateTimeFormatter formatter,
			String pattern, String elementName,
			ComponentResources componentResources) {
		this.value = value;
		this.style = style;
		this.formatter = formatter;
		this.pattern = pattern;
		this.elementName = elementName;
		this.componentResources = componentResources;
	}
}