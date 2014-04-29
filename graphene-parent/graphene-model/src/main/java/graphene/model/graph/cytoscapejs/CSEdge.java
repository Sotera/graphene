package graphene.model.graph.cytoscapejs;
import graphene.model.graph.GenericEdge;

public class CSEdge {
	
	public CSEdgeData data;
	
	public CSEdge()
	{
		
	}
	
	public CSEdge(GenericEdge e)
	{
		this.data = new CSEdgeData(e);
	}

}
