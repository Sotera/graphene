package graphene.model;

import graphene.model.graph.DirectedNode;
import graphene.model.graph.GenericNode;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * 
 * @author djue
 * 
 */
public class GRNodeTest {
	@Test
	public void testGRNode() {
		GenericNode n = new GenericNode();
		n.setBackgroundColor("unicorns");
		n.setId("SomeId");
		n.addData("attr1", "value1");
		n.addData("attr1", "value1");
		System.out.println(n);
		AssertJUnit.assertNotNull(n);
	}

	@Test
	public void testDirectedNode() throws Exception {
		DirectedNode d = new DirectedNode("MyValue", "MyId");
		System.out.println(d);
		AssertJUnit.assertNotNull(d);
	}
}
