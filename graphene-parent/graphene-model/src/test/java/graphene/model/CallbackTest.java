package graphene.model;

import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;

import org.apache.avro.AvroRemoteException;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class CallbackTest {

	public class ExampleCallback implements G_CallBack {

		@Override
		public boolean execute(final G_SearchResult sr, final G_EntityQuery q) throws AvroRemoteException {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Test
	public void testCallback() {
		final ExampleCallback t = new ExampleCallback();

		final G_SearchResult var = G_SearchResult.newBuilder().setScore(1.0d).build();
		try {
			AssertJUnit.assertTrue(t.execute(var, null));
		} catch (final AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			AssertJUnit.assertFalse(t.execute(null, null));
		} catch (final AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
