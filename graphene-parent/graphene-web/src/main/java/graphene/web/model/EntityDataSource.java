package graphene.web.model;

import graphene.dao.EntityDAO;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SortCriterion;
import graphene.model.idl.G_SortOrder;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.query.SearchFilter;
import graphene.model.view.entities.Entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class EntityDataSource implements GridDataSource {
	private final EntityDAO dao;
	@Inject
	private Logger logger;
	private final String partialName = null;

	private List<G_Entity> preparedResults;

	private final G_SearchType searchType = G_SearchType.COMPARE_CONTAINS;

	private int startIndex;

	public EntityDataSource(final EntityDAO dao2) {
		dao = dao2;
		// this.partialName = partialName;
	}

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	@Override
	public int getAvailableRows() {
		if (partialName == null) {
			return 0;
		}
		final EventQuery q = new EventQuery();
		q.addId(partialName);
		try {
			return (int) dao.count(q);
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

	@Override
	public Class<Entity> getRowType() {
		return Entity.class;
	}

	@Override
	public Object getRowValue(final int index) {
		return preparedResults.get(index - startIndex);
	}

	@Override
	public void prepare(final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {
		if (partialName == null) {
			preparedResults = new ArrayList<G_Entity>();
		} else {
			final AdvancedSearch srch = new AdvancedSearch();
			final List<SearchFilter> filters = new ArrayList<SearchFilter>();
			final SearchFilter sf = new SearchFilter();
			sf.setValue(partialName);
			filters.add(sf);
			srch.setFilters(filters);
			// EventQuery q = new EventQuery();
			// q.setFirstResult(startIndex);
			// q.setMaxResult(endIndex - startIndex + 1);
			// q.addId(partialName);
			// q.addAttribute(
			// new G_SearchTuple<String>(partialName, searchType));
			try {
				preparedResults = dao.getEntitiesByAdvancedSearch(srch);
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		}
		if (preparedResults == null) {
			preparedResults = new ArrayList<G_Entity>();
		}
		// (partialName, startIndex, endIndex - startIndex + 1);
		this.startIndex = startIndex;
	}

	// this is optional
	/**
	 * Converts a list of Tapestry's SortConstraint to a list of our business
	 * tier's SortCriterion. The business tier does not use SortConstraint
	 * because that would create a dependency on Tapestry.
	 */
	private List<G_SortCriterion> toSortCriteria(final List<SortConstraint> sortConstraints) {
		final List<G_SortCriterion> sortCriteria = new ArrayList<G_SortCriterion>();

		for (final SortConstraint sortConstraint : sortConstraints) {

			final String propertyName = sortConstraint.getPropertyModel().getPropertyName();
			G_SortOrder sortDirection = G_SortOrder.UNSORTED;

			switch (sortConstraint.getColumnSort()) {
			case ASCENDING:
				sortDirection = G_SortOrder.ASC;
				break;
			case DESCENDING:
				sortDirection = G_SortOrder.DESC;
				break;
			default:
			}

			final G_SortCriterion sortCriterion = new G_SortCriterion(sortDirection, propertyName);
			sortCriteria.add(sortCriterion);
		}

		return sortCriteria;
	}
}
