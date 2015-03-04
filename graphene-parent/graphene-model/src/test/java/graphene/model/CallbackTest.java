package graphene.util;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class CallbackTest {

	public class ExampleCallback implements G_CallBack<Object,Object> {

		@Override
		public boolean callBack(Object t) {
			if (t == null) {
				return false;
			}
			return true;
		}

		@Override
		public boolean callBack(Object t, Object q) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Test
	public void testCallback() {
		ExampleCallback t = new ExampleCallback();

		AssertJUnit.assertTrue(t.callBack("asdf"));
		AssertJUnit.assertFalse(t.callBack(null));
	}
}
