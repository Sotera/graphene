package graphene.dao.neo4j;

import org.neo4j.graphdb.Node;

public abstract class NodeBased {
	protected Node underlyingNode;

	public Node getUnderlyingNode() {
		return underlyingNode;
	}



	public void setUnderlyingNode(Node underlyingNode) {
		this.underlyingNode = underlyingNode;
	}

	public NodeBased(Node n) {
		underlyingNode = n;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((underlyingNode == null) ? 0 : underlyingNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeBased other = (NodeBased) obj;
		if (underlyingNode == null) {
			if (other.underlyingNode != null)
				return false;
		} else if (!underlyingNode.equals(other.underlyingNode))
			return false;
		return true;
	}

}
