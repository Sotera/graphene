#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.impl.es;

import graphene.dao.DataSourceListDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_Constraint;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.mapping.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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


	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String defaultIndexName;

	public DataSourceListDAOESImpl() {
	}

	public DataSourceListDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;

	}

	@Override
	public List<G_Constraint> getAvailableConstraints() {
		final List<G_Constraint> list = new ArrayList<G_Constraint>();
		list.add(G_Constraint.CONTAINS);
		list.add(G_Constraint.FUZZY_REQUIRED);
		list.add(G_Constraint.EQUALS);
		list.add(G_Constraint.STARTS_WITH);
		list.add(G_Constraint.NOT);
		return list;
	}

	@Override
	public List<String> getAvailableTypes() {
		return Arrays.asList("media");
	}

	@Override
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
		return defaultIndexName;
	}

	@Override
	public Map<String, ArrayList<String>> getFieldMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<String>> getRangeMappings() {
		return null;
	}

}
