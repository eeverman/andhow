package yarnandtail.andhow.property;

import org.junit.Test;
import static org.junit.Assert.*;
import yarnandtail.andhow.Alias;
import yarnandtail.andhow.AppFatalException;
import yarnandtail.andhow.valuetype.FlagType;

/**
 *
 * @author ericeverman
 */
public class PropertyBuilderBaseTest {
	

	public static class TestBuilder extends PropertyBuilderBase<TestBuilder, FlagProp, Boolean> {

		public TestBuilder () {
			instance = this;
			valueType(FlagType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public FlagProp build() {
			return null;
		}
	}
	
	@Test
	public void testAliasesHappyPath() {
		TestBuilder builder = new TestBuilder();
		
		builder.aliasInAndOut("Mike");
		builder.aliasIn("Bob");
		builder.aliasIn("Carl");
		builder.aliasOut("Sally");
		builder.aliasOut("Bob");
		builder.aliasInAndOut("Kathy");
		
		assertEquals(5, builder._aliases.size());
		Alias mike = builder._aliases.stream().filter(a -> a.getName().equals("Mike")).findFirst().get();
		Alias bob = builder._aliases.stream().filter(a -> a.getName().equals("Bob")).findFirst().get();
		Alias carl = builder._aliases.stream().filter(a -> a.getName().equals("Carl")).findFirst().get();
		Alias sally = builder._aliases.stream().filter(a -> a.getName().equals("Sally")).findFirst().get();
		Alias kathy = builder._aliases.stream().filter(a -> a.getName().equals("Kathy")).findFirst().get();
		
		assertTrue(mike.isIn() && mike.isOut());
		assertTrue(bob.isIn() && bob.isOut());
		assertTrue(carl.isIn() && ! carl.isOut());
		assertTrue(! sally.isIn() && sally.isOut());
		assertTrue(kathy.isIn() && kathy.isOut());
	}
	
	@Test(expected=AppFatalException.class)
	public void testAliasesSlashCharactersNotAllowed() {
		TestBuilder builder = new TestBuilder();
		
		builder.aliasInAndOut("a/path/looking/one");
	}
	
	@Test(expected=AppFatalException.class)
	public void testAliasesQuoteCharactersNotAllowed() {
		TestBuilder builder = new TestBuilder();
		
		builder.aliasInAndOut("\"NoQuotes\"");
	}
	
	@Test(expected=AppFatalException.class)
	public void testAliasesSpaceCharactersNotAllowed() {
		TestBuilder builder = new TestBuilder();
		
		builder.aliasInAndOut("  NoSpaces");
	}
	
	@Test(expected=AppFatalException.class)
	public void testAliasesNullNotAllowed() {
		TestBuilder builder = new TestBuilder();
		
		builder.aliasInAndOut(null);
	}
	
}
