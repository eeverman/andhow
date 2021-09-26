package org.yarnandtail.andhow.property;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Name;
import org.yarnandtail.andhow.valuetype.FlagType;

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
	
	@Test
	public void testAliasesSlashCharactersNotAllowed() {
		TestBuilder builder = new TestBuilder();

		assertThrows(AppFatalException.class, () ->
			builder.aliasInAndOut("a/path/looking/one")
		);
	}
	
	@Test
	public void testAliasesQuoteCharactersNotAllowed() {
		TestBuilder builder = new TestBuilder();

		assertThrows(AppFatalException.class, () ->
			builder.aliasInAndOut("\"NoQuotes\"")
		);
	}
	
	@Test
	public void testAliasesSpaceCharactersNotAllowed() {
		TestBuilder builder = new TestBuilder();

		assertThrows(AppFatalException.class, () ->
			builder.aliasInAndOut("  NoSpaces")
		);
	}
	
	@Test
	public void testAliasesNullNotAllowed() {
		TestBuilder builder = new TestBuilder();

		assertThrows(AppFatalException.class, () ->
			builder.aliasInAndOut(null)
		);
	}

	@Test
	public void setNotNullTest() {
		TestBuilder builder = new TestBuilder();
		assertFalse(builder._nonNull);

		builder.mustBeNonNull();

		assertTrue(builder._nonNull);
	}
}
