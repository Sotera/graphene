package graphene.web.components.ui;

import java.util.Locale;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = { "context:/core/js/propertyLister.js" })
public class PropertyLister {
	@Inject
	private AssetSource assetSource;
	@Inject
	private Locale locale;
	@Environmental
	protected JavaScriptSupport jss;

	@Property
	private String numProperties = "3";

	public String getAvatar() {

		return assetSource.getContextAsset("core/img/avatars/male.png", locale)
				.toClientURL();

	}

	void afterRender() {
		jss.addInitializerCall("makeChat", "reportChat");
	}
}
