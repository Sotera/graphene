package graphene.search;

import junit.framework.Assert;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class SearchCriterionTest extends Assert {
	@Test
	public void doSearchCriterionTest() {
		SearchCriterion sc;

		sc = new SearchCriterion("+foo");
		AssertJUnit.assertEquals(sc.isStartsWith(), false);
		AssertJUnit.assertEquals(sc.isEndsWith(), false);
		AssertJUnit.assertEquals(sc.isContains(), false);
		AssertJUnit.assertEquals(sc.isMustHave(), true);
		AssertJUnit.assertEquals(sc.isMustNotHave(), false);
		AssertJUnit.assertEquals(sc.getStr(), "foo");
		AssertJUnit.assertEquals(sc.match("foo"), true);
		AssertJUnit.assertEquals(sc.match("bar"), false);

		sc = new SearchCriterion("+*foo");
		AssertJUnit.assertEquals(sc.isStartsWith(), false);
		AssertJUnit.assertEquals(sc.isEndsWith(), true);
		AssertJUnit.assertEquals(sc.isContains(), false);
		AssertJUnit.assertEquals(sc.isMustHave(), true);
		AssertJUnit.assertEquals(sc.isMustNotHave(), false);
		AssertJUnit.assertEquals(sc.getStr(), "foo");
		AssertJUnit.assertEquals(sc.match("foo"), true);
		AssertJUnit.assertEquals(sc.match("myfoo"), true);
		AssertJUnit.assertEquals(sc.match("bar"), false);

		sc = new SearchCriterion("-foo*");
		AssertJUnit.assertEquals(sc.isStartsWith(), true);
		AssertJUnit.assertEquals(sc.isEndsWith(), false);
		AssertJUnit.assertEquals(sc.isContains(), false);
		AssertJUnit.assertEquals(sc.isMustHave(), false);
		AssertJUnit.assertEquals(sc.isMustNotHave(), true);
		AssertJUnit.assertEquals(sc.getStr(), "foo");
		AssertJUnit.assertEquals(sc.match("foo"), true);
		AssertJUnit.assertEquals(sc.match("myfoo"), false);
		AssertJUnit.assertEquals(sc.match("foobar"), true);
	}

}
