package graphene.util;

import java.text.DecimalFormat;
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
	public static final String MONEY_FORMAT_STRING = "###,###,##0.00";
	public static final String WHOLE_NUMBER_FORMAT_STRING = "###########";

	public static final String PERCENT_FORMAT_STRING = "##0.0";
	private static Logger logger = LoggerFactory
			.getLogger(DataFormatConstants.class);

	public static String formatPercent(double d) {
		return new DecimalFormat(DataFormatConstants.PERCENT_FORMAT_STRING)
				.format(d);
	}

	public static String reformatDate(String formatYouThinkItIsIn,
			String toFormat) {
		try {
			return new SimpleDateFormat(DataFormatConstants.DATE_FORMAT_STRING)
					.format(new SimpleDateFormat(formatYouThinkItIsIn)
							.parse(toFormat));
		} catch (ParseException e) {
			logger.error("Could not reformate the date identifier " + toFormat
					+ " with format " + formatYouThinkItIsIn);
			return toFormat;
		}
	}
}
