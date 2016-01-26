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
