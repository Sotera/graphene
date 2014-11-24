package graphene.web.components.ui;

import graphene.util.validator.ValidationUtils;

import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.BeanDisplay;
import org.apache.tapestry5.corelib.components.PropertyDisplay;
import org.apache.tapestry5.internal.beaneditor.BeanModelUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

@SupportsInformalParameters
public class MBeanDisplay {

	@Component(publishParameters = "object,lean,model,include,exclude,reorder,add,overrides ", inheritInformalParameters = true)
	private BeanDisplay beanDisplay;

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
	private ComponentResources resources;

	@Inject
	private BeanModelSource modelSource;

	@Property
	private String propertyName;

	private boolean modified = false;
    /**
     * The model that identifies the parameters to be edited, their order, and every other aspect. If not specified, a
     * default bean model will be created from the type of the object bound to the object parameter. The add, include,
     * exclude and reorder
     * parameters are <em>only</em> applied to a default model, not an explicitly provided one.
     */
    @Parameter
    @Property(write = false)
    private BeanModel model;
    PropertyDisplay d;
    @SetupRender
    void modifyModel(){
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
			
		}
    }
	

    /**
     * The object to be rendered; if not explicitly bound, a default binding to a property whose name matches this
     * component's id will be used.
     */
    @Parameter(required = true, allowNull = false, autoconnect = true)
    @Property(write = false)
    private Object object;

	public Object getPropertyValue() {
		return beanDisplay.getPropertyModel().getConduit().get(object);
	}

	public boolean isMeaningful(Object obj) {

		if (obj == null || !ValidationUtils.isValid(obj.toString())) {
			return false;
		}
		return true;
	}
}
