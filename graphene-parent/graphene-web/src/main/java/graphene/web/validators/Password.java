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
