package graphene.dao;

import java.awt.Color;

public interface StyleService {
	public String getHexColorForNode(String key);

	public String getHexColorForEdge(String key);

	public String getStyleForNode(String key);

	public String getStyleForEdge(String key);

	public abstract String getDarkOrLight(int red, int green, int blue);

	public abstract String getContrastingColor(String hexColor);

	public abstract Color hex2Rgb(String hexColor);

}
