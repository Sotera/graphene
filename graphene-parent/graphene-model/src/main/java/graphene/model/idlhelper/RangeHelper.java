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

import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.util.validator.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangeHelper {
	static final Logger logger = LoggerFactory.getLogger(RangeHelper.class);

	public static String toString(final G_Property p) {
		if (ValidationUtils.isValid(p.getSingletonRange())) {
			return SingletonRangeHelper.toString(p.getSingletonRange());
		} else if (ValidationUtils.isValid(p.getListRange())) {
			return ListRangeHelper.toString(p.getListRange());
		} else if (ValidationUtils.isValid(p.getBoundedRange())) {
			return "Bounded Range";
		} else if (ValidationUtils.isValid(p.getDistributionRange())) {
			return "Distribution Range";
		} else {
			return "Other Range";
		}
	}

	public static String toString(final G_PropertyMatchDescriptor p) {
		if (ValidationUtils.isValid(p.getKey(), p.getConstraint())) {
			if (ValidationUtils.isValid(p.getSingletonRange())) {
				return p.getKey() + " " + SingletonRangeHelper.toString(p.getSingletonRange());
			} else if (ValidationUtils.isValid(p.getListRange())) {
				return p.getKey() + " " + ListRangeHelper.toString(p.getListRange());
			} else if (ValidationUtils.isValid(p.getBoundedRange())) {
				return p.getKey() + " " + BoundedRangeHelper.toString(p.getBoundedRange());
			} else {
				return p.getKey() + " " + p.getConstraint() + "Other value";
			}
		} else {
			logger.error("Bad key or constraint for " + p.toString());
			return null;
		}
	}
}
