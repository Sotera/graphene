package graphene.dao.titan;

import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.util.validator.ValidationUtils;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public abstract class AbstractBlueprintImporter implements BlueprintImporter {

	@Inject
	protected G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	protected G_NodeTypeAccess nodeTypeAccess;

	@Inject
	protected G_PropertyKeyTypeAccess propertyKeyTypeAccess;

	protected Graph g;

	public Vertex createOrUpdateNode(String id, String idType, String nodeType,
			Vertex attachTo, String relationType, String relationValue) {
		Vertex a = null;
		if (ValidationUtils.isValid(id)) {
			a = getBasicNode(g, id, nodeType);
			setSafeProperty("idtype", idType, a);
			// now we have a valid node. Attach it to the other node provided.
			if (ValidationUtils.isValid(attachTo)) {
				Edge e = getSafeEdge(g, attachTo, relationType, a);
				setSafeProperty("value", relationValue, e);
			}
		}
		return a;
	}

	/**
	 * May return null. This is a newer more generic version
	 * 
	 * @param g
	 * @param id
	 * @param type
	 * 
	 * @return
	 */
	public Vertex getBasicNode(Graph g, String id, String type) {
		Vertex v = null;
		if (ValidationUtils.isValid(id)) {
			v = g.getVertex(id);
			if (v == null) {
				v = g.addVertex(id);
				setSafeProperty(type, id, v);
			}
		}
		return v;
	}

	public Vertex getGeoNode(Graph g, String lat, String lon, String type) {
		Vertex v = null;
		if (ValidationUtils.isValid(lat, lon)) {
			try {
				Geoshape geo = Geoshape.point(Double.parseDouble(lat),
						Double.parseDouble(lon));

				if (ValidationUtils.isValid(geo)) {
					v = g.getVertex(geo);
					if (v == null) {
						v = g.addVertex(geo);
						setSafeProperty(type, geo, v);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return v;
	}

	/**
	 * Creates the edge a - rel - b
	 * 
	 * @param a
	 * @param b
	 * @param g
	 * @param type
	 * @return
	 */
	public Edge getSafeEdge(Graph g, Vertex a, String type, Vertex b) {
		Edge e = null;
		if (ValidationUtils.isValid(a, b)) {
			try {
				G_EdgeType edgeType = edgeTypeAccess.getEdgeType(type);

				String id = a.getId().toString() + b.getId().toString()
						+ edgeType.getName();
				e = g.addEdge(id, a, b, edgeType.getName());
			} catch (AvroRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return e;
	}

	/**
	 * This will only set the value if it's safe
	 * 
	 * @param key
	 * @param value
	 * @param v
	 */
	public void setSafeProperty(String key, Object value, Vertex v) {
		if (ValidationUtils.isValid(value, v)) {
			if (v.getProperty(key) == null) {
				v.setProperty(key, value);
			}
		}
	}

	public void setSafeProperty(String key, Object value, Edge e) {
		if (ValidationUtils.isValid(value, e)) {
			if (e.getProperty(key) == null) {
				e.setProperty(key, value);
			}
		}
	}
}
