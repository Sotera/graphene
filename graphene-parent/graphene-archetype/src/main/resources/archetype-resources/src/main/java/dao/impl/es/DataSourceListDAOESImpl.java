package ${package}.dao.impl.es;

import graphene.dao.DataSourceListDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.datasourcedescriptors.DataSource;
import graphene.model.datasourcedescriptors.DataSourceList;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.mapping.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A DAO to return a list of available Data Sets. In some environments this is
 * done via a server call, so it should be an injected implementation.
 * 
 * 
 */
public class DataSourceListDAOESImpl extends BasicESDAO implements DataSourceListDAO {

	private static DataSourceList dataSourceList = null;

	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String indexName;

	public DataSourceListDAOESImpl() {
		// TODO Auto-generated constructor stub
	}

	public DataSourceListDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;

	}

	@Override
	public List<String> getAvailableTypes() {
		return Arrays.asList("media");
	}

	public List<String> getAvailableTypes(final String theIndex) {
		final List<String> types = new ArrayList<String>(10);
		// types.add(ALL_REPORTS);
		try {
			final JestClient client = c.getClient();
			final io.searchbox.indices.mapping.GetMapping.Builder g = new GetMapping.Builder();
			g.addIndex(theIndex);
			// g.addType(typeName);
			final GetMapping getMapping = g.build();

			JestResult jestResult;

			jestResult = client.execute(getMapping);

			final JsonNode readValue = mapper.readValue(jestResult.getJsonString(), JsonNode.class);
			final JsonNode mappings = readValue.findPath("mappings");
			// logger.debug("mappings: " + mappings.toString());

			String currentType = null;
			final Iterator<String> iter = mappings.fieldNames();
			while (iter.hasNext()) {
				currentType = iter.next();
				if (currentType != null) {
					types.add(currentType);
				}
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// logger.debug(types.toString());
		return types;
	}

	@Override
	public String getDefaultSchema() {
		return indexName;
	}

	/**
	 * Return a list of datasets for use by the rest service. These lists are
	 * used by the gui to allow users to choose a list, and to configure the
	 * appropriate screens.
	 * 
	 * @author PWG for DARPA
	 * 
	 */
	@Override
	public DataSourceList getList() {
		if (dataSourceList == null) {
			dataSourceList = loadList();
		}

		return dataSourceList;
	}

	private DataSourceList loadList() {
		final DataSourceList list = new DataSourceList();
		// such datasource
		list.addSource(makeInstagram());
		// add more data sources here if you want. wow.
		return list;
	}

	private DataSource makeInstagram() {
		final DataSource dataSource = new DataSource();
		return dataSource;
	}

}
