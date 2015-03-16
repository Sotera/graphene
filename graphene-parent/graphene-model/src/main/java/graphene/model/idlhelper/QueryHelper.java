package graphene.model.idlhelper;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class QueryHelper extends G_EntityQuery {
	public QueryHelper(final G_PropertyMatchDescriptor... pmd) {
		setAttributeList(null);
		setCaseSensitive(false);
		final List<String> filters = new ArrayList<String>();
		setFilters(filters);
		setFirstResult(0l);
		setInitiatorId("");
		setMaxResult((long) 1);
		setMinimumScore(0.0d);
		setSearchFreeText(true);
		setTargetSchema("");
		setTimeInitiated(DateTime.now().getMillis());
		setUserId("Unknown");
		setUsername("Unknown");
		final List<G_PropertyMatchDescriptor> list = new ArrayList<G_PropertyMatchDescriptor>();

		for (final G_PropertyMatchDescriptor pm : pmd) {
			list.add(pm);
		}
		setPropertyMatchDescriptors(list);
	}

	// TODO: Stop using search tuples and start using property match descriptors
	public QueryHelper(final List<G_SearchTuple> tuples) {
		setAttributeList(tuples);
		setCaseSensitive(false);
		final List<String> filters = new ArrayList<String>();
		setFilters(filters);
		setFirstResult(0l);
		setInitiatorId("");
		setMaxResult((long) 1);
		setMinimumScore(0.0d);
		setSearchFreeText(true);
		setTargetSchema("");
		setTimeInitiated(DateTime.now().getMillis());
		setUserId("Unknown");
		setUsername("Unknown");
		final List<G_PropertyMatchDescriptor> list = new ArrayList<G_PropertyMatchDescriptor>();
		setPropertyMatchDescriptors(list);
	}

	// TODO: Stop using search tuples and start using property match descriptors
	public QueryHelper(final String value, final G_SearchType g_SearchType, final List<String> filters,
			final int maxResults, final String schema) {
		final List<G_SearchTuple> tuples = new ArrayList<G_SearchTuple>();
		final G_SearchTuple tuple = new G_SearchTuple<String>(value, g_SearchType);
		tuples.add(tuple);
		setAttributeList(tuples);
		setCaseSensitive(false);
		setFilters(filters);
		setFirstResult(0l);
		setInitiatorId("");
		setMaxResult((long) maxResults);
		setMinimumScore(0.0d);
		setSearchFreeText(true);
		setTargetSchema(schema);
		setTimeInitiated(DateTime.now().getMillis());
		setUserId("Unknown");
		setUsername("Unknown");
		final List<G_PropertyMatchDescriptor> list = new ArrayList<G_PropertyMatchDescriptor>();
		setPropertyMatchDescriptors(list);
	}
}
