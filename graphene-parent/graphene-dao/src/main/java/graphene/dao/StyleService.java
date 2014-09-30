package graphene.dao;

public interface StyleService {
	public String getHexColorForNode(String key);

	public String getHexColorForEdge(String key);

	public String getStyleForNode(String key);

	public String getStyleForEdge(String key);

}
