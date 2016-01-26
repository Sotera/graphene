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

package graphene.model.idlhelper;

import graphene.model.idl.G_BoundedRange;
import graphene.util.validator.ValidationUtils;

public class BoundedRangeHelper {

	public static String toString(final G_BoundedRange p) {
		if (ValidationUtils.isValid(p.getStart(), p.getEnd())) {
			return p.getStart().toString() + " and " + p.getEnd().toString();
		} else if (ValidationUtils.isValid(p.getStart())) {
			return "Greater than " + p.getStart().toString();
		} else if (ValidationUtils.isValid(p.getEnd())) {
			return "Less than " + p.getEnd().toString();
		} else {
			return "Undescribable Range";
		}
	}

}
