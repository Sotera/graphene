package graphene.web.components.ui;

import graphene.dao.StyleService;
import graphene.util.validator.ValidationUtils;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.internal.beaneditor.BeanModelUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SupportsInformalParameters
public class MeaningfulBeanDisplay {
	/**
	 * The object to be rendered; if not explicitly bound, a default binding to
	 * a property whose name matches this component's id will be used.
	 */
	@Parameter(required = true, allowNull = false, autoconnect = true)
	@Property(write = false)
	private Object object;

	/**
	 * If true, then the CSS class attribute on the &lt;dt&gt; and &lt;dd&gt;
	 * elements will be ommitted.
	 */
	@Parameter(value = "false")
	private boolean lean;

	/**
	 * The model that identifies the parameters to be edited, their order, and
	 * every other aspect. If not specified, a default bean model will be
	 * created from the type of the object bound to the object parameter. The
	 * add, include, exclude and reorder parameters are <em>only</em> applied to
	 * a default model, not an explicitly provided one.
	 */
	@Parameter
	@Property(write = false)
	private BeanModel model;
	/**
	 * A comma-separated list of property names to be retained from the
	 * {@link org.apache.tapestry5.beaneditor.BeanModel} (only used when a
	 * default model is created automatically). Only these properties will be
	 * retained, and the properties will also be reordered. The names are
	 * case-insensitive.
	 */
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String include;

	/**
	 * A comma-separated list of property names to be removed from the
	 * {@link org.apache.tapestry5.beaneditor.BeanModel} (only used when a
	 * default model is created automatically). The names are case-insensitive.
	 */
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String exclude;

	/**
	 * A comma-separated list of property names indicating the order in which
	 * the properties should be presented. The names are case insensitive. Any
	 * properties not indicated in the list will be appended to the end of the
	 * display orde. Only used when a default model is created automatically.
	 */
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String reorder;

	/**
	 * A comma-separated list of property names to be added to the
	 * {@link org.apache.tapestry5.beaneditor.BeanModel} (only used when a
	 * default model is created automatically).
	 */
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String add;

	/**
	 * Where to search for local overrides of property display blocks as block
	 * parameters. Further, the container of the overrides is used as the source
	 * for overridden validation messages. This is normally the component
	 * itself, but when the component is used within a BeanEditForm, it will be
	 * the BeanEditForm's block parameter that will be searched.
	 */
	@Parameter(value = "componentResources")
	@Property(write = false)
	private ComponentResources overrides;

	@Inject
	protected StyleService style;

	@Inject
	private ComponentResources resources;

	@Inject
	private BeanModelSource modelSource;

	@Property
	private String propertyName;

	private boolean modified = false;

	private String className;

	void setupRender() {
		className = object.getClass().getName();
		if (model == null) {
			model = modelSource.createDisplayModel(object.getClass(),
					overrides.getContainerMessages());
			List<String> names = model.getPropertyNames();
			for (String p : names) {
				PropertyModel pm = model.get(p);
				PropertyConduit conduit = pm.getConduit();
				Object propertyValue = conduit.get(object);
				if (!ValidationUtils.isValid(propertyValue)) {
					model.exclude(p);
				}
			}
			BeanModelUtils.modify(model, add, include, exclude, reorder);
		}
	}

	/**
	 * Returns the property model for the current property.
	 */
	public PropertyModel getPropertyModel() {
		return model.get(propertyName);
	}

	public Object getPropertyValue() {
		return model.get(propertyName).getConduit().get(object);
	}

	public String getPropertyClass() {
		return lean ? null : getPropertyModel().getId();
	}

	public String getPropertyLinkClass() {
		String textColor = "txt-color-white";
		String hexColorForNode = style.getHexColorForNode(className);
		if (StringUtils.containsIgnoreCase(className, "Location")) {
			return "btn btn-xs bg-color-blueLight txt-color-white";
		} else if (StringUtils.containsIgnoreCase(className, "Address")) {
			return "btn btn-xs bg-color-blueDark txt-color-white";
		} else if (StringUtils.containsIgnoreCase(className, "Account")) {
			return "btn btn-xs bg-color-orange txt-color-white";
		} else if (StringUtils.containsIgnoreCase(className, "Subject")) {
			return "btn btn-xs bg-color-purple txt-color-white";
		} else if (StringUtils.containsIgnoreCase(className, "Name")) {
			return "btn btn-xs bg-color-greenLight txt-color-white";
		} else {
			return "btn btn-xs bg-color-teal txt-color-white";
		}

	}

	public boolean isMeaningful(Object obj) {

		if (obj == null || !ValidationUtils.isValid(obj.toString())) {
			return false;
		}
		return true;
	}
}
