package graphene.services;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_PropertyKey;
import graphene.model.idl.G_PropertyKeyTypeAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avro.AvroRemoteException;

public class G_PropertyKeyTypeAccessImpl implements G_PropertyKeyTypeAccess {
	private Map<String, G_PropertyKey> map = new TreeMap<String, G_PropertyKey>();

	public G_PropertyKeyTypeAccessImpl(Map<String, G_PropertyKey> map) {
		this.map.putAll(map);
	}

	@Override
	public List<G_PropertyKey> getPropertyKeys() throws AvroRemoteException {
		return new ArrayList<G_PropertyKey>(map.values());
	}

	@Override
	public G_PropertyKey getPropertyKey(String id) throws AvroRemoteException {
		return map.get(id);
	}

	@Override
	public G_PropertyKey getCommonPropertyKey(G_CanonicalPropertyType type)
			throws AvroRemoteException {
		return map.get(type.name());
	}

}
