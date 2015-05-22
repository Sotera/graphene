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
