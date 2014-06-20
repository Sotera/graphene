package graphene.util;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class FastNumberUtilsTest {
	/**
	 * Increase these numbers to perform less tests, decrease to perform more tests.
	 */
	private static final int INTSTOCHASTICITY = 20000000;
	private static final long LONGSTOCHASTICITY = 20000000000000000l;

	@BeforeSuite
	public void beforeSuite() {
	}

	@Test
	public void parseIntWithCheckString() {
		// Edge cases
		AssertJUnit.assertEquals(0, FastNumberUtils.parseIntWithCheck("0"));
		AssertJUnit.assertEquals(0, FastNumberUtils.parseIntWithCheck("-0"));
		AssertJUnit.assertEquals(Integer.MAX_VALUE,
				FastNumberUtils.parseIntWithCheck("" + Integer.MAX_VALUE));
		AssertJUnit.assertEquals(Integer.MIN_VALUE,
				FastNumberUtils.parseIntWithCheck("" + Integer.MIN_VALUE));

		// Cases we expect to throw exceptions
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("Bob"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("Bob"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("-Nancy"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("1234Bob"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("-1234Nancy"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0, FastNumberUtils.parseIntWithCheck("-"));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0, FastNumberUtils.parseIntWithCheck("+"));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0, FastNumberUtils.parseIntWithCheck(""));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		try {
			AssertJUnit
					.assertEquals(0, FastNumberUtils.parseIntWithCheck(null));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			// tricky
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("+1234"));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseIntWithCheck("1.1234"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		// Random testing
		for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i = (int) (i + (INTSTOCHASTICITY * Math
				.random()))) {
			AssertJUnit.assertEquals(i,
					FastNumberUtils.parseIntWithCheck(Integer.toString(i)));
			System.out.println(i);
		}
	}

	@Test
	public void parseIntWithCheckStringint() {
		// Edge cases
		AssertJUnit
				.assertEquals(0, FastNumberUtils.parseIntWithCheck("0", 999));
		AssertJUnit.assertEquals(0,
				FastNumberUtils.parseIntWithCheck("-0", 999));
		AssertJUnit.assertEquals(Integer.MAX_VALUE,
				FastNumberUtils.parseIntWithCheck("" + Integer.MAX_VALUE, 999));
		AssertJUnit.assertEquals(Integer.MIN_VALUE,
				FastNumberUtils.parseIntWithCheck("" + Integer.MIN_VALUE, 999));

		// Cases we expect to return default
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("Bob", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("-Nancy", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("1234Bob", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("-1234Nancy", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("-", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("+", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck(null, 999));

		// tricky
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("+1234", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("1.1234", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("1234567891011121314151617181920", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseIntWithCheck("-1234567891011121314151617181920", 999));
		
		// Random testing
		for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i = (int) (i + (INTSTOCHASTICITY * Math
				.random()))) {
			AssertJUnit
					.assertEquals(
							i,
							FastNumberUtils.parseIntWithCheck(
									Integer.toString(i), 999));
			System.out.println(i);
		}
	}

	// @Test
	public void parseIntWithoutCheck() {

		throw new RuntimeException("Test not implemented");
	}

	@Test
	public void parseLongWithCheckString() {
		// Edge cases
		AssertJUnit.assertEquals(Long.MAX_VALUE,
				FastNumberUtils.parseLongWithCheck("" + Long.MAX_VALUE));
		AssertJUnit.assertEquals(Long.MIN_VALUE,
				FastNumberUtils.parseLongWithCheck("" + Long.MIN_VALUE));

		// Cases we expect to throw exceptions
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("Bob"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("Bob"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("-Nancy"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("1234Bob"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("-1234Nancy"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit
					.assertEquals(0, FastNumberUtils.parseLongWithCheck("-"));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit
					.assertEquals(0, FastNumberUtils.parseLongWithCheck("+"));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			AssertJUnit.assertEquals(0, FastNumberUtils.parseLongWithCheck(""));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck(null));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}
		try {
			// tricky
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("+1234"));

			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		try {
			AssertJUnit.assertEquals(0,
					FastNumberUtils.parseLongWithCheck("1.1234"));
			AssertJUnit.fail("Exception not thrown");
		} catch (Exception e) {
			AssertJUnit.assertTrue(e instanceof NumberFormatException);
		}

		// Random testing
		for (long i = Long.MIN_VALUE; i < Long.MAX_VALUE; i = (long) (i + (LONGSTOCHASTICITY * Math
				.random()))) {
			AssertJUnit.assertEquals(i,
					FastNumberUtils.parseLongWithCheck(Long.toString(i)));
			System.out.println(i);
		}
	}

	@Test
	public void parseLongWithCheckStringlong() {
		// Edge cases
		AssertJUnit.assertEquals(0,
				FastNumberUtils.parseLongWithCheck("0", 999));
		AssertJUnit.assertEquals(0,
				FastNumberUtils.parseLongWithCheck("-0", 999));
		AssertJUnit.assertEquals(Long.MAX_VALUE,
				FastNumberUtils.parseLongWithCheck("" + Long.MAX_VALUE, 999));
		AssertJUnit.assertEquals(Long.MIN_VALUE,
				FastNumberUtils.parseLongWithCheck("" + Long.MIN_VALUE, 999));

		// Cases we expect to return default
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("Bob", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("-Nancy", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("1234Bob", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("-1234Nancy", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("-", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("+", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck(null, 999));

		// tricky
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("+1234", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("1.1234", 999));


		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck(""+Long.MAX_VALUE+"1", 999));
		AssertJUnit.assertEquals(999,
				FastNumberUtils.parseLongWithCheck("-"+Long.MAX_VALUE+"1", 999));
		
		// Random testing
		for (long i = Long.MIN_VALUE; i < Long.MAX_VALUE; i = (long) (i + (LONGSTOCHASTICITY * Math
				.random()))) {
			AssertJUnit.assertEquals(i,
					FastNumberUtils.parseLongWithCheck(Long.toString(i), 999));
			System.out.println(i);
		}
	}

	// @Test
	public void parseLongWithoutCheck() {
		throw new RuntimeException("Test not implemented");
	}
}
