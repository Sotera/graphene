package graphene.model.graph;

public class G_PersistedGraph {
	private java.lang.String id;

	String graphSeed;

	String username;

	/** workspace's modified datetime */
	private java.lang.Long modified;
	/** workspace's created datetime */
	private java.lang.Long created;
	String graphJSONdata;

	public java.lang.Long getCreated() {
		return created;
	}

	public String getGraphJSONdata() {
		return graphJSONdata;
	}

	public String getGraphSeed() {
		return graphSeed;
	}

	public java.lang.String getId() {
		return id;
	}

	public java.lang.Long getModified() {
		return modified;
	}

	public String getUserName() {
		return username;
	}

	public void setCreated(final java.lang.Long created) {
		this.created = created;
	}

	public void setGraphJSONdata(final String graphJSONdata) {
		this.graphJSONdata = graphJSONdata;
	}

	public void setGraphSeed(final String graphSeed) {
		this.graphSeed = graphSeed;
	}

	public void setId(final java.lang.String id) {
		this.id = id;
	}

	public void setModified(final java.lang.Long modified) {
		this.modified = modified;
	}

	public void setUserName(final String username) {
		this.username = username;
	}
}
