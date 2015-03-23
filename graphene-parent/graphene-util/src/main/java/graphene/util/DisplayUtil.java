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
