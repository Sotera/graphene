package graphene.model.graph;

import graphene.model.view.entities.IdProperty;

import java.util.List;

/**
 * Implemented by entitybeans that can be used to generate directed graphs. 
 * 
 * XXX: Will be replaced by G_Link
 * 
 * @author PWG for DARPA
 * 
 */
public interface DirectableObject {

	String getSrc();

	String getDest();

	String getValue();

	int getWeight();

	String getId(); // for drill down

	List<IdProperty> getEdgeAttributes(); // for mouse over etc

	List<IdProperty> getSrcAttributes(); // for mouse over etc

	List<IdProperty> getDestAttributes(); // for mouse over etc

}
