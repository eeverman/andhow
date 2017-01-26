package yarnandtail.andhow.property;

import org.junit.Test;
import yarnandtail.andhow.*;

import static org.junit.Assert.*;

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
		Name mike = builder._aliases.stream().filter(a -> a.getActualName().equals("Mike")).findFirst().get();
		Name bob = builder._aliases.stream().filter(a -> a.getActualName().equals("Bob")).findFirst().get();
		Name carl = builder._aliases.stream().filter(a -> a.getActualName().equals("Carl")).findFirst().get();
		Name sally = builder._aliases.stream().filter(a -> a.getActualName().equals("Sally")).findFirst().get();
		Name kathy = builder._aliases.stream().filter(a -> a.getActualName().equals("Kathy")).findFirst().get();
		
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
