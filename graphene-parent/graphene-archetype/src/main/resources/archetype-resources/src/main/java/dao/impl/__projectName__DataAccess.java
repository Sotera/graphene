package ${package}.dao.impl;

import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_DateRange;
import graphene.model.idl.G_DirectionFilter;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntitySearch;
import graphene.model.idl.G_LevelOfDetail;
import graphene.model.idl.G_Link;
import graphene.model.idl.G_LinkEntityTypeFilter;
import graphene.model.idl.G_LinkTag;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SortBy;
import graphene.model.idl.G_TransactionResults;
import graphene.model.idlhelper.SingletonRangeHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class InstagramDataAccess implements G_DataAccess {
	protected final G_EntitySearch _search;
	@Inject
	private Logger logger;

	@Inject
	public InstagramDataAccess(G_EntitySearch s) {
		_search = s;
	}

	@Override
	public List<G_Entity> getEntities(List<String> entities,
			G_LevelOfDetail levelOfDetail) throws AvroRemoteException {

		List<G_Entity> results = new ArrayList<G_Entity>();

		List<G_PropertyMatchDescriptor> idList = new ArrayList<G_PropertyMatchDescriptor>();

		int maxFetch = 100;
		int qCount = 0; // How many entities to query at once
		Iterator<String> idIter = entities.iterator();
		while (idIter.hasNext()) {
			String entity = idIter.next();

			G_PropertyMatchDescriptor idMatch = G_PropertyMatchDescriptor
					.newBuilder()
					.setKey("uid")
					.setRange(
							new SingletonRangeHelper(entity,
									G_PropertyType.STRING))
					.setConstraint(G_Constraint.REQUIRED_EQUALS).build();
			idList.add(idMatch);
			qCount++;

			if (qCount == (maxFetch - 1) || !idIter.hasNext()) {
				G_SearchResults searchResult = _search.search(null, idList, 0,
						100, null);
				if (searchResult != null) {
					logger.debug("Searched for " + qCount + " ids, found "
							+ searchResult.getTotal());

					for (G_SearchResult r : searchResult.getResults()) {
						G_Entity fle = (G_Entity) r.getResult();
						results.add(fle);
					}
				} else {
					logger.warn("Null search results!");
				}

				qCount = 0;
				idList.clear();
			}
		}

		return results;
	}

	@Override
	public Map<String, List<G_Entity>> getAccounts(List<String> entities)
			throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<G_Link>> getFlowAggregation(List<String> entities,
			List<String> focusEntities, G_DirectionFilter direction,
			G_LinkEntityTypeFilter entityType, G_LinkTag tag, G_DateRange date)
			throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<G_Link>> getTimeSeriesAggregation(
			List<String> entities, List<String> focusEntities, G_LinkTag tag,
			G_DateRange date) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_TransactionResults getAllTransactions(List<String> entities,
			G_LinkTag tag, G_DateRange date, G_SortBy sort,
			List<String> linkFilter, long start, long max)
			throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
