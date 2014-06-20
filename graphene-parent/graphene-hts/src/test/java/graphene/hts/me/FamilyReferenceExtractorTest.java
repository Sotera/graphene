package graphene.hts.me;

import graphene.hts.me.FamilyReferenceExtractor;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FamilyReferenceExtractorTest {

	private FamilyReferenceExtractor f;

	@BeforeClass
	public void setup() {
		f = new FamilyReferenceExtractor();
	}

	// FIXME: this had been working in the past. Check the regexes.
	// @Test
	public void findParentsTest() {

		Assert.assertEquals(
				f.getParent("S/ O WILL I AM RIKER , KLINGON HOLDING CELL, DUSTY PLANET, "),
				"WILL I AM RIKER ");
		Assert.assertEquals(
				f.getParent("S / O SPOCK, ZABUL TERRAGUN, SMALL VILLAGE NEAR SOLACE VIEW, "),
				"SPOCK");
		Assert.assertEquals(
				f.getParent("S/OJAMES T. KIRK, STARSHIP Enterprise, Delta Quadrant"),
				"JAMES T. KIRK");
		Assert.assertEquals(
				f.getParent("asdfS/ODavid Regex, Super Chicken Street, Sometown Malaysia"),
				"David Regex");
		Assert.assertEquals(f.getParent("S/O Sponge Bob"), "Sponge Bob");
	}

	@Test
	public void cleanNamesTest() {
		Assert.assertEquals(f.cleanName("DAVID Regex"), "DAVID Regex");
		Assert.assertEquals(f.cleanName("DAVID Reg Ex"), "DAVID Reg Ex");
		Assert.assertEquals(f.cleanName("DAVID      Reg        Ex"),
				"DAVID Reg Ex");

		Assert.assertEquals(f.cleanName("  DAVID Regex"), "DAVID Regex");
		Assert.assertEquals(f.cleanName("  DAVID Reg Ex"), "DAVID Reg Ex");
		Assert.assertEquals(f.cleanName(" DAVID      Reg        Ex"),
				"DAVID Reg Ex");
	}

}
