package graphene.ingest.neo4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * @author mh
 * @since 02.03.11
 */
public class Neo4jDatabaseCleaner {
	private GlobalGraphOperations graph;
	private GraphDatabaseService gds;

	public Neo4jDatabaseCleaner(GraphDatabaseService gds) {
		this.gds = gds;
		this.graph = GlobalGraphOperations.at(gds);
	}

	public Map<String, Object> cleanDb() {
		return cleanDb(Long.MAX_VALUE);
	}

	public Map<String, Object> cleanDb(long maxNodesToDelete) {
		Map<String, Object> result = new HashMap<String, Object>();

		long nodes = 0, relationships = 0;
		for (Node node : graph.getAllNodes()) {
			for (Relationship rel : node.getRelationships()) {
				rel.delete();
				relationships++;
			}

			node.delete();
			nodes++;

			if (nodes >= maxNodesToDelete)
				break;
		}
		result.put("maxNodesToDelete", maxNodesToDelete);
		result.put("nodes", nodes);
		result.put("relationships", relationships);
		return result;
	}

	private void clearIndex(Map<String, Object> result) {
		IndexManager indexManager = gds.index();
				
				result.put("node-indexes",
				Arrays.asList(indexManager.nodeIndexNames()));
		result.put("relationship-indexes",
				Arrays.asList(indexManager.relationshipIndexNames()));
		try {
			for (String ix : indexManager.nodeIndexNames()) {
				final Index<Node> index = indexManager.forNodes(ix);
				getMutableIndex(index).delete();
			}
			for (String ix : indexManager.relationshipIndexNames()) {
				final RelationshipIndex index = indexManager
						.forRelationships(ix);
				getMutableIndex(index).delete();
			}
		} catch (UnsupportedOperationException uoe) {
			throw new RuntimeException(
					"Implementation detail assumption failed for cleaning readonly indexes, please make sure that the version of this extension and the Neo4j server align");
		}
	}

	private <T extends PropertyContainer> Index<T> getMutableIndex(
			Index<T> index) {
		final Class<? extends Index> indexClass = index.getClass();
		if (indexClass.getName().endsWith("ReadOnlyIndexToIndexAdapter")) {
			try {
				final Field delegateIndexField = indexClass
						.getDeclaredField("delegate");
				delegateIndexField.setAccessible(true);
				return (Index<T>) delegateIndexField.get(index);
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		} else {
			return index;
		}
	}
}