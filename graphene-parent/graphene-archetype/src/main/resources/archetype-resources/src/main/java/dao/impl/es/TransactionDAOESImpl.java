package ${package}.dao.impl.es;

import graphene.dao.TransactionDAO;
import graphene.model.query.BasicQuery;
import graphene.model.view.events.DirectedEventRow;
import graphene.util.G_CallBack;

import java.util.List;

public class TransactionDAOESImpl implements TransactionDAO<Object, BasicQuery> {

	@Override
	public List<Object> findByQuery(BasicQuery pq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getAll(long offset, long maxResults) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count(BasicQuery q) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReady(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getReadiness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean performCallback(long offset, long maxResults,
			G_CallBack<Object,BasicQuery> cb, BasicQuery q) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long countEdges(String id) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DirectedEventRow> getEvents(BasicQuery q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectedEventRow findEventById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
