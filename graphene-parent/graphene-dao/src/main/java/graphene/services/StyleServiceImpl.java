/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/**
 * 
 */
package graphene.services;

import graphene.dao.StyleService;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_LegendItem;
import graphene.util.ColorUtil;
import graphene.util.validator.ValidationUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.slf4j.Logger;

/**
 * @author djue
 * 
 */
public class StyleServiceImpl implements StyleService {

	@Inject
	private ColorUtil colorUtil;
	protected HashMap<String, String> colorMap;
	protected HashMap<String, String> styleMap;
	private int numberOfDefinedColors = 0;

	private final String highlightStyle = "background-color: " + getHighlightBackgroundColor() + "; color: "
			+ getHighlightColor();

	@Inject
	private Logger logger;

	public static final String WHITE = "#fff";

	public static final String BLACK = "#000";

	@Inject
	public StyleServiceImpl() {
		colorMap = new HashMap<String, String>();
		styleMap = new HashMap<String, String>();
	}

	public String buildStyle(final String backgroundColor, final String textColor) {
		final String s = new StringBuffer().append("background-color: ").append(backgroundColor).append("; color: ")
				.append(textColor).append(";").toString();
		return s;
	}

	@Override
	public String getContrastingColor(final String hexColor) {
		final Color c = Color.decode("0x" + hexColor.replace("#", "").trim());
		return getDarkOrLight(c.getRed(), c.getGreen(), c.getBlue());
	}

	@Override
	public String getCSSClass(final String nodeType, final boolean highlighted) {
		if (highlighted) {
			return "highlighted";
		} else {
			return "nodeType";
		}
	}

	@Override
	public String getDarkOrLight(final int red, final int green, final int blue) {
		// double brightness = (red * 299) + (green * 587) + (blue * 114);
		final double y = ((red * 299) + (green * 587) + (blue * 114)) / 1000;
		// brightness /= 255000;
		if (y >= 128.0) {
			return BLACK;
		} else {
			return WHITE;
		}
	}

	@Override
	public String getHexColorForEdge(final String key) {
		return BLACK;
	}

	@Override
	public String getHexColorForNode(final String key) {
		String color = null;
		if (!ValidationUtils.isValid(key)) {
			logger.error("Invalid key!");
			color = "#808080";
		} else {
			// Use presets if available
			color = colorMap.get(key);
			if (color == null) {
				// otherwise generate a color based on the key
				color = randomColorForKey(key);
			}
		}
		return color;
	}

	@Override
	public String getHighlightBackgroundColor() {
		return "#a90329";
	}

	@Override
	public String getHighlightColor() {
		return WHITE;
	}

	@Override
	public String getHighlightStyle() {
		return highlightStyle;
	}

	@Override
	public List<G_LegendItem> getLegendForReports() {
		final List<G_LegendItem> list = new ArrayList<G_LegendItem>();
		list.add(new G_LegendItem("The narrative of the report", getStyle(G_CanonicalPropertyType.FREETEXT.name(),
				false), "Narrative", null));
		list.add(new G_LegendItem("The event or activity associated with this report.", getStyle(
				G_CanonicalPropertyType.EVENT.name(), false), "Activity", null));
		list.add(new G_LegendItem("A person or group formally mentioned in the report.", getStyle(
				G_CanonicalPropertyType.NAME.name(), false), "Subject", null));
		list.add(new G_LegendItem("The institution that filed the report.", getStyle(
				G_CanonicalPropertyType.FILER.name(), false), "Filing Institution", null));
		list.add(new G_LegendItem("An institution involved in the report.", getStyle(
				G_CanonicalPropertyType.INSTITUTION.name(), false), "Financial Institution", null));

		return list;
	}

