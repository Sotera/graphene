package graphene.rest.ws.impl;

import graphene.model.idl.G_AppInfo;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_Delimiter;
import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKey;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_SymbolConstants;
import graphene.rest.ws.MetaSearchRS;

import java.util.Arrays;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;
import org.slf4j.Logger;

public class MetaSearchRSImpl implements MetaSearchRS {

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Inject
	private G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	private G_NodeTypeAccess nodeTypeAccess;

	@Inject
	private G_PropertyKeyTypeAccess propertyKeyTypeAccess;

	@Inject
	private Logger logger;

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
	public List<G_IdType> getNodeTypes() {
		try {
			return nodeTypeAccess.getNodeTypes();
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public List<G_PropertyKey> getPropertyKeys() {
		try {
			return propertyKeyTypeAccess.getPropertyKeys();
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return null;
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
	public List<G_EdgeType> getRelationshipTypes() {
		try {
			return edgeTypeAccess.getEdgeTypes();
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<G_Constraint> getSearchTypes() {
		return Arrays.asList(G_Constraint.values());
	}

}
