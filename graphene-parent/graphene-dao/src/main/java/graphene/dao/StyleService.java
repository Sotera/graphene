package graphene.dao;

import graphene.model.idl.G_LegendItem;

import java.awt.Color;
import java.util.List;

/**
 * Provides styling for nodes and property types based on a key String.
 * 
 * @author djue
 * 
 */
public interface StyleService {
	public abstract String getContrastingColor(String hexColor);

	public abstract String getCSSClass(String nodeType, boolean highlighted);

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

	/**
	 * This is the background color for a highlight
	 * 
	 * @return This returns a CSS style.
	 */
	public String getHighlightBackgroundColor();

	/**
	 * This is the text color for a highlight
	 * 
	 * @return This returns a CSS style.
	 */
	public abstract String getHighlightColor();

	/**
	 * This is a combination of the highlight text and background color
	 * 
	 * @return This returns a CSS style.
	 */
	public abstract String getHighlightStyle();

	public abstract List<G_LegendItem> getLegendForReports();

	/**
	 * This is a combination of get HighlightStyle (if the bool is true) and get
	 * HexColorForNode for the background color.
	 * 
	 * @param nodeType
	 * @param highlighted
	 * @return This returns a CSS style.
	 */
	public abstract String getStyle(String nodeType, boolean highlighted);

	public abstract String getStyleForEdge(String key);

	public abstract String getStyleForNode(String key);

	public abstract Color hex2Rgb(String hexColor);

}
