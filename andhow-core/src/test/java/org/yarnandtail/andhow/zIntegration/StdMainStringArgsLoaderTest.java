package org.yarnandtail.andhow.zIntegration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHowTestConfig;
import org.yarnandtail.andhow.BaseConfig;
import org.yarnandtail.andhow.api.ValidatedValues;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.load.BaseForLoaderTests;
import org.yarnandtail.andhow.load.std.StdMainStringArgsLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.zTestGroups.*;

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
public class StdMainStringArgsLoaderTest extends BaseForLoaderTests {

	protected static final String STRPROP_BASE = StrPropProps.class.getCanonicalName() + ".";
	protected static final String FLAGPROP_BASE = FlagPropProps.class.getCanonicalName() + ".";

	AndHowTestConfig.AndHowTestConfigImpl config;

	@BeforeEach
	public void init() throws Exception {
		config = AndHowTestConfig.instance();
		config.setStandardLoaders(StdMainStringArgsLoader.class)
				.addOverrideGroup(StrPropProps.class).addOverrideGroup(FlagPropProps.class);
	}

	protected AndHowCore buildCore(BaseConfig aConfig) {
		AndHowCore core = new AndHowCore(
				aConfig.getNamingStrategy(),
				aConfig.buildLoaders(),
				aConfig.getRegisteredGroups());

		return core;
	}

	@Test
	public void testTest() {
		config.setCmdLineArgs(new String[]{"foo=bar"});

		assertEquals(1, config.buildLoaders().size());
		assertTrue(config.buildLoaders().get(0) instanceof StdMainStringArgsLoader);
		assertTrue(config.getNamingStrategy() instanceof CaseInsensitiveNaming);
		assertEquals(2, config.getRegisteredGroups().size());
	}


	@Test
	public void anUnrecognizedPropertyShouldNotCauseAProblem() throws Exception {

		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, true, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, true, false) );
		args.add("foo=bar");	// Unknown argument


		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertUnsetStringsCauseProblems() throws Exception {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectationsUnset();
		PropExpectations flagExpect = FlagPropProps.buildExpectationsUnset();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, false, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, false, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		PropProblemAssertions ppa = new PropProblemAssertions(config, 0, true, strExpect, flagExpect);
		ppa.assertErrors(false);
	}

	@Test
	public void assertUnsetFlagsCauseNoProblems() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectationsUnset();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, true, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, true, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValues1() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, false, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, false, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValues1WithAliases() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations1();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, true, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, true, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValues2() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations2();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, false, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, false, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValues2WithAliases() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations2();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, true, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, true, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValue3() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations3();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, false, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, false, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValue3WithAliases() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations3();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, true, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, true, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValues4() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations4();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, false, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, false, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

	@Test
	public void assertValues4WithAliases() throws IllegalAccessException {
		StdMainStringPropLoad loader = new StdMainStringPropLoad();

		PropExpectations strExpect = StrPropProps.buildExpectations1();
		PropExpectations flagExpect = FlagPropProps.buildExpectations4();

		List<String> args = loader.buildSources(StrPropProps.class, strExpect, 0, true, false);
		args.addAll( loader.buildSources(FlagPropProps.class, flagExpect, 0, true, false) );

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		PropValueAssertions pas = new PropValueAssertions(core, core, 0, true, strExpect, flagExpect);

		pas.assertAll(false);
	}

}
