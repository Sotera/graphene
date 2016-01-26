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
