package graphene.dao;

import java.awt.Color;

/**
 * Provides styling for nodes and property types based on a key String.
 * 
 * @author djue
 * 
 */
public interface StyleService {
	public abstract String getContrastingColor(String hexColor);

	public abstract String getDarkOrLight(int red, int green, int blue);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getHexColorForEdge(String key);

	/**
	 * 
	 * @param key
	 *            usually a name of an enum from G_CanonicalPropertyTypes, but
	 *            you can supply your own string.
	 * @return a hex color string for css, like #3412ff
	 */
	public String getHexColorForNode(String key);

	public String getStyleForEdge(String key);

	public String getStyleForNode(String key);

	public abstract Color hex2Rgb(String hexColor);

}
