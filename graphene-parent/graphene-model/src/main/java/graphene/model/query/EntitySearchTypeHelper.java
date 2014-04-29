package graphene.model.query;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_Delimiter;
import graphene.model.idl.G_SearchType;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author djue This is a more complex version of {@link SearchTypeHelper},
 *         which accounts for property types in two ways: a family type and a
 *         specific type. If the specific type is specified, it overrides any
 *         family type.
 * @see SearchTypeHelper
 * @see {@link G_SearchType}
 * @see {@link G_CanonicalPropertyType}
 */
public class EntitySearchTypeHelper {
	private static Logger logger = LoggerFactory
			.getLogger(SearchTypeHelper.class);

	private EntitySearchTypeHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param multiTermString
	 *            a delimited string with optional search types and property
	 *            types, using delimiters from {@link G_Delimiter}
	 * @param defaultSearchType
	 *            the {@link G_SearchType} to use if none is specified.
	 * @param defaultPropertyType
	 *            the {@link G_CanonicalPropertyType} if none is specified
	 * @return
	 */
	public static List<EntitySearchTuple<String>> processSearchList(
			String multiTermString, G_SearchType defaultSearchType,
			G_CanonicalPropertyType defaultPropertyType) {

		// Start off with a small list by default, it will expand if necessary.
		List<EntitySearchTuple<String>> list = new ArrayList<EntitySearchTuple<String>>(
				2);

		/*
		 * First break up multiple values by a delimiter.
		 */
		for (String s : StringUtils.tokenizeToStringCollection(multiTermString,
				G_Delimiter.MULTIPLE_VALUE.getValueString())) {

			try {
				/*
				 * Now process the individual value (if valid) and turn it into
				 * a search tuple
				 */
				if (ValidationUtils.isValid(s)) {
					list.add(EntitySearchTypeHelper.processSearchType(s,
							defaultSearchType, defaultPropertyType));
				}
			} catch (Exception e) {
				// catch the error and log it, but keep calm and carry on.
				logger.error(e.getMessage());
			}
		}
		return list;
	}

	/**
	 * Breaks down originalString which is in a form like these
	 * 
	 * 3:accountNumber=-foo
	 * 
	 * 2:phoneNumber=12323
	 * 
	 * @param originalString
	 * @param defaultSearchType
	 * @return
	 * @throws Exception
	 */
	public static EntitySearchTuple<String> processSearchType(
			String originalString, G_SearchType defaultSearchType,
			G_CanonicalPropertyType defaultPropertyType) throws Exception {

		// Build from scratch since we don't know if it's malformed or missing
		// values.
		EntitySearchTuple<String> tuple = new EntitySearchTuple<String>();

		/*
		 * Split the original string into it's SearchType and the remainder of
		 * the string
		 */
		String[] searchValArray = StringUtils.tokenizeToStringArray(
				originalString, G_Delimiter.SEARCH_TYPE.getValueString());

		G_SearchType st = null;
		String val = null;
		if (searchValArray == null) {
			throw new Exception("Error parsing search type: null");
		} else if (searchValArray.length == 1) {
			/*
			 * This means no search type specified, so use the default search
			 * type. We'll treat the first value, index 0, as the 'remainder'
			 */
			st = defaultSearchType;
			val = searchValArray[0];
		} else if (searchValArray.length == 2) {
			/*
			 * We found a search type, and we have a remainder.
			 */
			st = G_SearchType.fromValue(searchValArray[0]);
			if (st.equals(G_SearchType.COMPARE_INVALID)) {
				/*
				 * Here we can decide to either skip this term, or be nice and
				 * use the default search type. Let's be nice in this case, but
				 * here is where you can change that behavior in the future.
				 */
				st = defaultSearchType;
			}
			/*
			 * Treat the remainder of the originalString as the
			 * propertyType+valueToSearchOn
			 */
			val = searchValArray[1];
		} else {
			throw new Exception("Error parsing search type");
		}

		/*
		 * By the time we get here, val is something like
		 * 
		 * Account=1234
		 * 
		 * or
		 * 
		 * Name=-Bob
		 * 
		 * or
		 * 
		 * 57=kittens
		 * 
		 * We're going to parse out the propertyType delimiter (in this example
		 * it's '=', but the G_Delimiter defines the actual value), and then set
		 * the corresponding values in the tuple.
		 */
		String[] typePlusValueArray = StringUtils.tokenizeToStringArray(val,
				G_Delimiter.PROPERTY_TYPE.getValueString());
		G_CanonicalPropertyType pt = defaultPropertyType;
		if (typePlusValueArray == null) {
			throw new Exception("Error parsing typePlusValueArray: null");
		} else if (typePlusValueArray.length == 1) {
			/*
			 * No property type specified, use the default property type
			 */
			tuple.setFamily(defaultPropertyType);
			tuple.setValue(typePlusValueArray[0]);
		} else if (typePlusValueArray.length == 2) {

			if (org.apache.commons.lang.StringUtils
					.isNumeric(typePlusValueArray[0])) {
				tuple.setSpecificPropertyType(typePlusValueArray[0]);
			} else {
				tuple.setFamily(G_CanonicalPropertyType
						.fromValue(typePlusValueArray[0]));
			}
			tuple.setValue(typePlusValueArray[1]);
		} else {
			throw new Exception("Error parsing property type");
		}

		tuple.setSearchType(st);
		tuple.setFamily(pt);
		tuple.setValue(val);
		return tuple;
	}
}