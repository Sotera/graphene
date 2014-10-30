/**
 * 
 */
package graphene.web.services.javascript;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Mapper;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.got5.tapestry5.jquery.JQuerySymbolConstants;

/**
 * @author djue
 * 
 */
public class NeoCytoscapeStack implements JavaScriptStack {
	public static final String STACK_ID = "NeoCytoscapeStack";

	public NeoCytoscapeStack(
			@Symbol(JQuerySymbolConstants.USE_MINIFIED_JS) final boolean minified,
			final AssetSource assetSource) {

		final Mapper<String, Asset> pathToAsset = new Mapper<String, Asset>() {
			public Asset map(String path) {
				return assetSource.getExpandedAsset(path);
			}
		};

		final String path = String.format(
				"context:core/js/libs/cytoscape-2.2.12/cytoscape%s.js", minified ? ".min"
						: "");

		javaScriptStack = F
				.flow(path, "context:core/js/neo_cytoscape/script.js",
						"context:core/js/neo_cytoscape/load.js"

				).map(pathToAsset).toList();
		cssStack = Collections.emptyList();
	}

	private final List<Asset> javaScriptStack;

	private final List<StylesheetLink> cssStack;

	public String getInitialization() {

		return null;
	}

	public List<Asset> getJavaScriptLibraries() {

		return javaScriptStack;
	}

	public List<StylesheetLink> getStylesheets() {

		return cssStack;
	}

	public List<String> getStacks() {

		return Collections.emptyList();
	}

}
