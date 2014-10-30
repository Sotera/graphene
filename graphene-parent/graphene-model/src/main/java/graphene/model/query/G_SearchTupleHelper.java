package graphene.model.query;

import graphene.model.idl.G_IdType;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;

public class G_SearchTupleHelper extends G_SearchTuple<String> {
	/**
	 * Basic helper constructor. Usually specificPropertyType is null if a
	 * family is specified.
	 * 
	 * @param searchType
	 * @param nodeType
	 * @param specificPropertyType
	 * @param value
	 */
	public G_SearchTupleHelper(G_SearchType searchType, G_IdType nodeType,
			String specificPropertyType, String value) {
		super(searchType, nodeType, specificPropertyType, value);
	}

	/**
	 * Helper constructor for searching by a family type
	 * 
	 * @param searchType
	 * @param nodeType
	 * @param value
	 */
	public G_SearchTupleHelper(G_SearchType searchType, G_IdType nodeType,
			String value) {
		super(searchType, nodeType, null, value);
	}

	/**
	 * Helper constructor for searching by a specific id type ( some custom
	 * value based on a customer's dataset)
	 * 
	 * @param searchType
	 * @param nodeType
	 * @param value
	 */
	public G_SearchTupleHelper(G_SearchType searchType,
			String specificPropertyType, String value) {
		super(searchType, null, specificPropertyType, value);
	}
}
