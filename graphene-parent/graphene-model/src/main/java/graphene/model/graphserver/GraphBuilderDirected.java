package graphene.model.graphserver;

import graphene.model.graph.DirectableObject;
import graphene.model.graph.DirectedNode;
import graphene.model.graph.GenericEdge;
import graphene.model.graph.GenericGraph;
import graphene.model.graph.GenericNode;
import graphene.model.graph.entity.NodeColors;
import graphene.model.graph.entity.NodeFactory;
import graphene.model.graph.entity.NodeList;
import graphene.model.query.BasicQuery;
import graphene.model.view.entities.IdProperty;
import graphene.util.stats.TimeReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author PWG
 * 
 */
public class GraphBuilderDirected implements GraphBuilderWithDirection {
	static Logger logger = LoggerFactory.getLogger(GraphBuilderDirected.class);

	static boolean oneEdgePerPair = false; // set true if graph vis doesn't
											// allow
	// edges between the same nodes in both directions.
	// TODO: this should be a configuration option

	private boolean biPartite = false;

	// Set up some local structures so we don't have to keep passing them around
	private Map<String, GenericEdge> edgeMap = new HashMap<String, GenericEdge>();
	// private boolean customerCentric = true;
	// private boolean accountCentric = false;

	private long endDate = 0;

	private boolean GQT_Style = false;

	private DirectableObjectLoader loader;

	private int maxEdgesPerNode = 50; //

	private int maxNodes = 300; // MFM

	private int minWeight = 0;

	private NodeFactory nodeFactory = new NodeFactory();

	private NodeList nodeList = new NodeList();

	private BasicQuery originalQuery;

	private boolean showLeafNodes = true;
	private boolean showNameNodes = true;
	private long startDate = 0;

	/**
	 * Constructor.
	 * 
	 * @param GQT_Style
	 *            boolean. True means show the nodes as text so as to resemble
	 *            the legacy Graph Query Tool
	 * @param i
	 */
	public GraphBuilderDirected(boolean GQT_Style) {
		this.GQT_Style = GQT_Style;
	}

	public GraphBuilderDirected() {
	}

	private int countNodes() {
		int n = nodeList.countNodes();
		logger.debug("Nbr of nodes " + n);
		return n;

	}

	public Map<String, GenericEdge> getEdgeMap() {
		return edgeMap;
	}

	
	@Override
	public long getEndDate() {
		return endDate;
	}

	
	@Override
	public DirectableObjectLoader getLoader() {
		return loader;
	}

	public int getMaxEdgesPerNode() {
		return maxEdgesPerNode;
	}

	public int getMaxNodes() {
		return maxNodes;
	}

