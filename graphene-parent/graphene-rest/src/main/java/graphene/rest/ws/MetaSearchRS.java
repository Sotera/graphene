package graphene.rest.ws;

import graphene.model.idl.G_AppInfo;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_Delimiter;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_PropertyDescriptors;
import graphene.model.idl.G_PropertyKey;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/meta")
public interface MetaSearchRS {
	@Produces("application/json")
	@GET
	@Path("/appinfo")
	public G_AppInfo getAppInfo();

	@Produces("application/json")
	@GET
	@Path("/canonicaltruths")
	public abstract List<G_CanonicalTruthValues> getCanonicalTruthValues();

	@Produces("application/json")
	@GET
	@Path("/canonicaltypes")
	public abstract List<G_CanonicalPropertyType> getCanonicalTypes();

	@Produces("application/json")
	@GET
	@Path("/delimiters")
	public abstract List<G_Delimiter> getDelimiters();

	@Produces("application/json")
	@GET
	@Path("/descriptors")
	public abstract G_PropertyDescriptors getDescriptors();

	@Produces("application/json")
	@GET
	@Path("/entitytags")
	public abstract List<G_EntityTag> getEntityTags();

	@Produces("application/json")
	@GET
	@Path("/genders")
	public abstract List<G_Gender> getGenders();

	@Produces("application/json")
	@GET
	@Path("/propertykeys")
	public abstract List<G_PropertyKey> getPropertyKeys();

	@Produces("application/json")
	@GET
	@Path("/propertytags")
	public abstract List<G_PropertyTag> getPropertyTags();

	@Produces("application/json")
	@GET
	@Path("/propertytypes")
	public abstract List<G_PropertyType> getPropertyTypes();

	@Produces("application/json")
	@GET
	@Path("/searchtypes")
	public abstract List<G_Constraint> getSearchTypes();

}
