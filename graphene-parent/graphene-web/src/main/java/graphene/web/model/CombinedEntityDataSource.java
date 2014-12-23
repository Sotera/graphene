package graphene.web.model;

import graphene.dao.CombinedDAO;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SortCriterion;
import graphene.model.idl.G_SortOrder;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EntityQuery;
import graphene.model.query.SearchFilter;
import graphene.model.view.entities.Entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

public class CombinedEntityDataSource implements GridDataSource {
	private final CombinedDAO dao;

	private final String partialName = null;
	private final String schema = null;
	private List<Object> preparedResults;

	private final G_SearchType searchType = G_SearchType.COMPARE_CONTAINS;

	private int startIndex;

	public CombinedEntityDataSource(final CombinedDAO dao2) {
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
		final EntityQuery q = new EntityQuery();
		q.addAttribute(new G_SearchTuple(partialName,
				G_SearchType.COMPARE_EQUALS));
		try {
			return (int) dao.count(q);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void prepare(final int startIndex, final int endIndex,
			final List<SortConstraint> sortConstraints) {
		if (partialName == null) {
			preparedResults = new ArrayList<Object>();
		} else {
			final AdvancedSearch srch = new AdvancedSearch();
			final List<SearchFilter> filters = new ArrayList<SearchFilter>();
			final SearchFilter sf = new SearchFilter();
			sf.setValue(partialName);
			filters.add(sf);
			srch.setFilters(filters);

			final EntityQuery q = new EntityQuery();
			q.addAttribute(new G_SearchTuple(partialName,
					G_SearchType.COMPARE_EQUALS));
			try {
				preparedResults = dao.findByQuery(q);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		if (preparedResults == null) {
			preparedResults = new ArrayList<Object>();
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
	private List<G_SortCriterion> toSortCriteria(
			final List<SortConstraint> sortConstraints) {
		final List<G_SortCriterion> sortCriteria = new ArrayList<G_SortCriterion>();

		for (final SortConstraint sortConstraint : sortConstraints) {

			final String propertyName = sortConstraint.getPropertyModel()
					.getPropertyName();
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

			final G_SortCriterion sortCriterion = new G_SortCriterion(
					sortDirection, propertyName);
			sortCriteria.add(sortCriterion);
		}

		return sortCriteria;
	}
}
