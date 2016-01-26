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

import graphene.util.time.JodaTimeUtil;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFormatConstants {
	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	/**
	 * String amountString = new DecimalFormat(
	 * DataFormatConstants.MONEY_FORMAT_STRING).format(sum.toString());
	 */
	public static final String MONEY_FORMAT_STRING = "###,###,##0";
	public static final String WHOLE_NUMBER_FORMAT_STRING = "###########";
	public static final String SCORE_FORMAT_STRING = "0.000";
	public static final String PERCENT_FORMAT_STRING = "##0.0";
	private static Logger logger = LoggerFactory.getLogger(DataFormatConstants.class);
	public final static NumberFormat formatter = NumberFormat.getCurrencyInstance();

	/**
	 * Given milliseconds since epoch, return a standard ISO formatted date
	 * string.
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String formatDate(final long milliseconds) {
		final SimpleDateFormat sdf = new SimpleDateFormat(DataFormatConstants.ISO_DATE_FORMAT);
		return sdf.format(JodaTimeUtil.toDateTime(milliseconds).toDate());
	}

	public static String formatMoney(final Double d) {
		return new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING).format(d);
	}

	public static String formatMoney(final Integer i) {
		return new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING).format(i);
	}

	public static String formatMoney(final Long l) {
		return new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING).format(l);
	}

	/**
	 * Expects a double from 0 to 100, and will format it with
	 * PERCENT_FORMAT_STRING
	 * 
	 * @param d
	 * @return
	 */
	public static String formatPercent(final double d) {
		return new DecimalFormat(DataFormatConstants.PERCENT_FORMAT_STRING).format(d);
	}

	public static String formatScore(final Double d, final Double defaultValue) {
		if (d == null) {
			return new DecimalFormat(DataFormatConstants.SCORE_FORMAT_STRING).format(defaultValue);
		} else {
			return new DecimalFormat(DataFormatConstants.SCORE_FORMAT_STRING).format(d);
		}
	}

	public static Format getMoneyFormat() {
		return new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING);
	}

	public static String reformatDate(final String formatYouThinkItIsIn, final String toFormat) {
		try {
			return new SimpleDateFormat(DataFormatConstants.DATE_FORMAT_STRING).format(new SimpleDateFormat(
					formatYouThinkItIsIn).parse(toFormat));
		} catch (final ParseException e) {
			logger.error("Could not reformate the date identifier " + toFormat + " with format " + formatYouThinkItIsIn);
			return toFormat;
		}
	}
}
