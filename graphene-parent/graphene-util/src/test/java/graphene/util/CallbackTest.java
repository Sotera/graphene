package graphene.util;

import graphene.util.G_CallBack;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class CallbackTest {

	public class ExampleCallback implements G_CallBack<Object> {

		@Override
		public boolean callBack(Object t) {
			if (t == null) {
				return false;
			}
			return true;
		}

	}

	@Test
	public void testCallback() {
		ExampleCallback t = new ExampleCallback();

		AssertJUnit.assertTrue(t.callBack("asdf"));
		AssertJUnit.assertFalse(t.callBack(null));
	}
}
