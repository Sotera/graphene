package graphene.services;

import graphene.model.query.EntityQuery;

import java.util.Comparator;

public class ScoreComparator implements Comparator<EntityQuery> {

	@Override
	public int compare(final EntityQuery x, final EntityQuery y) {
		if (x.getMinimumScore() < y.getMinimumScore()) {
			return -1;
		}
		if (x.getMinimumScore() > y.getMinimumScore()) {
			return 1;
		}
		return 0;
	}

}
