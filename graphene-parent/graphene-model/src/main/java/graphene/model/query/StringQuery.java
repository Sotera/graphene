package graphene.model.query;

/**
 * As a result of making all GenericDAOs require a QueryObject that extends
 * BasicQuery, this class will be substituted in old DAOs that simply used a
 * String as the QueryObject.
 * 
 * @author djue
 * 
 */
public class StringQuery extends BasicQuery {
	private String value = null;

	public StringQuery() {
		// TODO Auto-generated constructor stub
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public StringQuery(String value) {
		super();
		this.value = value;
	}
}
