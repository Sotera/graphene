package graphene.web.components.security;

import graphene.model.idl.G_SymbolConstants;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Federated Login form component
 * 
 */
public class FederatedLogin {

	private static final Logger logger = LoggerFactory
			.getLogger(FederatedLogin.class);
	@Inject
	@Symbol(value = G_SymbolConstants.ENABLE_FEDERATED_LOGIN)
	@Property
	private String federatedEnabled;

}
