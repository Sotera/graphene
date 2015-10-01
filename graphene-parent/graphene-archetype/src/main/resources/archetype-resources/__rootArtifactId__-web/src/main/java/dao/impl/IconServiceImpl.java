#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.impl;

import graphene.dao.IconService;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idlhelper.PropertyHelper;
import graphene.util.Tuple;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class IconServiceImpl implements IconService {
	private final HashMap<Pattern, String> iconPatternMap = new HashMap<Pattern, String>();
	private final HashMap<Pattern, Tuple<String, String>> iconMap = new HashMap<Pattern, Tuple<String, String>>();
	@Inject
	private Logger logger;

	public IconServiceImpl() {
		if (iconPatternMap.isEmpty()) {
			addPattern("${symbol_escape}${symbol_escape}s(youtube|video)", false, "fa fa-youtube-play", "Document mentions videos or youtube");
		}
	}

	@Override
	public void addPattern(final String pattern, final boolean caseSensitive, final String iconClass,
			final String reason) {
		Pattern p;
		if (!caseSensitive) {
			p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

		} else {
			p = Pattern.compile(pattern);

		}
		iconPatternMap.put(p, iconClass);
		iconMap.put(p, new Tuple<String, String>(iconClass, reason));
	}

	@Override
	public List<Object> getIconsForText(final String text, final G_EntityQuery sq) {
		final ArrayList<String> otherKeys = new ArrayList<String>();
		final List<G_PropertyMatchDescriptor> propertyMatchDescriptors = sq.getPropertyMatchDescriptors();
		if (ValidationUtils.isValid(propertyMatchDescriptors)) {
			for (final G_PropertyMatchDescriptor p : propertyMatchDescriptors) {

				if (ValidationUtils.isValid(p.getSingletonRange())) {
					if (p.getSingletonRange().getType().equals(G_PropertyType.STRING)) {
						otherKeys.add((String) p.getSingletonRange().getValue());
					}
				} else if (ValidationUtils.isValid(p.getListRange())) {
					if (p.getListRange().getType().equals(G_PropertyType.STRING)) {
						for (final Object i : p.getListRange().getValues()) {
							otherKeys.add((String) i);
						}
					}
				}
			}
		}
		return new ArrayList(getIconsForText(text, otherKeys.toArray(new String[otherKeys.size()])));
	}

	@Override
	public Collection<G_Property> getIconsForText(final String text, final String... otherKeys) {
		final Collection<G_Property> icons = new ArrayList<G_Property>();
		try {
			if (ValidationUtils.isValid(text, otherKeys)) {
				Matcher m;
				for (final Pattern i : iconMap.keySet()) {
					m = i.matcher(text);
					if (m.find()) {
						icons.add(new PropertyHelper(iconMap.get(i).getFirst(), iconMap.get(i).getSecond(),
								G_PropertyTag.IMAGE));
					}
					m.reset();
				}
				Matcher m2;
				for (final String o : otherKeys) {
					m2 = Pattern.compile(o, Pattern.CASE_INSENSITIVE).matcher(text);
					if (m2.find()) {
						icons.add(new PropertyHelper("fa fa-asterisk", "Search term appeared in text",
								G_PropertyTag.IMAGE));
					}
					m2.reset();
				}
			}
		} catch (final Exception e) {
			logger.error("No icons will be returned: " + e.getMessage());
		}
		return icons;
	}

	@Override
	public Collection<G_Property> getIconsForTextWithCount(final String text, final String... otherKeys) {
		final Collection<G_Property> icons = new ArrayList<G_Property>();
		if (ValidationUtils.isValid(text, otherKeys)) {
			Matcher m;

			for (final Pattern i : iconPatternMap.keySet()) {
				m = i.matcher(text);
				int count = 0;
				while (m.find()) {
					count++;
				}
				if (count > 0) {
					icons.add(new PropertyHelper(iconMap.get(i).getFirst(), iconMap.get(i).getSecond() + " (" + count
							+ ")", G_PropertyTag.IMAGE));
				}
				m.reset();
			}
			// Look through the extra keys
			Matcher m2;
			for (final String o : otherKeys) {
				m2 = Pattern.compile(o, Pattern.CASE_INSENSITIVE).matcher(text);
				int count = 0;
				while (m2.find()) {
					count++;
				}
				if (count > 0) {
					icons.add(new PropertyHelper("fa fa-asterisk", "Search term appeared in text (" + count + ")",
							G_PropertyTag.IMAGE));

				}
				m2.reset();
			}
		}
		return icons;
	}

	@Override
	public void removePattern(final String pattern, final boolean caseSensitive) {
		if (ValidationUtils.isValid(pattern)) {
			try {
				Pattern p;
				if (caseSensitive) {
					p = Pattern.compile(pattern);
				} else {
					p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				}
				iconPatternMap.remove(p);
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}
