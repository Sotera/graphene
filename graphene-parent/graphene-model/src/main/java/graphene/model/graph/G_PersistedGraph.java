package graphene.model.graph;

public class G_PersistedGraph {
	private java.lang.String id;

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String id) {
		this.id = id;
	}

	String graphSeed;
	String username;
	/** workspace's modified datetime */
	private java.lang.Long modified;

	public java.lang.Long getModified() {
		return modified;
	}

	public void setModified(java.lang.Long modified) {
		this.modified = modified;
	}

	public java.lang.Long getCreated() {
		return created;
	}

	public void setCreated(java.lang.Long created) {
		this.created = created;
	}

	/** workspace's created datetime */
	private java.lang.Long created;

	public String getGraphSeed() {
		return graphSeed;
	}

	public void setGraphSeed(String graphSeed) {
		this.graphSeed = graphSeed;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}


	public String getGraphJSONdata() {
		return graphJSONdata;
	}

	public void setGraphJSONdata(String graphJSONdata) {
		this.graphJSONdata = graphJSONdata;
	}

	String graphJSONdata;
}
