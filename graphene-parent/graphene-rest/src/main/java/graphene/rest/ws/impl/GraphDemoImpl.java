package graphene.rest.ws.impl;

import graphene.dao.GraphDemoDAO;
import graphene.model.view.sample.GraphDemoObject;
import graphene.rest.ws.GraphDemo;

import org.slf4j.Logger;

/**
 * Small sample rest service for demo graphs and other testing. Moved the Neo4J
 * specific portions to the neo4j module.
 * 
 * @author djue
 * 
 */
public class GraphDemoImpl implements GraphDemo {

	private GraphDemoDAO dao;
	private Logger logger;



	public GraphDemoImpl() {
		super();
	}

	public GraphDemoImpl(Logger logger, GraphDemoDAO dao) {
		this.logger = logger;
		this.dao = dao;
	}

	public GraphDemoObject getAllNodes() {
		return dao.getAllNodes();
	}

	public GraphDemoObject getHTML() {
		return dao.getHTML();
	}

	public GraphDemoObject getJSON() {
		return dao.getJSON();
	}

	public GraphDemoObject getNode(String id) {
		return dao.getNode(id);
	}
}
