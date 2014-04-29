package graphene.dao.solr;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.slf4j.Logger;

public class DAOSolrModule {
	public SolrService buildSolarService(final Logger log) {
		SolrService s = new SolrService("", log);
		return s;
	}

	public static void bind(ServiceBinder binder) {

	}
}
