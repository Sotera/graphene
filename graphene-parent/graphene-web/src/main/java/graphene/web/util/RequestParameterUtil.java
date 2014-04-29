package graphene.web.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParameterUtil {
	private static Logger logger = LoggerFactory
			.getLogger(RequestParameterUtil.class);

	public static int intParam(HttpServletRequest rq, String name) {
		String s = rq.getParameter(name);
		int d = 0;
		try {

			if (s == null || s.length() == 0) {
				d = 0;
			} else {
				d = Integer.parseInt(s);
			}
		} catch (Exception e) {
			logger.debug("Could not parse int parameter: '" + name + "'");
		}
		return d;
	}

	public static long longParam(HttpServletRequest rq, String name) {
		String s = rq.getParameter(name);
		long d = 0;
		try {

			if (s == null || s.length() == 0) {
				d = 0;
			} else {
				d = Long.parseLong(s);
			}
		} catch (Exception e) {
			logger.debug("Could not parse long parameter: '" + name + "'");
		}
		return d;
	}

	public static double doubleParam(HttpServletRequest rq, String name) {
		String s = rq.getParameter(name);
		double d = 0;
		try {
			if (s == null || s.length() == 0)
				d = 0;
			else
				d = Double.parseDouble(s);
		} catch (Exception e) {
			logger.debug("Could not parse double parameter: '" + name + "'");
		}
		return d;
	}
}
