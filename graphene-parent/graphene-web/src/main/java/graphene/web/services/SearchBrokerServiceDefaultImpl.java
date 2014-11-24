package graphene.web.services;

import graphene.web.pages.CombinedEntitySearchPage;

public class SearchBrokerServiceDefaultImpl implements SearchBrokerService {

	@Override
	public String getSearchPage(String searchValue) {
		return "graphene/"+CombinedEntitySearchPage.class.getSimpleName();
	}

}
