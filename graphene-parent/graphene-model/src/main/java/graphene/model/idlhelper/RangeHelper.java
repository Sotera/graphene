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
