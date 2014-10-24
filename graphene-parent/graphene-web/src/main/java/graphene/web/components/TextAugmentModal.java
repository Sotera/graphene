package graphene.web.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = { "context:/core/js/textselect.js",
		"context:/core/js/plugin/highlighter/jQuery.highlighter.js" })
public class TextAugmentModal {

	@Inject
	private JavaScriptSupport jss;

	void afterRender() {
		jss.addInitializerCall("makeTextSelect", "reportSelect");
	}
}
