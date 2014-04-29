package graphene.model.graph.entity;

import graphene.model.graph.GenericNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeList {

	private Map<String, GenericNode> nodeMap = new HashMap<String, GenericNode>();
	static Logger logger = LoggerFactory.getLogger(NodeList.class);
	
	public boolean addNode(GenericNode node)
	{
		if (nodeMap.containsKey(node.getKey()))
			return false;
/*
		if (nbrUsed(node)) { // Can remove after testing
			logger.error("duplicate id for node " + node);			
		}
*/		
		nodeMap.put(node.getKey(), node);
		return true;
	}
/*	
	private boolean nbrUsed(GRNode node)
	{
		for (GRNode n:nodeMap.values()) { 
			if (n.getId().equals(node.getId())) {
				return true;
			}
		}
		return false;
	}
*/	
	public Collection<GenericNode> getAllNodes()
	{
		return nodeMap.values();
	}
	
	public GenericNode getNode(char entityType, String value)
	{
		return nodeMap.get(entityType + ":" + value);
	}
	
	public GenericNode getNode(String key)
	{
		return nodeMap.get(key);
	}
	
	public Set<String> getUnscannedValues(char type, int degree)
	{
		Set<String> results = new HashSet<String>();
		for (GenericNode n:nodeMap.values()) {
			if (n.getEntityType() != type)
				continue;
			if (n.isTraversed())
				continue;
			if (n.isPlaceholder())
				continue;
			if (degree != 0 && n.getDegree() >= degree)
				continue;
			results.add(n.getValue());
		}
		return results;
	}
	
	public void setAllScanned(char type)
	{
		for (GenericNode n:nodeMap.values()) {
			if (n.getEntityType() == type) 
					n.setTraversed(true);
		}
		
	}
	
	public int countNodes()
	{
		return nodeMap.size();
	}

	public boolean hasNode(EntityRefNode node)
	{
		return nodeMap.containsKey(node.getKey());
	}
	public void removeUnused()
	{
		logger.debug("Removing unused nodes. Starting count: " + nodeMap.size());
		Map<String, GenericNode> map = new HashMap<String, GenericNode>();
		for (GenericNode n:nodeMap.values()) {
			if (map.containsKey(n.getKey()))
				continue;
			if ((n.getEntityType() != EntityRefNode.ENTITY_IDENTIFIER) || n.isUsed())
				map.put(n.getKey(), n);
		}
		nodeMap = map;
		logger.debug("Removed unused nodes. Ending count: " + nodeMap.size());		
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (GenericNode n:nodeMap.values()) {
			s.append(n + "\n");
		}
		return s.toString();
	}
	

}
