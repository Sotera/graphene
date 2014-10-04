package graphene.dao;

import java.util.List;

public interface DocumentGraphIngest {

	public boolean parse(Object obj);

	public List<String> getSupportedObjects();

}