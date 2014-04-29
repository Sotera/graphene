package graphene.web.components.security;

import graphene.web.services.Authenticator;

import org.apache.tapestry5.corelib.base.AbstractConditional;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Checks if the user is logged in
 * 
 * @author karesti
 */
public class Authenticated extends AbstractConditional
{

    @Inject
    private Authenticator authenticator;

    @Override
    protected boolean test()
    {
        return authenticator.isLoggedIn();
    }

}
