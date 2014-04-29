package graphene.web.pages;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

@Import(stack={"CytoscapeStack","NeoCytoscapeStack"})
@PluginPage(visualType = G_VisualType.GRAPH)
public class ExampleCytoscape {
	@Property
	@Persist
	private String title;

	public void setupRender() {
		title = "Cytoscape Test";
	}

	

}
