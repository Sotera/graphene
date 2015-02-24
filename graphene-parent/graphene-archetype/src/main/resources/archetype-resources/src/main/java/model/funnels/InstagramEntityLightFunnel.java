package ${package}.model.funnels;

import graphene.model.funnels.Funnel;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.view.entities.EntityAttribute;
import graphene.model.view.entities.EntityLight;
import graphene.util.validator.ValidationUtils;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class InstagramEntityLightFunnel implements Funnel<EntityLight, G_Entity> {

	@Inject
	private Logger logger;

	@Override
	public G_Entity from(final EntityLight e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityLight to(final G_Entity e) {
		final EntityLight el = new EntityLight();
		if (ValidationUtils.isValid(e)) {

			el.setId(e.getUid());
			if (e.getProvenance() != null) {
				el.setDatasource_id(e.getProvenance().getUri());
			}
			final StringBuffer sb = new StringBuffer("");
			final List<G_Property> propertiesByTag = EntityHelper
					.getPropertiesByTag(e, G_PropertyTag.NAME);
			for (final G_Property p : propertiesByTag) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				// get the value of the property
				final String n = (String) PropertyHelper.from(p).getValue();
				el.getAttributes().add(new EntityAttribute("Name", "", n));
				sb.append(n);
			}
			if (ValidationUtils.isValid(e.getProvenance())) {
				el.setEffectiveName(e.getProvenance().getUri() + " "
						+ e.getUid() + ": " + sb.toString());
			}
			if (propertiesByTag.size() > 1) {
				el.setEffectiveName(el.getEffectiveName() + " ("
						+ propertiesByTag.size() + " associated values)");
			}
			el.setAllNames(sb.toString());
			for (final G_Property p : EntityHelper.getPropertiesByKey(e,
					"phone")) {
				el.getAttributes().add(
						new EntityAttribute("Phone", p.getKey(),
								(String) PropertyHelper.from(p).getValue()));
			}
			for (final G_Property p : EntityHelper.getPropertiesByTag(e,
					G_PropertyTag.GEO)) {
				el.getAttributes().add(
						new EntityAttribute("Address", p.getKey(),
								(String) PropertyHelper.from(p).getValue()));
			}
			for (final G_Property p : EntityHelper.getPropertiesByKey(e,
					"email")) {
				el.getAttributes().add(
						new EntityAttribute("Email", p.getKey(),
								(String) PropertyHelper.from(p).getValue()));
			}
			for (final G_Property p : EntityHelper.getPropertiesByTag(e,
					G_PropertyTag.ID)) {
				el.getAttributes().add(
						new EntityAttribute("Identifier",
								(String) PropertyHelper.from(p).getValue(),
								(String) PropertyHelper.from(p).getValue()));
			}
			for (final G_Property p : EntityHelper.getPropertiesByTag(e,
					G_PropertyTag.TEXT)) {
				el.getAttributes().add(
						new EntityAttribute("Narrative",
								(String) PropertyHelper.from(p).getValue(),
								(String) PropertyHelper.from(p).getValue()));
			}
			for (final G_Property ac : EntityHelper.getPropertiesByKey(e,
					"account")) {
				el.getAccountList().add(
						(String) PropertyHelper.from(ac).getValue());
			}

		} else {
			logger.error("Error funneling: source object was null");

		}
		return el;
	}

}
