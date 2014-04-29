package graphene.dao;

import graphene.model.view.sample.GraphDemoObject;

public interface GraphDemoDAO {

	public abstract GraphDemoObject getHTML();

	public abstract GraphDemoObject getJSON();

	public abstract GraphDemoObject getNode(String id);

	public abstract GraphDemoObject getAllNodes();

}
