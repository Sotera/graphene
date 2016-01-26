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

package graphene.util;

import graphene.util.validator.ValidationUtils;

public class DisplayUtil {

	public static final String DESCENDING_FLAG = "$";

	/**
	 * Creates a log based integer for node size that is larger than minSize,
	 * and capped at maxSize (if maxSize is non-zero)
	 * 
	 * @param amount
	 * @param minSize
	 * @param maxSize
	 * @return an integer that can be used for sizing nodes on a display
	 */
	public static int getLogSize(final Long amount, final int minSize, final int maxSize) {
		int size = minSize;
		if (ValidationUtils.isValid(amount)) {
			final long additionalPixels = Math.round(Math.log(amount));
			// if we are given a max size, cap the size at that value
			if (maxSize > 0) {
				size = (int) Math.min((additionalPixels + minSize), maxSize);
			} else {
				size = (int) (additionalPixels + minSize);
			}
		}
		return size;
	}
}
