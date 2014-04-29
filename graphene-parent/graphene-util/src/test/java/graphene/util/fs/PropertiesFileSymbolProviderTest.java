package graphene.util.fs;

import graphene.util.TestUtilModule;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class PropertiesFileSymbolProviderTest {
	protected Logger logger;

	protected Registry registry;

	private SymbolSource s;

	@BeforeSuite
	public void beforeSuite() {

		RegistryBuilder builder = new RegistryBuilder();
		builder.add(TestUtilModule.class);

		registry = builder.build();
		registry.performRegistryStartup();
		s = registry.getService(SymbolSource.class);
		logger = registry.getService(Logger.class);
	}

	@Test
	public void f1() {
		AssertJUnit.assertEquals("bar1", s.valueForSymbol("foo1"));
		AssertJUnit.assertEquals("bar2", s.valueForSymbol("foo2"));
		AssertJUnit.assertEquals("bar3", s.valueForSymbol("foo3"));
		AssertJUnit.assertEquals("bar4", s.valueForSymbol("foo4"));
	}

	@Test
	public void f2() {
		logger.debug(s.expandSymbols("${foo1}"));
	}

	@Test
	public void f3() {
		AssertJUnit.assertEquals("red", s.valueForSymbol("apple"));
		AssertJUnit.assertEquals("orange", s.valueForSymbol("orange"));
		AssertJUnit.assertEquals("green", s.valueForSymbol("pear"));
		AssertJUnit.assertEquals("yellow", s.valueForSymbol("banana"));
	}

	@Test
	public void f4() {
		boolean passed = false;
		try {
			s.valueForSymbol("car");

		} catch (Exception e) {
			passed = true;
		}
		AssertJUnit.assertTrue(passed);
	}
}