	public int getMinWeight() {
		return minWeight;
	}

	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}

	public NodeList getNodeList() {
		return nodeList;
	}

	public BasicQuery getOriginalQuery() {
		return originalQuery;
	}

	// ---------------------------------------------------------------------

	
	@Override
	public long getStartDate() {
		return startDate;
	}

	public boolean isBiPartite() {
		return biPartite;
	}

	public boolean isGQT_Style() {
		return GQT_Style;
	}

	public boolean isShowLeafNodes() {
		return showLeafNodes;
	}

	public boolean isShowNameNodes() {
		return showNameNodes;
	}

	/**
	 * Make an edge between two nodes if one doesn't already exist
	 * 
	 * @param src
	 *            GRNode source
	 * @param target
	 *            GRNode target
	 * @param degree
	 *            int degree
	 */

	private void makeEdge(GenericNode src, GenericNode target, int degree,
			int wt, String value, List<IdProperty> edgeAttrs) { // MFM

		// Check for a duplicate edge
		String fromkey = src.getId() + ":" + target.getId();
		GenericEdge e = edgeMap.get(fromkey);
		if (e != null) { // duplicate edge
			e.setCount(e.getCount() + 1); // MFM TODO: REVISIT THIS
			return;
		}

		if (oneEdgePerPair) {
			// check for the reverse edge
			String tokey = target.getId() + ":" + src.getId();
			e = edgeMap.get(tokey);
			if (e != null) {
				e.setDirected(false); // bidirectional
				return;
			}
		}
		// make a new edge
		// logger.trace("Making edge with key " + fromkey); // MFM commented out
		// trace
		e = new GenericEdge(src, target, degree, wt, true);
		e.setAmount(value);
		for (IdProperty p : edgeAttrs)
			e.addData(p.getIdName(), p.getIdValue());

		edgeMap.put(fromkey, e);

		// edge exists. Make in non-directed if reverse of this one
	}

	
	
	@Override
	public GenericGraph makeGraphResponse(String type, String[] values,
			int maxDegree, String nodeType) throws Exception {

		nodeFactory = new NodeFactory();
		nodeList = new NodeList();
		edgeMap = new HashMap<String, GenericEdge>();

		if (maxDegree <= 0) // guard against infinite recursion
			return new GenericGraph();
		TimeReporter t = new TimeReporter("Calculate Graph", logger);

		nodeFactory.reset();

		Set<DirectableObject> rowList = new HashSet<DirectableObject>();

		rowList.addAll(loader.pairQuery(originalQuery));

		makeNodes(rowList, 1);
		for (GenericNode n : nodeList.getAllNodes()) {
			for (String s : values)
				if (n.getValue().equals(s))
					n.setOrigin(true);
		}

		// Traversals

		int degree = 2;
		int count = 0;
		for (degree = 2; degree < maxDegree; ++degree) {
			rowList = traverse(degree);
			count = makeNodes(rowList, degree);
			logger.debug("After traverse for degree " + degree
					+ " Node count is " + count);
			if (count > maxNodes) {
				logger.debug("Too many nodes: will truncate");
				// truncate(degree);
				break;
			}
		}

		// temp fix for too many nodes, until truncate is working
		if (count > maxNodes) {
			logger.debug("About to recurse");
			return makeGraphResponse(type, values, maxDegree - 1, nodeType); // recurse
		}
		// end temp fix
		// We have either reached the maxdegrees or maxnodes

		t.logAsCompleted();

		List<GenericNode> nodes = new ArrayList<GenericNode>();
		for (GenericNode n : nodeList.getAllNodes()) {
			// TODO: color should depend on the implementation
			String color = NodeColors.getNodeColor(true, n.isOrigin(),
					n.isLeaf(), nodeType, 0);
			n.setBackgroundColor(color);
			nodes.add(n);
		}

		List<GenericEdge> edges = new ArrayList<GenericEdge>();
		edges.addAll(edgeMap.values());
		return new GenericGraph(nodes, edges);

		// logger.debug(nodeList.toString());
		// for (GREdge e:edgeMap.values())
		// logger.debug(e.toString());

	}

	/**
	 * Make nodes and edges for all items in the set of rows from the data table
	 * if not already present
	 * 
	 * @param rowList
	 *            Set<DirectableObject>
	 * @param degree
	 *            int current degree of the graph
	 * @return int the number of nodes currently in the graph
	 */
	private int makeNodes(Set<DirectableObject> rowList, int degree) {
		DirectedNode senderNode = null, receiverNode = null;
		List<IdProperty> attrs;

		logger.debug("Scanning " + rowList.size() + " DB Rows");

		for (DirectableObject e : rowList) {
			String sender = e.getSrc();
			String receiver = e.getDest();
			int wt = e.getWeight(); // MFM added
			String val = e.getValue();

			// MFM DEBUG
			// logger.debug("makeNodes: sender = " + sender + ", receiver = "
			// + receiver + ", weight =" + wt);

			senderNode = (DirectedNode) nodeList.getNode(sender);
			if (senderNode == null) {
				senderNode = nodeFactory.makeDirectedNode(sender, degree);
				attrs = e.getSrcAttributes();
				for (IdProperty idp : attrs) {
					String idName = idp.getIdName();
					String idValue = idp.getIdValue();
					if (idValue != null && idValue.length() > 0) {
						senderNode.addData(idName, idValue);
					}
				}
				nodeList.addNode(senderNode);
			}

			receiverNode = (DirectedNode) nodeList.getNode(receiver);
			if (receiverNode == null) {
				receiverNode = nodeFactory.makeDirectedNode(receiver, degree);
				nodeList.addNode(receiverNode);
				attrs = e.getDestAttributes();
				for (IdProperty idp : attrs) {
					String idName = idp.getIdName();
					String idValue = idp.getIdValue();
					if (idValue != null && idValue.length() > 0) {
						receiverNode.addData(idName, idValue);
					}
				}
			}

			makeEdge(senderNode, receiverNode, degree, wt, val,
					e.getEdgeAttributes());

		} // Each row

		return countNodes();

	}

	// ---------------------------------------------------------------------
	/**
	 * Call once we are done creating the nodes and edges, to get the final
	 * version
	 * 
	 * @return GraphResponse
	 */
	/*
	 * private GraphResponse makeResponse() { GraphResponse r = new
	 * GraphResponse(); GraphmlContainer gm = r.getGraphml(); setNodeKeys(gm);
	 * GraphmlGraph g = gm.graph; g.setNodeType(GQT_Style ? "Text" : "Icon");
	 * g.getNodes().addAll(nodeList.getAllNodes());
	 * g.getEdges().addAll(edgeMap.values());
	 * 
	 * for (GenericNode node : g.getNodes()) ((DirectedNode)
	 * node).setColors(GQT_Style); return r; }
	 */

	
	
	@Override
	public void setBiPartite(boolean biPartite) {
		logger.trace("Setting biPartite to " + biPartite);
		this.biPartite = biPartite;
	}

	public void setEdgeMap(Map<String, GenericEdge> edgeMap) {
		this.edgeMap = edgeMap;
	}

	
	@Override
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public void setGQT_Style(boolean gQT_Style) {
		GQT_Style = gQT_Style;
	}


	@Override
	public void setLoader(DirectableObjectLoader loader) {
		this.loader = loader;
	}

	@Override
	public void setMaxEdgesPerNode(int maxEdgesPerNode) {
		this.maxEdgesPerNode = maxEdgesPerNode;
	}


	@Override
	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	@Override
	public void setMinWeight(int weight) {
		this.minWeight = weight;
	}

	public void setNodeFactory(NodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
	}

	public void setNodeList(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	public void setOriginalQuery(BasicQuery originalQuery) {
		this.originalQuery = originalQuery;
	}

	
	@Override
	public void setQuery(BasicQuery q) {
		this.originalQuery = q;
	}


	@Override
	public void setShowLeafNodes(boolean showLeafNodes) {
		this.showLeafNodes = showLeafNodes;
	}

	
	@Override
	public void setShowNameNodes(boolean showNameNodes) {
		this.showNameNodes = showNameNodes;
	}

	
	@Override
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}


	@Override
	public void setStyle(boolean style) {
		this.GQT_Style = style;
	}

	/**
	 * Look through our current nodes for any that have not been scanned
	 * 
	 * @param degree
	 *            int
	 * @throws Exception
	 */
	private Set<DirectableObject> traverse(int degree) throws Exception {

		Set<DirectableObject> rowSet = new HashSet<DirectableObject>();
		Set<String> itemsToScan;

		itemsToScan = nodeList.getUnscannedValues('N', degree);

		// logger.trace("Traversing. There are " + nodeList.countNodes() +
		// " nodes so far of which " + itemsToScan.size() + " are unscanned");
		// MFM commented out

		if (itemsToScan.size() > 0) {
			// TODO: optimize by searching using "in"
			for (String v : itemsToScan) {

				if (v != null && v.length() > 0) {

					List<? extends DirectableObject> destns = loader
							.getDestFor(v, originalQuery);
					List<? extends DirectableObject> sources = loader
							.getSrcFor(v, originalQuery);

					if (destns.size() + sources.size() <= maxEdgesPerNode) {
						rowSet.addAll(destns);
						rowSet.addAll(sources);
					} else {
						List<? extends DirectableObject> tempSet;
						if (destns.size() > 0)
							tempSet = destns;
						else { // placeholder
							tempSet = sources;
							Iterator<? extends DirectableObject> it = tempSet
									.iterator();
							//XXX:What is going on here?  You are iterating but not using the variable r.
							DirectableObject r = it.next();
							DirectedNode idNode = nodeFactory.makeDirectedNode(
									v, degree);
							idNode.setPlaceholder(true);
							nodeList.addNode(idNode);
						}
					}
				} // END MFM TEST
			}
			nodeList.setAllScanned('N');
		}
		logger.debug("After traverse rowSet size is " + rowSet.size()
				+ " and nodelist size is " + nodeList.countNodes());

		return rowSet;
	}
}
