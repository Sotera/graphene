package graphene.model;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;
import graphene.model.query.G_CallBack;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class CallbackTest {

	public class ExampleCallback implements G_CallBack {

		@Override
		public boolean callBack(final G_SearchResult t, final G_EntityQuery q) {
			if (t == null) {
				return false;
			}
			return true;
		}

	}

	@Test
	public void testCallback() {
		final ExampleCallback t = new ExampleCallback();

		final G_SearchResult var = G_SearchResult.newBuilder().setScore(1.0d).build();
		AssertJUnit.assertTrue(t.callBack(var, null));
		AssertJUnit.assertFalse(t.callBack(null, null));
	}
}
