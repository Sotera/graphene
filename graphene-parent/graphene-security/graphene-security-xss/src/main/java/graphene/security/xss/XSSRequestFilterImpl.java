package graphene.security.xss;

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Papy
 * 
 */
public class XSSRequestFilterImpl implements RequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(XSSRequestFilterImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.tapestry5.services.RequestFilter#service(org.apache.tapestry5
	 * .services.Request, org.apache.tapestry5.services.Response,
	 * org.apache.tapestry5.services.RequestHandler)
	 */
	@Override
	public boolean service(final Request request, final Response response, final RequestHandler handler)
			throws IOException {
		if (logger.isDebugEnabled()) {
			// logger.debug("Wrapping Tapestry Request in XSSRequestWrapper");
		}
		return handler.service(new XSSRequestWrapper(request), response);
	}

}