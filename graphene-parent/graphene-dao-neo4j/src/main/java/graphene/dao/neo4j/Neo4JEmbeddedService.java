package graphene.dao.neo4j;

import graphene.util.jvm.JVMHelper;
import graphene.util.validator.ValidationUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.ConstraintViolationException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.graphdb.index.UniqueFactory.UniqueNodeFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 */
public class Neo4JEmbeddedService {

	// START SNIPPET: shutdownHook
	// private static void registerShutdownHook(final GraphDatabaseService
	// graphDb) {
	// // Registers a shutdown hook for the Neo4j instance so that it
	// // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	// // running application).
	// Runtime.getRuntime().addShutdownHook(new Thread() {
	// @Override
	// public void run() {
	// if (graphDb != null) {
	// System.out
	// .println("[[[[[[[[[[[[[[[[[[Shuting down Neo4JService from registerShutdownHook]]]]]]]]]]]]]]]]]]");
	// graphDb.shutdown();
	// JVMHelper.immolativeShutdown();
	// }
	// }
	// });
	// }

	private boolean connected;

	// Where on the disk the database is stored
	private String databaseLocation;

	boolean dropExisting = false;

	private boolean embedded;

	private ExecutionEngine executionEngine;

	private UniqueNodeFactory factory;

	private GlobalGraphOperations ggo;

	private GraphDatabaseFactory graphDatabaseFactory;

	private GraphDatabaseService graphDb;

	private String indexName;

	@Inject
	private Logger logger;

	private String nodeId;

	// Index of all nodes
	private Index<Node> nodeIndex;

	// Where on the disk the properties file is stored.
	private String propertiesFileLocation;

	// Cached store of unique node factories
	private HashMap<String, UniqueNodeFactory> uniqueNodeFactoryMap = new HashMap<String, UniqueNodeFactory>(
			2);

	/**
	 * Specify the path to the properties file.
	 * 
	 * @param configuration
	 */
	@Inject
	public Neo4JEmbeddedService(Map<String, String> configuration) {
		this.propertiesFileLocation = configuration
				.get("propertiesFileLocation");
	}

	/**
	 * Specify the path to the properties file.
	 * 
	 * @param propertiesFileName
	 */
	public Neo4JEmbeddedService(String propertiesFileName) {
		this.propertiesFileLocation = propertiesFileName;
	}

	public Transaction beginTx() {
		return graphDb.beginTx();
	}

	public boolean connectToGraph() {
		if (connected && graphDb != null) {
			return true;
		} else if (databaseLocation == null || databaseLocation.isEmpty()) {
			if (loadFromPropertiesFile()) {
				if (dropExisting) {
					try {
						File f = new File(databaseLocation);
						if (f.exists()) {
							logger.info("Attempting to delete existing files at "
									+ databaseLocation);
							FileUtils.deleteRecursively(f);
							logger.info("Deleted.");
						}

					} catch (Exception e) {
						logger.error("Could not delete files at "
								+ databaseLocation);
						// still proceed
					}
				}
			} else {
				return false;
			}
		}

		if (databaseLocation != null && !databaseLocation.isEmpty()) {
			try {
				if (graphDatabaseFactory == null) {
					graphDatabaseFactory = new GraphDatabaseFactory();
				}
				graphDb = graphDatabaseFactory
						.newEmbeddedDatabase(databaseLocation);

				ggo = GlobalGraphOperations.at(graphDb);
				executionEngine = new ExecutionEngine(graphDb);
				// registerShutdownHook(graphDb);
				connected = true;
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				connected = false;
			}
		} else {
			connected = false;
		}
		return connected;
	}

	public String createId(String propertyToUse, Node... array) {
		String newId = "";
		for (Node a : array) {
			if (a != null) {
				newId += a.getProperty(propertyToUse);
			}
		}
		return (newId.equals("") ? null : newId);
	}

	public void createNewIndex(Label labelToIndex, String propertyToIndexOn) {
		// START SNIPPET: createIndex
		IndexDefinition indexDef = null;
		try (Transaction tx = graphDb.beginTx()) {
			Schema schema = graphDb.schema();
			indexDef = schema.indexFor(labelToIndex).on(propertyToIndexOn)
					.create();
			tx.success();
		} catch (ConstraintViolationException cve) {
			logger.info("Neo4J Index already present for Label "
					+ labelToIndex.name() + " on property " + propertyToIndexOn);
		}
		// END SNIPPET: createIndex
		// START SNIPPET: wait
		if (indexDef != null) {
			try (Transaction tx = graphDb.beginTx()) {
				Schema schema = graphDb.schema();
				schema.awaitIndexOnline(indexDef, 15, TimeUnit.SECONDS);
				logger.info("Index for Label " + labelToIndex.name()
						+ " created on property " + propertyToIndexOn);
			}
		}

		// END SNIPPET: wait
	}

