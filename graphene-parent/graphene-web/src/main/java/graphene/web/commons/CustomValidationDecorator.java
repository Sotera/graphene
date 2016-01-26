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

//
// Based on solutions by Piero Sartini at http://article.gmane.org/gmane.comp.java.tapestry.user/81532
// and Thiago H. de Paula Fiqueredo at http://tapestry.1045711.n5.nabble.com/Example-of-overriding-the-default-ValidationDecorator-td2419072.html. 
//

package graphene.web.commons;

import org.apache.tapestry5.BaseValidationDecorator;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.corelib.components.RadioGroup;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.services.Environment;

/**
 * Like all ValidationDecorators, this decorator is invoked for every field being rendered server-side.
 * 
 * If the field is "required" then this adds css class "required-field" to the field, "required-field-c" to the field's
 * containing element, "required-label" to the field's label, and "required-label-c" to the label's containing element.
 * 
 * If the field has a validation error detected server-side then this adds css class "error-field" to the field,
 * "error-field-c" to the field's containing element, "error-label" to the field's label, and "error-label-c" to the
 * label's containing element.
 */
public class CustomValidationDecorator extends BaseValidationDecorator {

	private final Environment environment;
	private final MarkupWriter markupWriter;

	public CustomValidationDecorator(Environment environment, MarkupWriter markupWriter) {
		this.environment = environment;
		this.markupWriter = markupWriter;
	}

	@Override
	public void insideLabel(Field field, Element element) {
		final String radioGroupClassName = RadioGroup.class.getName();

		if (field == null) {
			return;
		}

		if (!field.getClass().getName().equals(radioGroupClassName)) {
			if (field.isRequired()) {
				element.addClassName("required-label");
				element.getContainer().addClassName("required-label-c");
			}
		}

		if (inError(field)) {
			element.addClassName("error-label");
			element.getContainer().addClassName("error-label-c");
		}
	}

	@Override
	public void insideField(Field field) {

		if (field.isRequired()) {
			getElement().addClassName("required-field");
			getElement().getContainer().addClassName("required-field-c");
		}

		if (inError(field)) {
			getElement().addClassName("error-field");
			getElement().getContainer().addClassName("error-field-c");
		}
	}

	private boolean inError(Field field) {
		ValidationTracker tracker = getTracker();
		return tracker.inError(field);
	}

	private ValidationTracker getTracker() {
		return environment.peekRequired(ValidationTracker.class);
	}

	private Element getElement() {
		return markupWriter.getElement();
	}
}
