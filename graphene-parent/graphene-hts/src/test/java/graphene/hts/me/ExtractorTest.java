package graphene.hts.me;

import graphene.hts.entityextraction.EmailExtractor;
import graphene.hts.entityextraction.Extractor;
import graphene.hts.entityextraction.USSSNExtractor;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class ExtractorTest {

	@Test
	public void testEmail() {
		Extractor e = new EmailExtractor();
		AssertJUnit.assertTrue(e.extract("bob@gmail.com").contains(
				"bob@gmail.com"));
		AssertJUnit.assertTrue(e.extract("asdfasdf bob@gmail.com asdfasdf")
				.contains("bob@gmail.com"));
		AssertJUnit.assertTrue(e.extract(
				"#@@$ bob.asdf@gmail.com 123q4erwqe5243t").contains(
				"bob.asdf@gmail.com"));

	}

	@Test
	public void testSSN() {
		Extractor e = new USSSNExtractor();
		System.out.println(e.extract("212-23-3123"));
//		AssertJUnit
//				.assertTrue(e.extract("212-23-3123").contains("212-23-3123"));

	}
}
