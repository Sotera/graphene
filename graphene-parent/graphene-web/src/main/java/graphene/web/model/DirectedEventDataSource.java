package graphene.web.model;

import graphene.dao.TransactionDAO;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SortCriterion;
import graphene.model.idl.G_SortOrder;
import graphene.model.query.EventQuery;
import graphene.model.view.events.DirectedEventRow;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

public class DirectedEventDataSource implements GridDataSource {
	private TransactionDAO dao;

	private String partialName = null;

	private List<DirectedEventRow> preparedResults;

	private G_SearchType searchType = G_SearchType.COMPARE_CONTAINS;

	private int startIndex;

	public DirectedEventDataSource(TransactionDAO dao) {
		this.dao = dao;
		// this.partialName = partialName;
	}

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	public int getAvailableRows() {
		if (partialName == null) {
			return 0;
		}
		EventQuery q = new EventQuery();
		q.addId(partialName);
		try {
			return (int) dao.count(q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Class<DirectedEventRow> getRowType() {
		return DirectedEventRow.class;
	}

	@Override
	public Object getRowValue(final int index) {
		return preparedResults.get(index - startIndex);
	}

	@Override
	public void prepare(final int startIndex, final int endIndex,
			final List<SortConstraint> sortConstraints) {
		if (partialName == null) {
			preparedResults = new ArrayList<DirectedEventRow>();
		} else {
			EventQuery q = new EventQuery();
			q.setFirstResult(startIndex);
			q.setMaxResult(endIndex - startIndex + 1);
			q.addId(partialName);
			// q.addAttribute(
			// new G_SearchTuple<String>(partialName, searchType));
			try {
				// FIXME: Need to set limit and offset in query object
				preparedResults = dao.findByQuery(q);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (preparedResults == null) {
			preparedResults = new ArrayList<DirectedEventRow>();
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
