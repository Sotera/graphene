package graphene.dao;

import graphene.model.idl.G_Constraint;

import java.util.Map;

public interface GraphTraversalRuleService {

	public abstract void addRule(final String nodeType, final G_Constraint queryType);

	public abstract G_Constraint getRule(final String nodeType);

	public abstract Map<String, G_Constraint> getTypeQueryMap();

	public abstract void setTypeQueryMap(final Map<String, G_Constraint> typeQueryMap);

}
