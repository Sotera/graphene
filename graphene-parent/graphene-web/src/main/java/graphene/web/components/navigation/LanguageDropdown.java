package graphene.web.components.navigation;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class LanguageDropdown {

	@Inject
	@Path("context:/core/img/flags/us.png")
	@Property
	private Asset imgFlagUS;

}
