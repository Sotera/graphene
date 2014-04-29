package graphene.web.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ColorUtil {
	private static final String BUNDLE_NAME = "graphene.model.util.colors"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ColorUtil() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
