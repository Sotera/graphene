package graphene.dao.sql;

import graphene.model.sql.FINFLOWDaily120;
import graphene.model.sql.QFINFLOWDaily120;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.jolbox.bonecp.BoneCP;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLServer2005Templates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.EntityPath;

public class FinFlowDaily100DAOSQLImpl {

	@Inject
	private BoneCP cp;

	protected Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return cp.getConnection();
	}

	protected SQLQuery from(Connection conn, EntityPath<?>... o)
			throws SQLException {
		SQLTemplates dialect = new SQLServer2005Templates(); // SQL-dialect
		return new SQLQuery(conn, dialect).from(o);
	}

	public List<FINFLOWDaily120> getFlowsFrom(String entity) {
		QFINFLOWDaily120 q = new QFINFLOWDaily120("q");
		List<FINFLOWDaily120> list = new ArrayList<FINFLOWDaily120>();
		try {
			list = from(getConnection(), q).where(q.fromEntityId.eq(entity))
					.list(q);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<FINFLOWDaily120> getFlowsTo(String entity) {
		QFINFLOWDaily120 q = new QFINFLOWDaily120("q");
		List<FINFLOWDaily120> list = new ArrayList<FINFLOWDaily120>();
		try {
			list = from(getConnection(), q).where(q.toEntityId.eq(entity))
					.list(q);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<FINFLOWDaily120> getFlowsBoth(String entity) {
		QFINFLOWDaily120 q = new QFINFLOWDaily120("q");
		List<FINFLOWDaily120> list = new ArrayList<FINFLOWDaily120>();
		try {
			list = from(getConnection(), q).where(
					q.fromEntityId.eq(entity).or(q.toEntityId.eq(entity)))
					.list(q);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<FINFLOWDaily120> getFlowsFromTo(String from, String to) {
		QFINFLOWDaily120 q = new QFINFLOWDaily120("q");
		List<FINFLOWDaily120> list = new ArrayList<FINFLOWDaily120>();
		try {
			list = from(getConnection(), q).where(
					q.fromEntityId.eq(from).and(q.toEntityId.eq(to))).list(q);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<FINFLOWDaily120> getFlowsBetween(String... entities) {
		QFINFLOWDaily120 q = new QFINFLOWDaily120("q");
		List<FINFLOWDaily120> list = new ArrayList<FINFLOWDaily120>();
		try {
			list = from(getConnection(), q).where(
					q.fromEntityId.in(entities).and(q.toEntityId.in(entities)))
					.list(q);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
