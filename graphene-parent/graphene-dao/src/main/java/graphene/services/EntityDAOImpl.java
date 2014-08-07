package graphene.services;

import graphene.dao.EntityDAO;
import graphene.dao.EntityRefDAO;
import graphene.dao.IdTypeDAO;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.view.entities.Account;
import graphene.model.view.entities.BasicEntityRef;
import graphene.model.view.entities.CommunicationId;
import graphene.model.view.entities.EmailAddress;
import graphene.model.view.entities.Entity;
import graphene.model.view.entities.Identifier;
import graphene.model.view.entities.Name;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.darpa.vande.generic.V_IdProperty;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityDAOImpl implements EntityDAO {
	private static Logger logger = LoggerFactory.getLogger(EntityDAOImpl.class);

	private EntityRefDAO dao;

	@Inject
	private IdTypeDAO idTypeDAO;

	@Inject
	public EntityDAOImpl(EntityRefDAO dao) {
		this.dao = dao;
	}

	@Override
	public Entity getById(String id) {
		Entity e = new Entity();
		e.setId(id);
		updateAllFields(e);
		return e;
	}

	@Override
	public List<Entity> getEntitiesByAdvancedSearch(AdvancedSearch srch) {
		// This is tricky because we can't iterate through entities or search
		// for an entity by multiple fields. Each attribute/entity pair
		// is a row in the database.
		// One solution is to create a set of matches for each criterion
		// and return the intersection of all the sets.
		ArrayList<Entity> results = new ArrayList<Entity>();

		Set<String> matches = dao.entityIDsByAdvancedSearch(srch);
		if (matches != null) {
			for (String s : matches) {
				Entity e = new Entity(srch.getSource(), s);
				if (e.getAccountList() == null) {
					updateAllFields(e);
				}
				results.add(e);
			}
		}
		return results;
	}

	// @Override
	// public List<Entity> getEntitiesByProperty(G_CanonicalPropertyType
	// property,
	// String value) {
	// List<Entity> results = new ArrayList<Entity>();
	// List<String> entityIds = null;
	//
	// EntityQuery q = new EntityQuery();
	// G_SearchTuple<String> srch = new G_SearchTuple<String>();
	// srch.setSearchType(G_SearchType.COMPARE_EQUALS);
	// srch.setFamily(property);
	// srch.setValue(value);
	// List<G_SearchTuple<String>> attrs = new
	// ArrayList<G_SearchTuple<String>>();
	// attrs.add(srch);
	// q.setAttributeList(attrs);
	//
	// try {
	// entityIds = dao.findByQuery(q);
	// for (String id : entityIds) {
	// results.add(getById(id));
	// }
	// } catch (Exception e) {
	// logger.error("Error on rowSearch with query " + q);
	// e.printStackTrace();
	// return results;
	// }
	//
	// return results;
	// }

	@Override
	public void updateAllFields(Entity e) {
		if (e.isFullyLoaded()) {
			return;
		}

		e.setNameList(new HashSet<Name>());
		e.setCommunicationIdList(new HashSet<CommunicationId>());
		e.setAddressList(new HashSet<Identifier>());
		e.setEmailList(new HashSet<EmailAddress>());
		e.setIdentList(new HashSet<V_IdProperty>());
		e.setAccountList(new HashSet<Account>());

		Set<BasicEntityRef> rows;
		try {
			rows = dao.getBasicRowsForCustomer(e.getId());

			for (BasicEntityRef r : rows) {
				String val = r.getIdentifier();

				String family = idTypeDAO.getFamily(r.getIdtypeId());

				if (family.equals("name")) {
					Name ad = new Name(e.getDatasourceId(), val, val);
					e.addName(ad);
				}

				else if (family.equals("address")) {
					Identifier ad = new Identifier(e.getDatasourceId(), val,
							val);
					e.addAddress(ad);
				} else if (family.equals("communicationId")) {
					CommunicationId communicationId = new CommunicationId(
							e.getDatasourceId(), val, val);
					e.addCommunicationId(communicationId);
				} else if (family.equals("email")) {
					EmailAddress ad = new EmailAddress(e.getDatasourceId(),
							val, val);
					e.addEmailAddress(ad);
				} else {
					e.getIdentList().add(new V_IdProperty(family, val));
				}
				if (r.getAccountNumber() != null) {
					Account ac = new Account(e.getDatasourceId(),
							r.getAccountNumber(), r.getAccountNumber());
					e.addAccount(ac);
				}
			}
			e.setFullyLoaded(true);
		} catch (Exception e1) {
			// TODO FIXME Auto-generated catch block
			e1.printStackTrace();
			return;
		}

	}

	@Override
	public long count(EventQuery q) {
		try {
			return dao.count(q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
