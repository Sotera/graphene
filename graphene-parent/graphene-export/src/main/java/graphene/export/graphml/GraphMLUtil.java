package graphene.export.graphml;

public class GraphMLUtil {
	public static final String SCHEMA_LOCATION = "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd";
	
	@SuppressWarnings("rawtypes")
	public static final Class[] GRAPHML_CLASSES = new Class[]{GraphML.class, Graph.class, GraphData.class, GraphDataXML.class};
}
