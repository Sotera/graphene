package graphene.services;

import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_EdgeTypeAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avro.AvroRemoteException;

public class G_EdgeTypeAccessImpl implements G_EdgeTypeAccess {
	private Map<String, G_EdgeType> map = new TreeMap<String, G_EdgeType>();

	public G_EdgeTypeAccessImpl(Map<String, G_EdgeType> map) {
		this.map.putAll(map);
	}

	@Override
	public List<G_EdgeType> getEdgeTypes() throws AvroRemoteException {
		return new ArrayList<G_EdgeType>(map.values());
	}

	@Override
	public G_EdgeType getEdgeType(String id) throws AvroRemoteException {
		return map.get(id);
	}

	@Override
	public G_EdgeType getCommonEdgeType(G_CanonicalRelationshipType type)
			throws AvroRemoteException {
		return map.get(type.name());
	}

}
