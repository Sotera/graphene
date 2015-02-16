package graphene.web.components.admin;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;

public class SystemStatus {
	@Inject
	@Symbol(SymbolConstants.PRODUCTION_MODE)
	@Property(write = false)
	private boolean productionMode;

	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	@Property(write = false)
	private String tapestryVersion;
	private static final String PATH_SEPARATOR_PROPERTY = "path.separator";
	private final String pathSeparator = System.getProperty(PATH_SEPARATOR_PROPERTY);
	// Match anything ending in .(something?)path.

	private static final Pattern PATH_RECOGNIZER = Pattern.compile("\\..*path$");

	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	@Property(write = false)
	private String applicationVersion;

	@Property
	private String propertyName;

	public String[] getComplexPropertyValue() {
		// Neither : nor ; is a regexp character.

		return getPropertyValue().split(pathSeparator);
	}

	public String getPropertyValue() {
		return System.getProperty(propertyName);
	}

	public List<String> getSystemProperties() {
		return InternalUtils.sortedKeys(System.getProperties());
	}

	public boolean isComplexProperty() {
		return PATH_RECOGNIZER.matcher(propertyName).find() && getPropertyValue().contains(pathSeparator);
	}
}
