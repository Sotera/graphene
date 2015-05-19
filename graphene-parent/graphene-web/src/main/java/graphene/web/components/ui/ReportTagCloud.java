package graphene.web.components.ui;

import graphene.model.idl.G_SymbolConstants;

import java.util.Collection;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class ReportTagCloud {
	@Property
	@Parameter(required = true, autoconnect = true)
	private Collection<String> tags;
	@Property
	@Parameter(required = true, autoconnect = true, value = "literal:Tags")
	private String title;

	@Property
	private String currentTag;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_TAG_CLOUDS)
	@Property(write = false, read = true)
	private boolean enableTagClouds;
}
