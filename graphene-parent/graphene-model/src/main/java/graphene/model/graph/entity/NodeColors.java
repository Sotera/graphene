package graphene.model.graph.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeColors {
	private NodeColors() {
		// TODO Auto-generated constructor stub
	}

	public static final String leaf_bg_color = "#0000ff";
	public static final String origin_bg_color = "#ffff00";
	static Logger logger = LoggerFactory.getLogger(NodeColors.class);

	/**
	 * @param GQT
	 *            if true use the colors from the legacy Graph Query Tool
	 * @param isOrigin
	 *            true if one of the nodes searched for
	 * @param isLeaf
	 *            true if no further links
	 * @param valueType
	 *            one of "account", "customer" or an identifier family
	 * @param dataset
	 *            a code for the data source (currently unused)
	 **/
	public static String getNodeColor(boolean GQT, boolean isOrigin,
			boolean isLeaf, String valueType, int dataset) {
		String color = null;

		if (GQT)
			color = GQTColor(isOrigin, valueType, dataset);
		else if (isOrigin)
			color = origin_bg_color;
		else if (isLeaf)
			color = leaf_bg_color;
		else
			color = "#FFFFFF";

		return color;

	}

	/**
	 * @return the color used by the Graphics Query T * @param isOrigin true if
	 *         one of the nodes searched for
	 * @param valueType
	 *            one of "account", "customer" or an identifier family
	 * @param dataset
	 *            a code for the data source (currently unused) ool currently in
	 *            use
	 */
	public static String GQTColor(boolean isOrigin, String valueType,
			int dataset) {
		// type should be "customer", "account" or an Identifier short name

		// logger.debug("getting color for valuetype " + valueType);

		if (isOrigin)
			return "red"; // RED

		if (valueType == null || valueType.length() == 0)
			return "black"; // should not happen

		String color = "Silver"; // Light gray default

		if (valueType.equals("address"))
			color = "Gray"; // LightSlateGray
		else if (valueType.equals("communicationId"))
			color = "Aqua"; // LightCyan
		else if (valueType.contains("office"))
			color = "Teal";
		else if (valueType.equals("account")) {
			color = "Lime";

		}
		/*
		 * switch (dataset) { case 0: color = "lime"; // LightGreen break; case
		 * 1: color = "fuschia"; // magenta break; case 2: color = "Green";
		 * break; default: color = "White"; // white break; } }
		 */
		else if (valueType.equals("customer")) {
			color = "orange";
		}

		else if (valueType.equals("number")) {
			color = "aqua";
		}

		else
			// default.
			color = "green"; // green

		return color;

	}

}
