package graphene.rest.ws;

import graphene.model.idl.G_AppInfo;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_Delimiter;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_LinkTag;
import graphene.model.idl.G_NodeType;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SymbolConstants;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;

public class MetaSearchRSImpl implements MetaSearchRS {

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Override
	public G_AppInfo getAppInfo() {
		return new G_AppInfo(appName, appVersion, DateTime.now().getMillis());
	}

	@Override
	public List<G_CanonicalTruthValues> getCanonicalTruthValues() {
		return Arrays.asList(G_CanonicalTruthValues.values());
	}

	@Override
	public List<G_CanonicalPropertyType> getCanonicalTypes() {
		return Arrays.asList(G_CanonicalPropertyType.values());
	}

	@Override
	public List<G_Delimiter> getDelimiters() {
		return Arrays.asList(G_Delimiter.values());
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		return Arrays.asList(G_EntityTag.values());
	}

	@Override
	public List<G_Gender> getGenders() {
		return Arrays.asList(G_Gender.values());
	}

	@Override
	public List<G_LinkTag> getLinkTags() {
		return Arrays.asList(G_LinkTag.values());
	}

	@Override
	public List<G_NodeType> getNodeTypes() {
		return Arrays.asList(G_NodeType.values());
	}

	@Override
	public List<G_PropertyTag> getPropertyTags() {
		return Arrays.asList(G_PropertyTag.values());
	}

	@Override
	public List<G_PropertyType> getPropertyTypes() {
		return Arrays.asList(G_PropertyType.values());
	}

	@Override
	public List<G_SearchType> getSearchTypes() {
		return Arrays.asList(G_SearchType.values());
	}

}
