package graphene.model.funnels;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.view.entities.EntityAttribute;
import graphene.model.view.entities.EntityLight;

import java.util.List;

@Deprecated
public class DefaultEntityLightFunnel implements Funnel<EntityLight, G_Entity> {

	@Override
	public G_Entity from(final EntityLight e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityLight to(final G_Entity e) {
		final EntityLight el = new EntityLight();
		el.setId(e.getUid());
		el.setDatasource_id(e.getProvenance().getUri());
		final StringBuffer sb = new StringBuffer("");
		final List<G_Property> propertiesByTag = EntityHelper.getPropertiesByTag(e, G_PropertyTag.NAME);
		el.setEffectiveName(e.getProvenance() + e.getUid());
		for (final G_Property p : propertiesByTag) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			// get the value of the property
			final String n = (String) PropertyHelper.from(p).getValue();

			el.getAttributes().add(new EntityAttribute("Name", "", n));

			// set the effectiveName to be the shortest one.

			// if (el.getEffectiveName() == null) {
			// el.setEffectiveName(n);
			// } else if (n.length() < el.getEffectiveName().length()) {
			// el.setEffectiveName(n);
			// }
			sb.append(n);
		}
		if (propertiesByTag.size() > 1) {
			el.setEffectiveName(el.getEffectiveName() + " (" + propertiesByTag.size() + " associated values)");
		}
		el.setAllNames(sb.toString());
		for (final G_Property p : EntityHelper.getPropertiesByKey(e, "communicationId")) {
			el.getAttributes().add(
					new EntityAttribute("CommunicationId", p.getKey(), (String) PropertyHelper.from(p).getValue()));
		}
		for (final G_Property p : EntityHelper.getPropertiesByTag(e, G_PropertyTag.GEO)) {
			el.getAttributes().add(
					new EntityAttribute("Address", p.getKey(), (String) PropertyHelper.from(p).getValue()));
		}
		for (final G_Property p : EntityHelper.getPropertiesByKey(e, "email")) {
			el.getAttributes()
					.add(new EntityAttribute("Email", p.getKey(), (String) PropertyHelper.from(p).getValue()));
		}
		for (final G_Property p : EntityHelper.getPropertiesByTag(e, G_PropertyTag.ID)) {
			el.getAttributes().add(
					new EntityAttribute("Identifier", (String) PropertyHelper.from(p).getValue(),
							(String) PropertyHelper.from(p).getValue()));
		}
		for (final G_Property ac : EntityHelper.getPropertiesByKey(e, "account")) {
			el.getAccountList().add((String) PropertyHelper.from(ac).getValue());
		}
		return el;

	}

}
