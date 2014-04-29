// Based on http://tapestry.apache.org/tapestry5/guide/beaneditform.html

package graphene.web.pages.infrastructure;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.PropertyOutputContext;

public class AppPropertyDisplayBlocks {

	@Property
	@Environmental
	private PropertyOutputContext context;

}
