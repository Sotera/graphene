package graphene.rest.ws.impl;

import graphene.dao.DataSourceListDAO;
import graphene.dao.EntityDAO;
import graphene.model.query.AdvancedSearch;
import graphene.model.view.entities.EntityLight;
import graphene.model.view.entities.EntitySearchResults;
import graphene.rest.ws.EntityServerRS;
import graphene.util.stats.TimeReporter;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EntityServerRSImpl implements EntityServerRS {

	@Inject
	private Logger logger;

	@Inject
	private DataSourceListDAO dataSourceListDAO;
	
	@Inject
	private EntityDAO entitydao;

	@Override
	@GET
	@Path("/advancedSearch")
	@Produces("application/json")
	public EntitySearchResults advancedSearch(
			@QueryParam("jsonSearch") String jsonSearch) {
		TimeReporter t = new TimeReporter("advancedSearch", logger);
		logger.trace("json search: " + jsonSearch);
		ObjectMapper mapper = new ObjectMapper();
		byte[] bytes = jsonSearch.getBytes();
		AdvancedSearch search = null;
		try {
			search = mapper.readValue(bytes, 0, bytes.length,
					AdvancedSearch.class);
		} catch (JsonParseException e) {
			logger.error(e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		EntitySearchResults results = new EntitySearchResults();
		if (search == null) {
			logger.debug("json parse failed");
			results = null;
		} else {
			logger.trace(search.getDataSet());
			search.setFieldsIntoFilters(dataSourceListDAO.getList());
			results.addEntities(entitydao
					.getLightEntitiesByAdvancedSearch(search));
		}
		t.logAsCompleted();
		return results;
	}

	@Override
	@GET
	@Path("/getEntityByID/{ID}")
	@Produces("application/json")
	/**
	 * Returns an entity given the ID. May not be needed, since
	 * all the search responses may have complete entities contained
	 * in them. 
	 */
	public EntityLight getEntityByID(@PathParam("ID") String id) {
		logger.debug("Getting entity for id " + id);
		return entitydao.getById(id);
	}

}
