/**
 * 
 */
package graphene.services;

import graphene.dao.StyleService;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.util.ColorUtil;
import graphene.util.validator.ValidationUtils;

import java.awt.Color;
import java.util.HashMap;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;

/**
 * @author djue
 * 
 */
public class StyleServiceImpl implements StyleService {

	@Inject
	private ColorUtil colorUtil;
	protected HashMap<String, String> colorMap = new HashMap<String, String>();
	private int numberOfDefinedColors = 0;

	public StyleServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getContrastingColor(final String hexColor) {
		final Color c = Color.decode(hexColor);
		return getDarkOrLight(c.getRed(), c.getBlue(), c.getGreen());
	}

	@Override
	public String getDarkOrLight(final int red, final int green, final int blue) {
		double brightness = (red * 299) + (green * 587) + (blue * 114);
		brightness /= 255000;

		if (brightness >= 0.5) {
			return "#000000";
		} else {
			return "#ffffff";
		}
	}

	@Override
	public String getHexColorForEdge(final String key) {
		return "#000000";
	}

	@Override
	public String getHexColorForNode(final String key) {
		if (!ValidationUtils.isValid(key)) {
			return "#808080";
		}
		// Use presets if available
		if (colorMap.containsKey(key)) {
			return colorMap.get(key);
		} else {
			// otherwise generate a color based on the key
			return randomColorForKey(key);
		}
	}

	@Override
	public String getStyleForEdge(final String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStyleForNode(final String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color hex2Rgb(final String hexColor) {
		return Color.decode(hexColor);
	}

	private String randomColorForKey(final String key) {
		final int hashInt = key.hashCode();
		// int r = hashInt % 16777216;
		return String.format("#%06X", (0xFFFFFF & hashInt));
	}

	@PostInjection
	public void setup() {
		colorMap.clear();
		colorMap.put(G_CanonicalPropertyType.CUSTOMER_NUMBER.name(),
				colorUtil.getPshade0());
		colorMap.put(G_CanonicalPropertyType.REPORT_ID.name(),
				colorUtil.getPshade0());
		colorMap.put(G_CanonicalPropertyType.ENTITY.name(),
				colorUtil.getS1shade1());
		colorMap.put(G_CanonicalPropertyType.ACCOUNT.name(),
				colorUtil.getS1shade1());

		colorMap.put(G_CanonicalPropertyType.PHONE.name(),
				colorUtil.getPshade1());
		colorMap.put(G_CanonicalPropertyType.EMAIL_ADDRESS.name(),
				colorUtil.getPshade2());
		colorMap.put(G_CanonicalPropertyType.IP.name(), colorUtil.getPshade3());
		colorMap.put(G_CanonicalPropertyType.ADDRESS.name(),
				colorUtil.getPshade4());
		colorMap.put(G_CanonicalPropertyType.NAME.name(),
				colorUtil.getS2shade2());

		colorMap.put(G_CanonicalPropertyType.EIN.name(),
				colorUtil.getScshade0());
		colorMap.put(G_CanonicalPropertyType.TAXID.name(),
				colorUtil.getScshade0());
		colorMap.put(G_CanonicalPropertyType.SSN.name(),
				colorUtil.getScshade0());
		colorMap.put(G_CanonicalPropertyType.GOVERNMENTID.name(),
				colorUtil.getScshade1());
		colorMap.put(G_CanonicalPropertyType.PASSPORT.name(),
				colorUtil.getScshade2());
		colorMap.put(G_CanonicalPropertyType.VISA.name(),
				colorUtil.getScshade2());
		colorMap.put(G_CanonicalPropertyType.LICENSEPLATE.name(),
				colorUtil.getScshade3());
		colorMap.put(G_CanonicalPropertyType.VIN.name(),
				colorUtil.getScshade3());
		colorMap.put(G_CanonicalPropertyType.FLIGHT.name(),
				colorUtil.getScshade4());

		colorMap.put(G_CanonicalPropertyType.TIME_DATE.name(),
				colorUtil.getS1shade4());

		numberOfDefinedColors = colorMap.size();
	}
}
