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
	private CombinedDAO dao;

	private String partialName = null;
	private String schema = null;
	private List<Object> preparedResults;

	private G_SearchType searchType = G_SearchType.COMPARE_CONTAINS;

	private int startIndex;

	public CombinedEntityDataSource(CombinedDAO dao2) {
		this.dao = dao2;
		// this.partialName = partialName;
	}

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	public int getAvailableRows() {
		if (partialName == null) {
			return 0;
		}
		EntityQuery q = new EntityQuery();
		q.addAttribute(new G_SearchTuple(partialName,
				G_SearchType.COMPARE_EQUALS));
		try {
			return (int) dao.count(q);
		} catch (Exception e) {
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
			AdvancedSearch srch = new AdvancedSearch();
			List<SearchFilter> filters = new ArrayList<SearchFilter>();
			SearchFilter sf = new SearchFilter();
			sf.setValue(partialName);
			filters.add(sf);
			srch.setFilters(filters);

			EntityQuery q = new EntityQuery();
			q.addAttribute(new G_SearchTuple(partialName,
					G_SearchType.COMPARE_EQUALS));
			try {
				preparedResults = dao.findByQuery(q);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			List<SortConstraint> sortConstraints) {
		List<G_SortCriterion> sortCriteria = new ArrayList<G_SortCriterion>();

		for (SortConstraint sortConstraint : sortConstraints) {

			String propertyName = sortConstraint.getPropertyModel()
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

			G_SortCriterion sortCriterion = new G_SortCriterion(sortDirection,
					propertyName);
			sortCriteria.add(sortCriterion);
		}

		return sortCriteria;
	}
}
