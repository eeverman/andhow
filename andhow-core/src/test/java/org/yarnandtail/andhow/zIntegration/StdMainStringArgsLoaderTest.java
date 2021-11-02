package org.yarnandtail.andhow.zIntegration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.load.BaseForLoaderTests;
import org.yarnandtail.andhow.load.std.StdMainStringArgsLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.zTestGroups.StrPropProps;
import org.yarnandtail.andhow.zTestGroups.FlagPropProps;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static com.github.stefanbirkner.systemlambda.SystemLambda.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the KeyValuePairLoader as the only loader in a completely configured
 * AndHowCore.
 *
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

	protected void printValues(final ValidatedValues result, final Class<?> clazz) throws IllegalAccessException {
		List<NameAndProperty> naps = AndHowUtil.getProperties(clazz);

		for (NameAndProperty nap : naps) {
			Object val = result.getExplicitValue(nap.property);

			if (val != null) {
				String strVal = val.toString();
				strVal = strVal.replace("\"", "\\\"");
				strVal = strVal.replace("\t", "\\t");
				strVal = strVal.replace("\n", "\\n");

				System.out.println("assertEquals(\"" + strVal +
						"\", result.getExplicitValue(" + clazz.getSimpleName() + "." + nap.fieldName + "));");

			} else {
				System.out.println("assertNull(" +
						"result.getExplicitValue(" + clazz.getSimpleName() + "." + nap.fieldName + "));");

			}

		}
	}

	@Test
	public void testTest() {
		config.setCmdLineArgs(new String[] {"foo=bar"});

		assertEquals(1, config.buildLoaders().size());
		assertTrue(config.buildLoaders().get(0) instanceof StdMainStringArgsLoader);
		assertTrue(config.getNamingStrategy() instanceof CaseInsensitiveNaming);
		assertEquals(2, config.getRegisteredGroups().size());
	}

	@Test
	public void settingNoValuesShouldResultInNonNullPropertyProblems() throws Exception {

		//Trick to allow access w/in the lambda
		final AppFatalException[] afeArray = new AppFatalException[1];

		// This should have Problems from missing values and an unknown property
		String outText = tapSystemErr(() -> {
			afeArray[0] = assertThrows(AppFatalException.class,
					() -> buildCore(config));
		});

		AppFatalException afe = afeArray[0];

		// Five Props will be invalid if nothing is set
		assertEquals(5, afe.getProblems().size());
		assertThat(
				afe.getProblems().filter(RequirementProblem.NonNullPropertyProblem.class).stream()
						.map(p -> p.getPropertyCoord().getProperty()).collect(Collectors.toList()),
				containsInAnyOrder(StrPropProps.PROP_100, StrPropProps.PROP_120, StrPropProps.PROP_121,
						StrPropProps.PROP_122, StrPropProps.PROP_123));

		// Are the 5 properties w/ missing values mentioned in the output?
		assertTrue(outText.contains(STRPROP_BASE + "PROP_100"));
		assertTrue(outText.contains(STRPROP_BASE + "PROP_120"));
		assertTrue(outText.contains(STRPROP_BASE + "PROP_121"));
		assertTrue(outText.contains(STRPROP_BASE + "PROP_122"));
		assertTrue(outText.contains(STRPROP_BASE + "PROP_123"));

	}

	@Test
	public void setEveryValue1() throws IllegalAccessException {
		List<String> args = buildEveryStrPropValue1();
		args.addAll(buildEveryFlagPropValue1());
		//
		config.setCmdLineArgs(args.toArray(new String[args.size()]));
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);
		assertEveryFlagPropValue1(core);
	}

	@Test
	public void setEveryValue2() throws IllegalAccessException {
		List<String> args = buildEveryStrPropValue1();
		args.addAll(buildEveryFlagPropValue2());
		//
		config.setCmdLineArgs(args.toArray(new String[args.size()]));
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);
		assertEveryFlagPropValue2(core);
	}

	@Test
	public void setEveryValue3() throws IllegalAccessException {
		List<String> args = buildEveryStrPropValue1();
		args.addAll(buildEveryFlagPropValue3());
		//
		config.setCmdLineArgs(args.toArray(new String[args.size()]));
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);
		assertEveryFlagPropValue3(core);
	}

	@Test
	public void setEveryValue4() throws IllegalAccessException {
		List<String> args = buildEveryStrPropValue1();
		args.addAll(buildEveryFlagPropValue4());
		//
		config.setCmdLineArgs(args.toArray(new String[args.size()]));
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);
		assertEveryFlagPropValue4(core);
	}

	@Test
	public void setEveryValue5() throws IllegalAccessException {
		List<String> args = buildEveryStrPropValue1();
		args.addAll(buildEveryFlagPropValue5());
		//
		config.setCmdLineArgs(args.toArray(new String[args.size()]));
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);
		assertEveryFlagPropValue5(core);
	}

	@Test
	public void setNoFlags() throws IllegalAccessException {
		List<String> args = buildEveryStrPropValue1();

		//
		config.setCmdLineArgs(args.toArray(new String[args.size()]));
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);
		assertFlagDefaultValues(core);
	}

	@Test
	public void anUnrecognizedPropertyShouldNotCauseAProblem() throws Exception {

		List<String> args = buildEveryStrPropValue1();
		args.add("foo=bar");

		config.setCmdLineArgs(args.toArray(new String[args.size()]));

		// If there were problems, an exception would be thrown
		AndHowCore core = buildCore(config);

		//printValues(core, StrPropProps.class);
		assertEveryStrPropValue1(core);

	}

		public List<String> buildEveryStrPropValue1() {
		List<String> args = new ArrayList();

		// Null OK | No Default | No Validations
		args.add(STRPROP_BASE + "PROP_0" + "=" + "  two \nwords  ");
		// Null OK | Has Default | No Validations
		args.add(STRPROP_BASE + "PROP_10" + "=" + "  \" words in space \"  ");

		//
		// Null OK | No Default | Has Validations
		args.add("StrPropProps.PROP_20" + "=" + "  StaR\tT ing  ");
		args.add(STRPROP_BASE + "PROP_21" + "=" + "\t\"star\tInG\"\t");
		args.add(STRPROP_BASE + "PROP_22" + "=" + " \" \"a b\" \" ");
		args.add(STRPROP_BASE + "PROP_23" + "=" + " \" \"A B\" \" ");

		//
		// Null OK | Has Default | Has Validations
		args.add("StrPropProps.PROP_30" + "=" + "  \tStaRs star inging\t  ");
		args.add(STRPROP_BASE + "PROP_31" + "=" + "\t  \"star star iNgiNG\"  \t");
		args.add(STRPROP_BASE + "PROP_32" + "=" + " \" \"a b\" \" ");
		args.add(STRPROP_BASE + "PROP_33" + "=" + " \" \"A b\" \" ");

		//
		// Not Null

		// Not Null | No Default | No Validations
		args.add("StrPropProps.PROP_100" + "=" + " \" \" ");
		// Not Null | Has Default | No Validations
		args.add(STRPROP_BASE + "PROP_110" + "=" + " \"\" ");

		//
		// Not Null | No Default | Has Validations
		args.add("StrPropProps.PROP_120" + "=" + "\t  StaR\tstar\ting  \t");
		args.add(STRPROP_BASE + "PROP_121" + "=" + "\"star\tstar\tiNG\"");
		args.add(STRPROP_BASE + "PROP_122" + "=" + "\t\b\n\r\f\" \"a b\" \"\t\b\n\r\f");
		args.add(STRPROP_BASE + "PROP_123" + "=" + " \t\b\n\r\f \" \"a B\" \" \t\b\n\r\f ");


		//
		// Not Null | Has Default | Has Validations
		args.add("StrPropProps.PROP_130" + "=" + "\t\b\n\r\f StaRs star inging \t\b\n\r\f ");
		args.add(STRPROP_BASE + "PROP_131" + "=" + " \t\b\n\r\f star star iNgiNG \t\b\n\r\f ");
		args.add(STRPROP_BASE + "PROP_132" + "=" + " \" \"a b\" \" ");
		args.add(STRPROP_BASE + "PROP_133" + "=" + " \" \"A b\" \" ");

		//
		// Special Trimmers and Types
		args.add("StrPropProps.PROP_200" + "=" + "  \" space_n_quotes \" ");
		args.add(STRPROP_BASE + "PROP_210" + "=" + " \" upperCaseMe \" ");

		return args;
	}


	public void assertEveryStrPropValue1(ValidatedValues result) {
		assertEquals("two \nwords", result.getExplicitValue(StrPropProps.PROP_0));
		assertEquals(" words in space ", result.getExplicitValue(StrPropProps.PROP_10));

		assertEquals("StaR\tT ing", result.getExplicitValue(StrPropProps.PROP_20));
		assertEquals("star\tInG", result.getExplicitValue(StrPropProps.PROP_21));
		assertEquals(" \"a b\" ", result.getExplicitValue(StrPropProps.PROP_22));
		assertEquals(" \"A B\" ", result.getExplicitValue(StrPropProps.PROP_23));
		assertEquals("StaRs star inging", result.getExplicitValue(StrPropProps.PROP_30));
		assertEquals("star star iNgiNG", result.getExplicitValue(StrPropProps.PROP_31));
		assertEquals(" \"a b\" ", result.getExplicitValue(StrPropProps.PROP_32));
		assertEquals(" \"A b\" ", result.getExplicitValue(StrPropProps.PROP_33));
		assertEquals(" ", result.getExplicitValue(StrPropProps.PROP_100));
		assertEquals("", result.getExplicitValue(StrPropProps.PROP_110));

		assertEquals("StaR\tstar\ting", result.getExplicitValue(StrPropProps.PROP_120));
		assertEquals("star\tstar\tiNG", result.getExplicitValue(StrPropProps.PROP_121));
		assertEquals(" \"a b\" ", result.getExplicitValue(StrPropProps.PROP_122));
		assertEquals(" \"a B\" ", result.getExplicitValue(StrPropProps.PROP_123));
		assertEquals("StaRs star inging", result.getExplicitValue(StrPropProps.PROP_130));
		assertEquals("star star iNgiNG", result.getExplicitValue(StrPropProps.PROP_131));
		assertEquals(" \"a b\" ", result.getExplicitValue(StrPropProps.PROP_132));
		assertEquals(" \"A b\" ", result.getExplicitValue(StrPropProps.PROP_133));
		assertEquals("\" space_n_quotes \"", result.getExplicitValue(StrPropProps.PROP_200));
		assertEquals(" UPPERCASEME ", result.getExplicitValue(StrPropProps.PROP_210));
	}



	public List<String> buildEveryFlagPropValue1() {
		List<String> args = new ArrayList();

		// Not Null | No Default | No Validations
		args.add("FlagPropProps.PROP_100");

		// Not Null | Has Default | No Validations
		args.add(FLAGPROP_BASE + "PROP_110");
		args.add(FLAGPROP_BASE + "PROP_111");

		// Special type is true on 'X' only (though should still be true if present)
		args.add(FLAGPROP_BASE + "PROP_200");

		return args;
	}

	public void assertEveryFlagPropValue1(ValidatedValues result) {
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_100));
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_110));
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_111));
		assertEquals(false, result.getExplicitValue(FlagPropProps.PROP_200));
	}

	public List<String> buildEveryFlagPropValue2() {
		List<String> args = new ArrayList();

		// Not Null | No Default | No Validations
		args.add("FlagPropProps.PROP_100=");

		// Not Null | Has Default | No Validations
		args.add(FLAGPROP_BASE + "PROP_110=");
		args.add(FLAGPROP_BASE + "PROP_111=");

		// Special type is true on 'X' only (though should still be true if present)
		args.add(FLAGPROP_BASE + "PROP_200=");

		return args;
	}

	public void assertEveryFlagPropValue2(ValidatedValues result) {
		assertEveryFlagPropValue1(result);
	}

	public List<String> buildEveryFlagPropValue3() {
		List<String> args = new ArrayList();

		// Not Null | No Default | No Validations
		args.add("FlagPropProps.PROP_100= \t\b\n\r\f ");

		// Not Null | Has Default | No Validations
		args.add(FLAGPROP_BASE + "PROP_110=\t\b\n\r\f ");
		args.add(FLAGPROP_BASE + "PROP_111= \t\b\n\r\f");

		// Special type is true on 'X' only (though should still be true if present)
		args.add(FLAGPROP_BASE + "PROP_200= \t\b\n\r\f\t\b\n\r\f ");

		return args;
	}

	public void assertEveryFlagPropValue3(ValidatedValues result) {
		assertEveryFlagPropValue1(result);
	}

	public List<String> buildEveryFlagPropValue4() {
		List<String> args = new ArrayList();

		// Not Null | No Default | No Validations
		args.add("FlagPropProps.PROP_100" + "=" + " true ");

		// Not Null | Has Default | No Validations
		args.add(FLAGPROP_BASE + "PROP_110" + "=" + " yes ");
		args.add(FLAGPROP_BASE + "PROP_111" + "=" + " y ");

		// Special type is true on 'X' only (though should still be true if present)
		args.add(FLAGPROP_BASE + "PROP_200" + "=" + " X ");

		return args;
	}

	public void assertEveryFlagPropValue4(ValidatedValues result) {
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_100));
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_110));
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_111));
		assertEquals(true, result.getExplicitValue(FlagPropProps.PROP_200));
	}

	public List<String> buildEveryFlagPropValue5() {
		List<String> args = new ArrayList();

		// Not Null | No Default | No Validations
		args.add("FlagPropProps.PROP_100" + "=" + " false ");

		// Not Null | Has Default | No Validations
		args.add(FLAGPROP_BASE + "PROP_110" + "=" + " xxx ");
		args.add(FLAGPROP_BASE + "PROP_111" + "=" + " 1234.5725\" ");

		// Special type is true on 'X' only (though should still be true if present)
		args.add(FLAGPROP_BASE + "PROP_200" + "=" + " O ");

		return args;
	}

	public void assertEveryFlagPropValue5(ValidatedValues result) {
		assertEquals(false, result.getExplicitValue(FlagPropProps.PROP_100));
		assertEquals(false, result.getExplicitValue(FlagPropProps.PROP_110));
		assertEquals(false, result.getExplicitValue(FlagPropProps.PROP_111));
		assertEquals(false, result.getExplicitValue(FlagPropProps.PROP_200));
	}

	public void assertFlagDefaultValues(ValidatedValues result) {
		assertEquals(false, result.getValue(FlagPropProps.PROP_100));
		assertEquals(true, result.getValue(FlagPropProps.PROP_110));
		assertEquals(false, result.getValue(FlagPropProps.PROP_111));
		assertEquals(false, result.getValue(FlagPropProps.PROP_200));
	}

}
