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

import graphene.web.commons.CustomValidationDecorator;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.FormValidationControl;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationDecorator;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Environment;

/**
 * CustomForm simply wraps a Form so it can introduce our own custom validation decorator in place of the default one.
 */
@SupportsInformalParameters
public class CustomForm implements ClientElement, FormValidationControl {

	// Generally useful bits and pieces

	@Inject
	private Environment environment;

	@Component(publishParameters = "context, tracker, clientValidation, autoFocus, zone, secure, validationId, validate, class", 
			inheritInformalParameters = true)
	private Form form;

	// The code

	/**
	 * This beginRender() will execute before our inner form's beginRender(). It gives us the chance to change the
	 * environment first - let's push our custom validation decorator onto the environment stack.
	 */
	void beginRender(MarkupWriter writer) {
		environment.push(ValidationDecorator.class, new CustomValidationDecorator(environment, writer));
	}

	/**
	 * This afterRender() will execute after our inner form's beginRender(). Let's undo what we did in beforeRender().
	 */
	void afterRender(MarkupWriter writer) {
		environment.pop(ValidationDecorator.class);
	}

    /**
     * Returns the client id of the embedded form.
     */
    @Override
    public String getClientId()
    {
        return form.getClientId();
    }

    @Override
	public void clearErrors() {
		form.clearErrors();
	}

	@Override
	public boolean getHasErrors() {
		return form.getHasErrors();
	}

	@Override
	public boolean isValid() {
		return form.isValid();
	}

	@Override
	public void recordError(String errorMessage) {
		form.recordError(errorMessage);
	}

	@Override
	public void recordError(Field field, String errorMessage) {
		form.recordError(field, errorMessage);
	}
}
