package graphene.util.db;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UsesOrderedConfiguration(String.class)
public class JDBCUtil {

	private Logger logger = LoggerFactory.getLogger(JDBCUtil.class);

	public JDBCUtil(Collection<String> drivers) {
		findJDBCDrivers(drivers);
	}

	/**
	 * A convenience/info utility for doing ClassForName on jdbc drivers.
	 * 
	 * @param drivers
	 */
	public void findJDBCDrivers(Collection<String> drivers) {
		if (drivers != null) {
			for (String driver : drivers) {
				try {
					Class<?> c = Class.forName(driver);
					if (c != null) {
						logger.info("+++++++++ SUCCESS finding " + driver);
					}
				} catch (ClassNotFoundException e1) {
					logger.warn("======== Could not find " + driver
							+ " on classpath");
					e1.printStackTrace();
				}
			}
		}
	}
}
