package ${package}.dao.impl.es;

import graphene.dao.CombinedDAO;
import graphene.dao.EntityDAO;
import graphene.dao.annotations.EntityLightFunnelMarker;
import graphene.model.funnels.Funnel;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EntityQuery;
import graphene.model.query.EventQuery;
import graphene.model.query.SearchFilter;
import graphene.model.view.entities.EntityLight;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * This version of EntityDAO skips the need for EntityRef sub services, and goes
 * directly against a modeled ES schema.
 * 
 * This class ends up feeding the entity search screen of the ExtJS search
 * results page.
 * 
 * @author djue
 * 
 */
public class EntityDAOESImpl implements EntityDAO {

	@Inject
	private Logger logger;

	private final CombinedDAO dao;

	private final Funnel<EntityLight, G_Entity> entityLightFunnel;

	@Inject
	public EntityDAOESImpl(
			final CombinedDAO dao,
			@Inject @EntityLightFunnelMarker final Funnel<EntityLight, G_Entity> f) {
		this.dao = dao;
		entityLightFunnel = f;
	}

	private void addSafeGProperty(final List<G_Property> list,
			final String idType, final String identifier,
			final G_PropertyTag tag) {
		G_Property ad;
		if ((ad = createSafeGProperty(idType, identifier, tag)) != null) {
			list.add(ad);
		}
	}

	private G_Entity convertFrom(final Object r) {

		return null;
	}

	@Override
	public long count(final EventQuery q) {
		final EntityQuery eq = new EntityQuery();
		eq.addAttribute(q.getAttributeList());
		eq.setSchema(q.getSchema());
		try {
			return dao.count(eq);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private G_Property createSafeGProperty(final String idType,
			final String identifier, final G_PropertyTag tag) {
		if (ValidationUtils.isValid(idType, identifier, tag)) {
			final G_Property g = new PropertyHelper(idType, identifier, tag);
			return g;
		}
		return null;
	}

	@Override
	public EntityLight getById(final String id) {
		final EntityQuery eq = new EntityQuery();
		eq.addAttribute(new G_SearchTuple(id, G_SearchType.COMPARE_EQUALS));
		try {
			final List<Object> matches = dao.findByQuery(eq);
			if (matches.size() > 0) {
				final G_Entity e = convertFrom(matches.get(0));
				return entityLightFunnel.to(e);
			}
		} catch (final Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return entityLightFunnel.to(new G_Entity());
	}

	@Override
	public List<G_Entity> getEntitiesByAdvancedSearch(final AdvancedSearch srch) {
		// This is tricky because we can't iterate through entities or search
		// for an entity by multiple fields. Each attribute/entity pair
		// is a row in the database.
		// One solution is to create a set of matches for each criterion
		// and return the intersection of all the sets.
		final ArrayList<G_Entity> results = new ArrayList<G_Entity>();

		final EntityQuery eq = new EntityQuery();
		// eq.setSchema(srch.getDataSet());
		for (final SearchFilter f : srch.getFilters()) {
			// TODO: Fix this by hooking up the actual search time with the one
			// that will be performed.
			eq.addAttribute(new G_SearchTuple(f.getValue(),
					G_SearchType.COMPARE_CONTAINS));
		}
		eq.setFirstResult(srch.getStart());

		// Get a smaller list because we are going against ES
		eq.setMaxResult(100);
		List<Object> matches;
		try {
			matches = dao.findByQuery(eq);
			if (matches != null) {
				for (final Object s : matches) {
					final G_Entity e = convertFrom(s);
					if (e != null) {
						results.add(e);
					}
				}
			}
		} catch (final Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return results;
	}

	@Override
	public List<EntityLight> getLightEntitiesByAdvancedSearch(
			final AdvancedSearch search) {
		final List<EntityLight> list = new ArrayList<EntityLight>();
		for (final G_Entity g : getEntitiesByAdvancedSearch(search)) {
			final EntityLight converted = entityLightFunnel.to(g);
			if (ValidationUtils.isValid(converted)) {
				list.add(converted);
			}
		}
		return list;
	}

}
