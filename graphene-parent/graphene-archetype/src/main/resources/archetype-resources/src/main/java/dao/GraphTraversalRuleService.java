package ${package}.dao;

import graphene.model.idl.G_SearchType;

import java.util.Map;

public interface GraphTraversalRuleService {

	public abstract void addRule(final String nodeType, final G_SearchType queryType);

	public abstract G_SearchType getRule(final String nodeType);

	public abstract Map<String, G_SearchType> getTypeQueryMap();

	public abstract void setTypeQueryMap(final Map<String, G_SearchType> typeQueryMap);

}
