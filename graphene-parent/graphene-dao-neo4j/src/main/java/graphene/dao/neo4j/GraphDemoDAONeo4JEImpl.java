package graphene.dao.neo4j;

import graphene.dao.GraphDemoDAO;
import graphene.model.view.sample.DisplayNode;
import graphene.model.view.sample.GraphDemoObject;
import graphene.model.view.sample.NodeAttribute;
import graphene.model.view.sample.NodeData;
import graphene.util.FastNumberUtils;

import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.slf4j.Logger;

public class GraphDemoDAONeo4JEImpl implements GraphDemoDAO {
	@Inject
	private Neo4JEmbeddedService s;

	@Inject
	private Logger logger;

	public GraphDemoDAONeo4JEImpl() {
		super();
	}

	public GraphDemoDAONeo4JEImpl(Neo4JEmbeddedService persistenceService) {
		this.s = persistenceService;
	}

	@Override
	public GraphDemoObject getAllNodes() {
		return getNode("1");
	}

	@Override
	public GraphDemoObject getNode(String id) {
		Node startNode = null;
		try {
			long longId = FastNumberUtils.parseLongWithCheck(id);

			if (id == null)
				return new GraphDemoObject();
			startNode = s.getGraphDb().getNodeById(longId);
			if (startNode == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new GraphDemoObject();
		}
		logger.debug("got Node " + startNode.getId() + " "
				+ startNode.getProperty("name"));
		TraversalDescription desc = s.getGraphDb().traversalDescription();
		// only get one level out in all directions
		Traverser t = desc.breadthFirst().evaluator(Evaluators.atDepth(1))
				.traverse(startNode);

		HashMap<Long, Node> nodes = new HashMap<Long, Node>();
		HashMap<String, NodeAttribute> incoming = new HashMap<String, NodeAttribute>();
		HashMap<String, NodeAttribute> outgoing = new HashMap<String, NodeAttribute>();

		NodeData data = new NodeData();
		data.setName((String) startNode.getProperty("name"));
		data.setId(startNode.getId());

		for (Path path : t) {
			for (Node n : path.nodes()) {
				nodes.put(n.getId(), n);
			}
			for (Relationship rel : path.relationships()) {
				String type = rel.getType().toString();
				logger.debug("processing relationship " + type);
				Node endNode = rel.getEndNode();
				Node sNode = rel.getStartNode();
				// normally we'd process incoming and outgoing relationships
				// separately, but for some reason Neo4j can't differentiate
				// between directions when looking at all relationship types.
				// if (endNode.getId() == startNode.getId()) {

				NodeAttribute value = incoming.get(type);
				if (value == null) {
					value = new NodeAttribute();
					value.setId(type);
					value.setName("Incoming:" + type + "");
					incoming.put(type, value);
				}
				// value.getValues().add( new DisplayNode(sNode));
				// we are doing this because the traversals don't
				// differentiate between incoming and outgoing, and in our
				// particular case it's always a mutual relationship endNode.
				DisplayNode d = new DisplayNode(endNode.getId(),
						(String) endNode.getProperty("name"));
				value.getValues().add(d);
				// } else {
				NodeAttribute value2 = outgoing.get(type);
				if (value2 == null) {
					value2 = new NodeAttribute();
					value2.setId(type);
					value2.setName("Outgoing:" + type + "");
					outgoing.put(type, value2);
				}
				value2.getValues().add(d);
				// }
			}

		}
		data.getAttributes().addAll(incoming.values());
		data.getAttributes().addAll(outgoing.values());
		/*
		 * connections.each do |c| c["nodes"].each do |n| nodes[n["self"]] =
		 * n["data"] end rel = c["relationships"][0]
		 * 
		 * if rel["end"] == node["self"] incoming["Incoming:#{rel["type"]}"] <<
		 * {:values => nodes[rel["start"]].merge({:id => node_id(rel["start"])
		 * }) } else outgoing["Outgoing:#{rel["type"]}"] << {:values =>
		 * nodes[rel["end"]].merge({:id => node_id(rel["end"]) }) } end end
		 */
		// d.setData("\"data\":{\"attributes\":[{\"id\":\"friends\",\"name\":\"Incoming:friends\",\"values\":[{\"name\":\"Mark\",\"id\":\"2\"}]},{\"id\":\"friends\",\"name\":\"Outgoing:friends\",\"values\":[{\"name\":\"Mark\",\"id\":\"2\"}]}],\"name\":\"Johnathan\",\"id\":\"1\"}");
		GraphDemoObject gdo = new GraphDemoObject();
		gdo.setData(data);
		gdo.setDetails_html("<h2>Neo ID: " + startNode.getId()
				+ "</h2>\n<p class='summary'>\n" + s.getProperties(startNode)
				+ "</p>\n");
		return gdo;
	}

	@Override
	public GraphDemoObject getJSON() {
		GraphDemoObject todo = new GraphDemoObject();
		// todo.setData("This is my first todo in json");
		todo.setDetails_html("<b>This is my first todo</b>");
		return todo;
	}

	@Override
	public GraphDemoObject getHTML() {
		GraphDemoObject todo = new GraphDemoObject();
		// todo.setData("This is my first todo in html");
		todo.setDetails_html("<b>This is my first todo</b>");
		return todo;
	}
}
