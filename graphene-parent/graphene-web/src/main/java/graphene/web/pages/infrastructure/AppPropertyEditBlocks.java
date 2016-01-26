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