	@Override
	public String getStyle(final String nodeType, final boolean highlighted) {
		// logger.debug("Getting Style for " + nodeType);
		if (highlighted) {
			return getHighlightStyle();
		} else {
			final String s = styleMap.get(nodeType);
			if (s == null) {
				final String color = getHexColorForNode(nodeType);
				styleMap.put(nodeType, buildStyle(color, getContrastingColor(color)));
				return s;
			}
			return styleMap.get(nodeType);
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
		final String color = String.format("#%06X", (0xFFFFFF & hashInt));
		// cache it
		colorMap.put(key, color);
		return color;

	}

	@PostInjection
	public void setup() {

		colorMap.clear();
		colorMap.put(G_CanonicalPropertyType.CUSTOMER_NUMBER.name(), colorUtil.getPshade0());
		colorMap.put(G_CanonicalPropertyType.REPORT_ID.name(), colorUtil.getPshade0());
		colorMap.put(G_CanonicalPropertyType.ENTITY.name(), colorUtil.getS1shade1());
		colorMap.put(G_CanonicalPropertyType.ACCOUNT.name(), colorUtil.getS1shade1());

		colorMap.put(G_CanonicalPropertyType.PHONE.name(), colorUtil.getPshade1());
		colorMap.put(G_CanonicalPropertyType.EMAIL_ADDRESS.name(), colorUtil.getPshade2());
		colorMap.put(G_CanonicalPropertyType.IP.name(), colorUtil.getPshade3());
		colorMap.put(G_CanonicalPropertyType.ADDRESS.name(), colorUtil.getPshade4());
		colorMap.put(G_CanonicalPropertyType.NAME.name(), "#8086F0");

		colorMap.put(G_CanonicalPropertyType.EIN.name(), colorUtil.getScshade0());
		colorMap.put(G_CanonicalPropertyType.TAXID.name(), colorUtil.getScshade0());
		colorMap.put(G_CanonicalPropertyType.SSN.name(), colorUtil.getScshade0());
		colorMap.put(G_CanonicalPropertyType.GOVERNMENTID.name(), colorUtil.getScshade1());
		colorMap.put(G_CanonicalPropertyType.PASSPORT.name(), colorUtil.getScshade2());
		colorMap.put(G_CanonicalPropertyType.VISA.name(), colorUtil.getScshade2());
		colorMap.put(G_CanonicalPropertyType.LICENSEPLATE.name(), colorUtil.getScshade3());
		colorMap.put(G_CanonicalPropertyType.VIN.name(), colorUtil.getScshade3());
		colorMap.put(G_CanonicalPropertyType.FLIGHT.name(), colorUtil.getScshade4());
		colorMap.put(G_CanonicalPropertyType.OTHER.name(), "#A0522D");
		colorMap.put(G_CanonicalPropertyType.FREETEXT.name(), "lightcoral");// #FFD1DC
		colorMap.put(G_CanonicalPropertyType.INSTITUTION.name(), "cornflowerblue");// "#FDDFB3"
		colorMap.put(G_CanonicalPropertyType.FILER.name(), "#909090");
		colorMap.put(G_CanonicalPropertyType.EVENT.name(), "gold");
		colorMap.put(G_CanonicalPropertyType.TIME_DATE.name(), colorUtil.getS1shade4());

		numberOfDefinedColors = colorMap.size();
		styleMap.clear();
		styleMap.put("ERROR", buildStyle("#ff0", BLACK));
		styleMap.put(G_CanonicalPropertyType.NAME.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.NAME.name()), WHITE));
		styleMap.put(G_CanonicalPropertyType.EMAIL_ADDRESS.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.EMAIL_ADDRESS.name()), WHITE));
		styleMap.put(G_CanonicalPropertyType.OTHER.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.OTHER.name()), WHITE));
		styleMap.put(G_CanonicalPropertyType.FREETEXT.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.FREETEXT.name()), BLACK));
		styleMap.put(G_CanonicalPropertyType.INSTITUTION.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.INSTITUTION.name()), BLACK));
		styleMap.put(G_CanonicalPropertyType.FILER.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.FILER.name()), WHITE));
		styleMap.put(G_CanonicalPropertyType.EVENT.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.EVENT.name()), BLACK));
		styleMap.put(G_CanonicalPropertyType.GOVERNMENTID.name(),
				buildStyle(colorMap.get(G_CanonicalPropertyType.GOVERNMENTID.name()), BLACK));
	}
}
