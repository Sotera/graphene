package graphene.services;

import graphene.model.idl.G_EntityQuery;

import java.util.Comparator;

public class ScoreComparator implements Comparator<G_EntityQuery> {

	@Override
	public int compare(final G_EntityQuery x, final G_EntityQuery y) {
		if (x.getMinimumScore() < y.getMinimumScore()) {
			return -1;
		}
		if (x.getMinimumScore() > y.getMinimumScore()) {
			return 1;
		}
		return 0;
	}

}
