package graphene.services;

import graphene.dao.EntityDAO;
import graphene.dao.EntityRefDAO;
import graphene.dao.IdTypeDAO;
import graphene.dao.annotations.EntityLightFunnelMarker;
import graphene.model.Funnel;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_Uncertainty;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.view.entities.BasicEntityRef;
import graphene.model.view.entities.EntityLight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityDAOImpl implements EntityDAO {
	private static Logger logger = LoggerFactory.getLogger(EntityDAOImpl.class);

	private EntityRefDAO dao;

	@Inject
	private IdTypeDAO idTypeDAO;

	private Funnel<EntityLight, G_Entity> funnel;

	@Inject
	public EntityDAOImpl(EntityRefDAO dao, @Inject @EntityLightFunnelMarker Funnel f) {
		this.dao = dao;
		this.funnel = f;// new EntityLightFunnel();
	}

	@Override
	public EntityLight getById(String id) {
		G_Entity e = new G_Entity(id, null, null, null, null);
		updateAllFields(e);
		return funnel.to(e);
	}

	@Override
	public List<G_Entity> getEntitiesByAdvancedSearch(AdvancedSearch srch) {
		// This is tricky because we can't iterate through entities or search
		// for an entity by multiple fields. Each attribute/entity pair
		// is a row in the database.
		// One solution is to create a set of matches for each criterion
		// and return the intersection of all the sets.
		ArrayList<G_Entity> results = new ArrayList<G_Entity>();

		Set<String> matches = dao.entityIDsByAdvancedSearch(srch);
		if (matches != null) {
			for (String s : matches) {
				List<G_EntityTag> tagList = new ArrayList<G_EntityTag>(1);
				tagList.add(G_EntityTag.FILE);
				G_Provenance prov = new G_Provenance(srch.getSource());
				G_Uncertainty uncertainty = new G_Uncertainty(1.0d);
				G_Property property = new PropertyHelper(G_PropertyTag.LABEL, s);
				G_Entity e = new EntityHelper(s, tagList, prov, uncertainty,
						Collections.singletonList(property));// srch.getSource(),
																// s);
				e.setUid(s);
				// if (e.getProperties() == null) {
				updateAllFields(e);
				// }
				results.add(e);
			}
		}
		return results;
	}

	public void updateAllFields(G_Entity e) {
		// if (e.isFullyLoaded()) {
		// return;
		// }

		// e.setNameList(new HashSet<G_Property>());
		// e.setCommunicationIds(new HashSet<G_Property>());
		// e.setAddressList(new HashSet<G_Property>());
		// e.setEmailList(new HashSet<G_Property>());
		// e.setIdentList(new HashSet<G_Property>());
		// e.setAccountList(new HashSet<G_Property>());
		List<G_Property> list = new ArrayList<G_Property>();
		Set<BasicEntityRef> rows;
		try {
			rows = dao.getBasicRowsForCustomer(e.getUid());

			for (BasicEntityRef r : rows) {
				String val = r.getIdentifier();
				G_Provenance prov = new G_Provenance(
						r.getIdentifierTableSource());
				// if (r.getAccountNumber() != null) {
				if (list.size() == 0) {
					// we only want to add this kind of property ONCE
					list.add(new PropertyHelper("account", val,
							G_PropertyTag.ID));
				}
				String family = idTypeDAO.getNodeType(r.getIdtypeId());
				G_Property ad = null;
				if (family.equals("name")) {
					ad = new PropertyHelper(family, val, G_PropertyTag.NAME);

				} else if (family.equals("address")) {
					ad = new PropertyHelper(family, val, G_PropertyTag.GEO);
				} else if (family.equals("communicationId")) {
					ad = new PropertyHelper(family, val, G_PropertyTag.ID);
				} else if (family.equals("email")) {
					ad = new PropertyHelper(family, val, G_PropertyTag.ID);
				} else {
					ad = new PropertyHelper(family, val, G_PropertyTag.ID);
				}

				if (ad != null) {
					ad.setProvenance(prov);
					list.add(ad);
				}
			}
			e.getProperties().addAll(list);
			// e.setFullyLoaded(true);
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

	@Override
	public List<EntityLight> getLightEntitiesByAdvancedSearch(
			AdvancedSearch search) {
		List<EntityLight> list = new ArrayList<EntityLight>();
		for (G_Entity g : getEntitiesByAdvancedSearch(search)) {
			list.add(funnel.to(g));
		}
		return list;
	}
}
