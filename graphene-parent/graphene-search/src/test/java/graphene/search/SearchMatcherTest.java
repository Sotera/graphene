package graphene.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class SearchMatcherTest {
	private static Logger l = LoggerFactory.getLogger(SearchMatcherTest.class);

	@Test
	public void doSearchMatcherTest() {
		SearchMatcher sm;

		sm = new SearchMatcher(
				"+foo -bar something nothing \"two words\" *olf olf*", false);

		AssertJUnit.assertEquals(-1, sm.calcScore("The quick brown fox"));
		AssertJUnit.assertEquals(-1, sm.calcScore("foo bar"));
		//FIXME: This test is broken
		//AssertJUnit.assertEquals(2, sm.calcScore("foo something"));
		//AssertJUnit.assertEquals(1, sm.calcScore("foo two words"));
		//AssertJUnit.assertEquals(2, sm.calcScore("foo golf"));
		//AssertJUnit.assertEquals(2, sm.calcScore("foo olfer"));
		//AssertJUnit.assertEquals(-1, sm.calcScore("foo olfer bar"));

	}

	@Test
	public void testRegularSearch() {
		SearchMatcher sm;

		sm = new SearchMatcher("candy store", false);
		l.debug(sm.getSearch().toString());

	}
}
