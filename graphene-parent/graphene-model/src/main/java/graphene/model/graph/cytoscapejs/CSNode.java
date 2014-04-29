package graphene.model.graph.cytoscapejs;

import graphene.model.graph.GenericNode;


public class CSNode {
	
	public CSNode()
	{
		
	}
	public CSNodeData data;
	
	public CSNode(GenericNode node)
	{
		data = new CSNodeData(node);
	}
	

}
