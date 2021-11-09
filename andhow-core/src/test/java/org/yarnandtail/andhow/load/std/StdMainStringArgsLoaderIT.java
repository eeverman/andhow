package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHowTestConfig;
import org.yarnandtail.andhow.load.BaseForLoaderTests;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.test.bulktest.*;
import org.yarnandtail.andhow.test.props.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the KeyValuePairLoader as the only loader in a completely configured
 * AndHowCore.
 * <p>
 * This is close to how AndHow runs in production, except:
 * * No auto-discovery / registry of Properties
 * * No auto-discovery of AndHowInit
 */
public class StdMainStringArgsLoaderIT extends BaseForLoaderTests {

	AndHowTestConfig.AndHowTestConfigImpl config;
	PropValueLoader propValueLoader;

	@BeforeEach
	public void init() throws Exception {
		config = AndHowTestConfig.instance();
		config.setStandardLoaders(StdMainStringArgsLoader.class)
				.addOverrideGroup(StrPropProps.class).addOverrideGroup(FlagPropProps.class)
				.addOverrideGroup(IntPropProps.class);

		propValueLoader = new StdMainStringValueLoader();
	}

	@Test
	public void testTest() {
		config.setCmdLineArgs(new String[]{"foo=bar"});

		assertEquals(1, config.buildLoaders().size());
		assertTrue(config.buildLoaders().get(0) instanceof StdMainStringArgsLoader);
		assertTrue(config.getNamingStrategy() instanceof CaseInsensitiveNaming);
		assertEquals(3, config.getRegisteredGroups().size());
	}


	@Test
	public void anUnrecognizedPropertyShouldNotCauseAProblem() throws Exception {

		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();
		PropExpectations intExpect = IntPropProps.buildExpectations1();


		List<Object> extraArgs = new ArrayList<>(1);
		extraArgs.add("foo=bar");	// Unknown argument

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, extraArgs,
				strExpect, flagExpect, intExpect);
		coord.runForValues(true, true);

	}

	@Test
	public void assertUnsetNonFlagsCauseProblems() throws Exception {

		PropExpectations strExpect = StrPropProps.buildExpectationsUnset();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();
		PropExpectations intExpect = IntPropProps.buildExpectationsUnset();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForProblems(false, false);
	}

	@Test
	public void assertUnsetFlagsCauseNoProblems() throws Exception {

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectationsUnset();	// <- only one unset
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(true, false);
	}

	@Test
	public void assertValues1() throws Exception {

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(false, false);
	}

	@Test
	public void assertValues1WithAliases() throws Exception {

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(true, false);
	}

	@Test
	public void assertValues2() throws Exception {

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations2();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(false, false);
	}

	@Test
	public void assertValues2WithAliases() throws Exception {
		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations2();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(true, false);
	}

	@Test
	public void assertValue3() throws Exception {
		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations3();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(false, false);
	}

	@Test
	public void assertValue3WithAliases() throws Exception {
		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations3();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(true, false);
	}

	@Test
	public void assertValues4() throws Exception {
		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations4();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(false, false);
	}

	@Test
	public void assertValues4WithAliases() throws Exception {
		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations4();
		PropExpectations intExpect = IntPropProps.buildExpectations1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForValues(true, false);
	}

	@Test
	public void assertInvalidValues1() throws Exception {
		StdMainStringValueLoader loader = new StdMainStringValueLoader();

		PropExpectations strExpect = StrPropProps.buildInvalid1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1(); // Flags cannot have invalid vals
		PropExpectations intExpect = IntPropProps.buildInvalid1();

		TestCoordinator coord = new TestCoordinator(config, propValueLoader, strExpect, flagExpect, intExpect);
		coord.runForProblems(true, false);
	}

}
