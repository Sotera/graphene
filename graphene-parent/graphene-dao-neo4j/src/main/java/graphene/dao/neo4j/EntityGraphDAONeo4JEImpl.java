package graphene.dao.neo4j;

import graphene.dao.EntityGraphDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_RelationshipType;
import graphene.model.query.EntityGraphQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mil.darpa.vande.converters.infovis.InfoVisEdge;
import mil.darpa.vande.converters.infovis.InfoVisGraphAdjacency;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.impl.core.NodeProxy;
import org.slf4j.Logger;

public class EntityGraphDAONeo4JEImpl implements
		EntityGraphDAO<InfoVisGraphAdjacency, EntityGraphQuery> {
	private static final boolean DEBUGMODE = true;

	public EntityGraphDAONeo4JEImpl(Neo4JEmbeddedService service) {
		this.service=service;
		if (this.service == null) {
			System.out.println("Waiting to initialize service...");
		}
	}

	// inject a pre-configured service.
	//@InjectService("UnifiedEntity")
	private Neo4JEmbeddedService service;
	@Inject
	private Logger logger;

	@Override
	public List<InfoVisGraphAdjacency> findByQuery(EntityGraphQuery q)
			throws Exception {
		if (DEBUGMODE) {
			return getSampleGraph();
		} else {
			List<InfoVisGraphAdjacency> ga = new ArrayList<InfoVisGraphAdjacency>();
			List<PossibleMatchByDataset> l = getPossibleMatches(q);
			for (PossibleMatchByDataset pm : l) {
				ga.addAll(getDataFromNeo4J(pm));
				// TODO: we probably need to re-adjust the code so it only
				// returns a graph for one traversal at a time.
			}
			return ga;
		}
	}

	private List<InfoVisGraphAdjacency> getSampleGraph() {
		// TODO Auto-generated method stub
		List<InfoVisGraphAdjacency> list = new ArrayList<InfoVisGraphAdjacency>();

		for (int i = 0; i < 22; i++) {
			InfoVisGraphAdjacency a = new InfoVisGraphAdjacency(
					"GraphNode" + i, "GraphNode" + i, "$color", "#83548B",
					"$type", "circle", "$dim", "" + i);
			// add some random edges
			for (int k = 0; k < 22; k++) {
				if (k != i && Math.random() < 0.05) {
					// NOTE THE ORDERING IS IMPORTANT
					// otherwise you won't get edges showing up in the graph--go
					// figure.
					a.addEdge(new InfoVisEdge("GraphNode" + k, a.getId(),
							"$color", "#557EAA"));
				}
			}
			list.add(a);
		}

		return list;
	}

	private List<PossibleMatchByDataset> getPossibleMatches(EntityGraphQuery q) {

		List<PossibleMatchByDataset> list = new ArrayList<PossibleMatchByDataset>();
		// At this point the DB should have been shut down from the ingest.
		if (service.connectToGraph()) {
			// START SNIPPET: execute
			ExecutionEngine engine = new ExecutionEngine(service.getGraphDb());
			StringBuilder sb = new StringBuilder();

			sb.append(" start x=node:NAME(VALUE='" + q.getAttribute() + "') ");
			sb.append(" match x-[:HAS_GLOBAL_ID]-i ");
			sb.append(" RETURN x.VALUE,x,i.VALUE, i ");
			sb.append(" LIMIT 2 ");
	
			System.out.println("About to run : \n" + sb.toString());

			ExecutionResult result = engine.execute(sb.toString());
			// List<String> columns = result.columns();
			// START SNIPPET: rows
			if (result != null) {
				for (Map<String, Object> row : result) {
					PossibleMatchByDataset match = new PossibleMatchByDataset();
					match.setDatasetId("Dataset1");
					match.setIdentifiers((String) row.get("x.VALUE"));
					match.setCustomerNumber((String) row.get("i.VALUE"));
					NodeProxy n = (NodeProxy) row.get("i");
					match.setNode(n);
					match.setInternalNodeId(Long.toString(n.getId()));
					list.add(match);
				}
			}
			// END SNIPPET: rows
			// resultString = engine.execute(sb.toString()).dumpToString();
			// columnsString = columns.toString();
			// System.out.println("columnsString: " + columnsString);

		}

		return list;
	}

	@Override
	public Node findOrCreateUnique(G_CanonicalPropertyType type,
			Map<String, Object> payload) {
				return null;

	}

	@Override
	public Relationship findOrCreateUnique(G_RelationshipType type,
			Map<String, Object> payload) {
				return null;

	}
	
	@Override
	public void addRelationship(Node a, Node b, Relationship r, Map<String, Object> payload){
		
	}

	/**
	 * @param q
	 * @return
	 */
	private List<InfoVisGraphAdjacency> getDataFromNeo4J(PossibleMatchByDataset q) {

		List<InfoVisGraphAdjacency> list = new ArrayList<InfoVisGraphAdjacency>();
		// At this point the DB should have been shut down from the ingest.
		if (service.connectToGraph() && q.getCustomerNumber() != null) {
//			String resultString;
//			String columnsString;
//			String nodeResult = null;
//			String rows = "";

			// START SNIPPET: execute
			ExecutionEngine engine = new ExecutionEngine(service.getGraphDb());
			StringBuilder sb = new StringBuilder();
			sb.append(" start InternalNode =node(" + q.getInternalNodeId()
					+ ") ");
			sb.append(" match Path=InternalNode-[RelationshipsPath:HAS_ACCOUNT|HAS_MEMBERSHIP|HAS_PHYSICAL|HAS_ADDRESS|HAS_COMMUNICATION_ID*1..2]-AttributeNode ");
			sb.append(" RETURN length(Path) as Length,AttributeNode,RelationshipsPath, ");
			sb.append(" InternalNode,Path, AttributeNode.VALUE? as AttributeValue ");
			sb.append(" order by length(Path) asc ");

			System.out.println("About to run : \n" + sb.toString());

			ExecutionResult result = engine.execute(sb.toString());
			List<String> columns = result.columns();
			// START SNIPPET: rows
			for (Map<String, Object> row : result) {
				NodeProxy attributeNode = (NodeProxy) row.get("AttributeNode");
				// make the node
				InfoVisGraphAdjacency a = new InfoVisGraphAdjacency(""
						+ attributeNode.getId(),
						"" + row.get("AttributeValue"), "$color", "#83548B",
						"$type", "circle", "$dim", "2");
				// add some adjacencies
				for (Entry<String, Object> column : row.entrySet()) {//FIXME: this must be wrong.  We are looping over something we're not using.
					//rows += column.getKey() + ": " + column.getValue() + "; ";
					NodeProxy node = (NodeProxy) row.get("InternalNode");

					String relationshipsPath = row.get("RelationshipsPath")
							.toString();
					a.addEdge(new InfoVisEdge("" + node.getId(), ""
							+ attributeNode.getId(), "$color", "#557EAA",
							"path", relationshipsPath));

				}
				//rows += "\n";
				list.add(a);
			}
			// System.out.println("rows: " + rows);
			// END SNIPPET: rows
			// resultString = engine.execute(sb.toString()).dumpToString();
			//columnsString = columns.toString();
			// System.out.println("columnsString: " + columnsString);

		}
		return list;
	}
}
