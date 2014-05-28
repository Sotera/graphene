package graphene.model.idl;

public class G_SearchTuple<T> {
	protected G_SearchType searchType;

	protected T value;

	/**
	 * Default constructor for when we are building from scratch.
	 */
	public G_SearchTuple() {
		// TODO Auto-generated constructor stub
	}

	public G_SearchTuple(T value, G_SearchType searchType) {
		this.value = value;
		this.searchType = searchType;
	}

	public G_SearchType getSearchType() {
		return searchType;
	}

	public T getValue() {
		return value;
	}

	public void setSearchType(G_SearchType searchType) {
		this.searchType = searchType;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "G_SearchTuple ["
				+ (searchType != null ? "searchType=" + searchType + ", " : "")
				+ (value != null ? "value=" + value : "") + "]";
	}
}
