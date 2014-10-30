package graphene.dao.es;

import io.searchbox.client.JestClient;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphene.dao.LoggingDAO;

public class LoggingDAOESImpl implements LoggingDAO {
	private JestClient jestClient;
	private ESRestAPIConnection c;
	private String auth;
	private ObjectMapper mapper;
	@Inject
	public LoggingDAOESImpl(ESRestAPIConnection c, JestClient jestClient) {
		this.auth = null;
		this.c = c;
		this.jestClient = jestClient;
		this.mapper = new ObjectMapper(); // can reuse, share globally
	}
	@Override
	public boolean recordQuery(String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordLogin(String userName, DateTime date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordExport(String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

}