	public Node createNonUniqueNode() {
		return graphDb.createNode();
	}

	/**
	 * Helper method for creating nodes. Does not check for uniqueness.
	 * 
	 * @param props
	 * @return
	 */
	public Node createNonUniqueNode(Map<String, Object> props) {
		return setProperties(graphDb.createNode(), props);
	}

	public boolean createUniqueConstraint(Label label, String uniqueProperty) {

		try (Transaction tx = graphDb.beginTx()) {
			graphDb.schema().constraintFor(label)
					.assertPropertyIsUnique(uniqueProperty).create();
			tx.success();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean dropExisting() {
		return dropExisting(databaseLocation);
	}

	public boolean dropExisting(String path) {
		try {
			File existingGraphDb = new File(path);
			if (existingGraphDb.exists()) {
				FileUtils.deleteRecursively(existingGraphDb);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getDatabaseLocation() {
		return databaseLocation;
	}

	public ExecutionEngine getExecutionEngine() {
		return executionEngine;
	}

	public UniqueNodeFactory getFactory() {
		return factory;
	}

	/**
	 * @return the GlobalGraphOperations
	 */
	public GlobalGraphOperations getGgo() {
		return ggo;
	}

	public GraphDatabaseFactory getGraphDatabaseFactory() {
		return graphDatabaseFactory;
	}

	/**
	 * @return the graphDb
	 */
	public GraphDatabaseService getGraphDb() {
		if (!connectToGraph()) {
			logger.error("Could not connect to graph at this time.");
			return null;
		} else {
			return graphDb;
		}
	}

	/**
	 * Helper method to get indexes by Label, wrapped in a tx
	 * 
	 * @param label
	 * @return one or more IndexDefinitions to iterate over, or null if there
	 *         were none matching the label.
	 */
	public Iterable<IndexDefinition> getIndexByLabel(Label label) {
		Iterable<IndexDefinition> x = null;
		try (Transaction tx = graphDb.beginTx()) {
			x = graphDb.schema().getIndexes(label);
		}
		return x;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getLocation() {
		return databaseLocation;
	}

	public Index<Node> getNodeIndex() {
		return nodeIndex;
	}

	/**
	 * Helper method to get an index, wrapped in a tx
	 * 
	 * @param nameOfIndex
	 * @return
	 */
	public Index<Node> getNodeIndexByName(String nameOfIndex) {
		Index<Node> x = null;
		try (Transaction tx = graphDb.beginTx()) {
			x = graphDb.index().forNodes(nameOfIndex);
		}
		return x;
	}

	/**
	 * Helper method for creating unique nodes (based on a single index lookup).
	 * Note you may want to add your nodes to other indexes.
	 * 
	 * @param key
	 * 
	 * @param indexName
	 * @param key
	 *            the key to find the entity under in the index.
	 * @param value
	 * @param value
	 *            the value the key is mapped to for the entity in the index.
	 * 
	 * @param nodeProperties
	 * @return
	 */
	// public Node getOrCreateUniqueNode(String indexName, String key,
	// Object value, final Map<String, Object> nodeProperties) {
	// if (ValidationUtils.isValid(indexName, key, value)) {
	//
	// if (value instanceof Number) {
	// value = ValueContext.numeric((Number) value);
	// }
	// UniqueNodeFactory f = getOrCreateUniqueNodeFactory(indexName);
	// Map<String, Object> p = ValidationUtils
	// .getSafeProperties(nodeProperties);
	// Node n = f.getOrCreate(key, value);
	// return n;
	// } else {
	// // throw new IllegalArgumentException("Unique index " + indexName
	// // + " key " + key + " value must not be null");
	// return null;
	// }
	// }

	public Node getOrCreateUniqueNode(Label indexName, String key,
			Object value, final Map<String, Object> nodeProperties) {
		if (ValidationUtils.isValid(indexName, key, value)) {
			Node result = null;
			ResourceIterator<Node> resultIterator = null;
			try (Transaction tx = graphDb.beginTx()) {
				String queryString = "MERGE (n:" + indexName.name() + " {"
						+ key + ": {var}}) RETURN n";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("var", value);
				resultIterator = executionEngine.execute(queryString,
						parameters).columnAs("n");
				result = resultIterator.next();
				resultIterator.close();
				tx.success();
				return result;
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return null;
	}

	/**
	 * Use this only if you're not going to maintain your own unique factory
	 * instances (which is the preferred way)
	 * 
	 * @param nodeId
	 * @return
	 */
	// public Node getOrCreateNodeWithUniqueFactory(String nodeId) {
	// return factory.getOrCreate(nodeId, nodeId);
	// }

	/**
	 * Use this only if you're not going to maintain your own unique factory
	 * instances (which is the preferred way)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	// public Node getOrCreateNodeWithUniqueFactory(String key, String value) {
	// if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
	// return null;
	// }
	// return factory.getOrCreate(key, value);
	// }

	public UniqueNodeFactory getOrCreateUniqueNodeFactory(String indexName) {
		if (uniqueNodeFactoryMap.containsKey(indexName)) {
			return uniqueNodeFactoryMap.get(indexName);
		} else {
			UniqueFactory.UniqueNodeFactory factory = new UniqueFactory.UniqueNodeFactory(
					graphDb, indexName) {
				protected void initialize(Node node,
						Map<String, Object> nodeProperties) {
					setProperties(node, nodeProperties);
				}
			};
			uniqueNodeFactoryMap.put(indexName, factory);
			return factory;
		}
	}

	public Relationship getOrCreateUniqueRelationship(final Node startNode,
			final Node endNode, final RelationshipType type, Direction d,
			final Map<String, Object> properties) {
		if (startNode != null && endNode != null && type != null) {
			for (Relationship relationship : startNode
					.getRelationships(type, d)) {
				if (relationship.getEndNode().equals(endNode)) {
					// don't make a new relationship
					return relationship;
				}
			}
			Relationship r = startNode.createRelationshipTo(endNode, type);
			setProperties(r, properties);
			return r;
		} else {
			return null;
		}
	}

	// Works for Nodes and Relationships!
	public String getProperties(PropertyContainer primitive) {
		assert primitive != null;
		String properties = "<ul>";
		for (String k : primitive.getPropertyKeys()) {
			properties += "<li><b>" + k + "</b> " + primitive.getProperty(k)
					+ "</li>";
		}
		properties += "</ul>";
		return properties;
	}

	public String getPropertiesFileLocation() {
		return propertiesFileLocation;
	}

	// public String getProperties(Node n) {
	// if (n != null) {
	// String properties = "<ul>";
	// for (String s : n.getPropertyKeys()) {
	// properties += "<li><b>" + s + "</b> " + n.getProperty(s)
	// + "</li>";
	// }
	// properties += "</ul>";
	// return properties;
	// }
	// return null;
	// }

	/**
	 * Helper method to get an index, wrapped in a tx
	 * 
	 * @param nameOfIndex
	 * @return
	 */
	public Index<Relationship> getRelationshipIndexByName(String nameOfIndex) {
		Index<Relationship> x = null;
		try (Transaction tx = graphDb.beginTx()) {
			x = graphDb.index().forRelationships(nameOfIndex);

		}
		return x;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean isDropExisting() {
		return dropExisting;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	// This will close the graph when the registry is shutdown.
	@PostInjection
	public void listenForShutdown(RegistryShutdownHub hub) {
		hub.addRegistryShutdownListener(new Runnable() {
			public void run() {

				if (graphDb != null) {
					System.out
							.println("[[[[[[[[[[[[[[[[[[listenForShutdown: Shuting down Neo4JService because Tapestry registry is shutting down]]]]]]]]]]]]]]]]]]");
					graphDb.shutdown();
					JVMHelper.immolativeShutdown();
				} else {
					System.out
							.println("[[[[[[[[[[[[[[[[[[listenForShutdown: Neo4JService was already shut down]]]]]]]]]]]]]]]]]]");
				}
			}
		});
	}

	public boolean loadFromPropertiesFile() {
		if (propertiesFileLocation == null || propertiesFileLocation.isEmpty()) {
			logger.error("No property file specified");
			return false;
		}
		Properties prop = new Properties();
		// load a properties file
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			InputStream is = classLoader
					.getResourceAsStream(propertiesFileLocation);
			prop.load(is);
			String databaseLocationRaw = (prop.getProperty("databaseLocation"));
			logger.info("Neo4j Embedded Database Location string provided by properties file was "
					+ databaseLocationRaw);
			databaseLocation = graphene.util.fs.FileUtils
					.convertSystemProperties(databaseLocationRaw);
			logger.info("Neo4j Embedded Database Location resolved to "
					+ databaseLocation);

			dropExisting = (prop.getProperty("dropExisting")
					.equalsIgnoreCase("true"));
			embedded = prop.getProperty("embedded").equalsIgnoreCase("true");
			indexName = prop.getProperty("indexName");
			nodeId = prop.getProperty("nodeId");

			logger.info("Defined Neo4j Embedded Database Service with parameters: "
					+ this.toString());
		} catch (Exception e) {
			logger.error("Could not load properties file "
					+ propertiesFileLocation + ":" + e.getMessage());
			return false;
		}
		return true;
	}

	public void removeAll() {

		logger.info("Removing all nodes and references.");

		try (Transaction tx = graphDb.beginTx()) {

			for (Relationship relationship : ggo.getAllRelationships()) {
				relationship.delete();
			}
			logger.info("Deleted all relationships.");
			for (Node node : ggo.getAllNodes()) {
				node.delete();
			}

			tx.success();
			logger.info("Deleted all nodes.");
		}
	}

	public boolean removeAllNodes() {
		return removeSomeNodes(-1);
	}

	public boolean removeSomeNodes(long maxNodesToDelete) {
		Iterator<Node> iter = ggo.getAllNodes().iterator();
		long nodesDeleted = 0, relationships = 0;
		while (iter.hasNext() && maxNodesToDelete > 0
				&& nodesDeleted <= maxNodesToDelete) {
			try (Transaction tx = getGraphDb().beginTx()) {
				Node node = iter.next();
				for (Relationship rel : node.getRelationships()) {
					rel.delete();
					relationships++;
				}

				node.delete();
				nodesDeleted++;
				tx.success();

			}
		}
		logger.info("Deleted " + nodesDeleted + " nodes and " + relationships
				+ "relationships.");
		return true;
	}

	public void setDatabaseLocation(String databaseLocation) {
		this.databaseLocation = databaseLocation;
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	public void setFactory(UniqueNodeFactory factory) {
		this.factory = factory;
	}

	/**
	 * @param graphOperations
	 *            the graphOperations to set
	 */
	public void setGgo(GlobalGraphOperations graphOperations) {
		this.ggo = graphOperations;
	}

	public void setGraphDatabaseFactory(
			GraphDatabaseFactory graphDatabaseFactory) {
		this.graphDatabaseFactory = graphDatabaseFactory;
	}

	/**
	 * @param graphDb
	 *            the graphDb to set
	 */
	public void setGraphDb(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public void setLocation(String location) {
		this.databaseLocation = location;
	}

	public void setNodeIndex(Index<Node> nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	/**
	 * Helper method to assign all the map properties to the node, then return
	 * the node. Does not use transactions internally.
	 * 
	 * @param primitive
	 * @param properties
	 * @return
	 */
	public <T extends PropertyContainer> T setProperties(T primitive,
			Map<String, Object> properties) {
		assert primitive != null;
		if (properties == null || properties.isEmpty())
			return primitive;
		for (Map.Entry<String, Object> prop : properties.entrySet()) {
			if (prop.getValue() == null) {
				primitive.removeProperty(prop.getKey());
			} else {
				primitive.setProperty(prop.getKey(), prop.getValue());
			}
		}
		return primitive;
	}

	public void setPropertiesFileLocation(String propertiesFileLocation) {
		this.propertiesFileLocation = propertiesFileLocation;
	}

	public void shutDown() {
		System.out.println();
		System.out.println("Shutting down database ...");
		// START SNIPPET: shutdownServer
		if (graphDb != null) {
			graphDb.shutdown();
			graphDb = null;
		}

		connected = false;
		// END SNIPPET: shutdownServer
	}

	@Override
	public String toString() {
		return "Neo4JService [connected=" + connected + ", dropExisting="
				+ dropExisting + ", embedded=" + embedded + ", indexName="
				+ indexName + ", location=" + databaseLocation + ", nodeId="
				+ nodeId + ", propertiesFileName=" + propertiesFileLocation
				+ "]";
	}

}
