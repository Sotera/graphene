package graphene.model.graph.entity;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This entire class is obsolete. Eliminate its use. --djue
 * 
 * @author PWG
 * 
 */
@Deprecated
public class IconMap {
	private IconMap() {
		// TODO Auto-generated constructor stub
	}

	private static Map<String, String> iconMap = null;
	static Logger logger = LoggerFactory.getLogger(IconMap.class);

	static private void populateIconMap() {
//Assign custom icons here.
	}

	static {
		iconMap = new HashMap<String, String>();
		populateIconMap();
	}

	public static String getIcon(String idType) {
		// if (iconMap == null) {
		// iconMap = new HashMap<String, String>();
		// populateIconMap();
		// }
		String icon = iconMap.get(idType);
		if (icon == null) {
			// logger.error("No icon found for " + idType);
			icon = "core/images/Token_Dark/Token-PNG/Miscellaneous/Oh.png";
		}

		return icon;
	}

}
