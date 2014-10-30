package graphene.services;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avro.AvroRemoteException;

public class G_NodeTypeAccessImpl implements G_NodeTypeAccess {
	private Map<String, G_IdType> map = new TreeMap<String, G_IdType>();

	public G_NodeTypeAccessImpl(Map<String, G_IdType> map) {
		this.map.putAll(map);
	}

	@Override
	public List<G_IdType> getNodeTypes() throws AvroRemoteException {
		return new ArrayList<G_IdType>(map.values());
	}

	@Override
	public G_IdType getNodeType(String id) throws AvroRemoteException {
		return map.get(id);
	}

	@Override
	public G_IdType getCommonNodeType(G_CanonicalPropertyType type)
			throws AvroRemoteException {
		return map.get(type.name());
	}

	@Override
	public G_CanonicalPropertyType getCommonTypeFromString(String t)
			throws AvroRemoteException {
		return G_CanonicalPropertyType.valueOf(t);
	}

	@Override
	public G_CanonicalPropertyType getCommonTypeFromType(G_IdType t)
			throws AvroRemoteException {
		return G_CanonicalPropertyType.valueOf(t.getName());
	}

	@Override
	public boolean equalsCanonical(G_IdType t,
			G_CanonicalPropertyType canonical) throws AvroRemoteException {
		return canonical.name().equals(t.getName());
	}

	@Override
	public boolean equalsString(G_IdType t, String name)
			throws AvroRemoteException {
		return t.getName().equals(name);
	}
}
