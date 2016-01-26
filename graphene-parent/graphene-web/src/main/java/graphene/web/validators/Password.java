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

package graphene.web.validators;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.validator.AbstractValidator;

public class Password extends AbstractValidator<Void, String> {
	private String validationMessage;
	private String validation;

	public Password() {
		super(null, String.class, "validate-password");
	}

	public Password(final String validation, final String validationMessage) {
		super(null, String.class, "validate-password");
		this.validation = validation;
		this.validationMessage = validationMessage;
	}

	private String buildMessage(final MessageFormatter formatter, final Field field) {
		return formatter.format(field.getLabel());
	}

	/**
	 * @return the validation
	 */
	public String getValidation() {
		return validation;
	}

	/**
	 * @return the validationMessage
	 */
	public String getValidationMessage() {
		return validationMessage;
	}

	/**
	 * Adds client-side validation by writing javascript into the page as it is
	 * rendered.
	 */
	@Override
	public void render(final Field field, final Void constraintValue, final MessageFormatter formatter,
			final MarkupWriter writer, final FormSupport formSupport) {
		formSupport.addValidation(field, "password", buildMessage(formatter, field), null);
	}

	/**
	 * @param validation
	 *            the validation to set
	 */
	public void setValidation(final String validation) {
		this.validation = validation;
	}

	/**
	 * @param validationMessage
	 *            the validationMessage to set
	 */
	public void setValidationMessage(final String validationMessage) {
		this.validationMessage = validationMessage;
	}

	/**
	 * Server-side validation.
	 */
	@Override
	public void validate(final Field field, final Void constraintValue, final MessageFormatter formatter,
			final String value) throws ValidationException {
		// This does the server-side validation
		if (value != null) {
			if (!value.matches(validation)) {
				throw new ValidationException(buildMessage(formatter, field));
			}
		}

	}

}
