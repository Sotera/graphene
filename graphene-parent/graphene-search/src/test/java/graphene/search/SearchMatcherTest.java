package graphene.search;

import graphene.search.SearchMatcher;
import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class SearchMatcherTest extends Assert {
	private static Logger l = LoggerFactory.getLogger(SearchMatcherTest.class);

	@Test
	public void doSearchMatcherTest() {
		SearchMatcher sm;

		sm = new SearchMatcher(
				"+foo -bar something nothing \"two words\" *olf olf*", false);

		AssertJUnit.assertEquals(sm.calcScore("The quick brown fox"), -1);
		AssertJUnit.assertEquals(sm.calcScore("foo bar"), -1);
		AssertJUnit.assertEquals(sm.calcScore("foo something"), 2);
		AssertJUnit.assertEquals(sm.calcScore("foo two words"), 1);
		AssertJUnit.assertEquals(sm.calcScore("foo golf"), 2);
		AssertJUnit.assertEquals(sm.calcScore("foo olfer"), 2);
		AssertJUnit.assertEquals(sm.calcScore("foo olfer bar"), -1);

	}

	@Test
	public void testRegularSearch() {
		SearchMatcher sm;

		sm = new SearchMatcher("candy store", false);
		l.debug(sm.getSearch().toString());

	}
}
