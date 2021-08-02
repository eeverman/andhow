package org.yarnandtail.andhow;

import org.junit.jupiter.api.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;
import static org.yarnandtail.andhow.load.KeyValuePairLoader.KVP_DELIMITER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author eeverman
 */
public class StdConfigSimulatedAppTest extends AndHowTestBase {

	private static final String GROUP_PATH = "org.yarnandtail.andhow.StdConfigSimulatedAppTest.SampleRestClientGroup";
	private static final String CLASSPATH_BEGINNING = "/org/yarnandtail/andhow/StdConfigSimulatedAppTest.";

	
	@Test
	public void testAllValuesAreSetViaCmdLineArgAndPropFile() {

		//Prop name case ignored
		String[] cmdLineArgs = new String[] {
			GROUP_PATH + ".classpath_prop_file" + KVP_DELIMITER + CLASSPATH_BEGINNING + "all.props.speced.properties"
		};
		
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.setCmdLineArgs(cmdLineArgs)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();
		
		AndHow.setConfig(config);
		
		assertEquals("/org/yarnandtail/andhow/StdConfigSimulatedAppTest.all.props.speced.properties",
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertAllPointsSpecedValues("testAllValuesAreSetViaCmdLineArgAndPropFile");
	}

	@Test
	public void testAllValuesAreSetViaFixedPropertyValueAndPropFile() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.addFixedValue(SampleRestClientGroup.CLASSPATH_PROP_FILE,
						CLASSPATH_BEGINNING + "all.props.speced.properties")	//set via FixedValue (PropertyValue)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		assertEquals("/org/yarnandtail/andhow/StdConfigSimulatedAppTest.all.props.speced.properties",
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertAllPointsSpecedValues("testAllValuesAreSetViaFixedPropertyValueAndPropFile");
	}

	@Test
	public void testAllValuesAreSetViaFixedPropertyValueAndPropFileOverridingWithFixedPropertyValue() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.addFixedValue(SampleRestClientGroup.CLASSPATH_PROP_FILE,
						CLASSPATH_BEGINNING + "all.props.speced.properties")	//set via FixedValue (PropertyValue)
				.addFixedValue(SampleRestClientGroup.REST_PORT, 99)	//Override value in prop file
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		assertEquals("  Big App  ", SampleRestClientGroup.APP_NAME.getValue());	//Just check one prop file val
		assertEquals(99, SampleRestClientGroup.REST_PORT.getValue(), "Fixed value should override prop file");
	}

	@Test
	public void testAllValuesAreSetViaFixedKeyObjectPairAndPropFile() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.addFixedValue(GROUP_PATH + ".classpath_prop_file", /* case ignored */
						CLASSPATH_BEGINNING + "all.props.speced.properties")	//set via FixedValue (KeyObjectPair)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		assertEquals("/org/yarnandtail/andhow/StdConfigSimulatedAppTest.all.props.speced.properties",
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertAllPointsSpecedValues("testAllValuesAreSetViaFixedKeyObjectPairAndPropFile");
	}

	@Test
	public void testAllValuesAreSetViaFixedKeyObjectPairAndPropFileOverridingWithFixedKeyObjectPair() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.addFixedValue(GROUP_PATH + ".classpath_prop_file", /* case ignored */
						CLASSPATH_BEGINNING + "all.props.speced.properties")	//set via FixedValue (KeyObjectPair)
				.addFixedValue(GROUP_PATH + ".REST_PORT", 98)	//Override value in prop file
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		assertEquals("  Big App  ", SampleRestClientGroup.APP_NAME.getValue());	//Just check one prop file val
		assertEquals(98, SampleRestClientGroup.REST_PORT.getValue(), "Fixed value should override prop file");
	}

	@Test
	public void testAllValuesAreSetViaEnvVarAndPropFile() {

		Map<String, String> envvars = new HashMap();
		envvars.put(
				GROUP_PATH + ".classpath_prop_file", /* case ignored */
				CLASSPATH_BEGINNING + "all.props.speced.properties"
		);

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.setEnvironmentProperties(envvars)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		assertEquals("/org/yarnandtail/andhow/StdConfigSimulatedAppTest.all.props.speced.properties",
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertAllPointsSpecedValues("testAllValuesAreSetViaEnvVarAndPropFile");
	}

	@Test
	public void testAllValuesAreSetViaSystemPropertiesAndPropFile() {

		Properties props = new Properties();
		props.put(
				GROUP_PATH + ".classpath_prop_file", /* case ignored */
				CLASSPATH_BEGINNING + "all.props.speced.properties"
		);

		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.setSystemProperties(props)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		assertEquals("/org/yarnandtail/andhow/StdConfigSimulatedAppTest.all.props.speced.properties",
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertAllPointsSpecedValues("testAllValuesAreSetViaSystemPropertiesAndPropFile");
	}

	/**
	 * Asserts each value to match the values spec'ed in the StdConfigSimulatedAppTest.all.props.speced.properties file.
	 */
	void assertAllPointsSpecedValues(String calledFromTestName) {
		String failDesc = "assertion failure in the test named '" + calledFromTestName + "'";

		assertEquals("  Big App  ", SampleRestClientGroup.APP_NAME.getValue(), failDesc);
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue(), failDesc);
		assertEquals(8080, SampleRestClientGroup.REST_PORT.getValue(), failDesc);
		assertEquals("doquery/", SampleRestClientGroup.REST_SERVICE_NAME.getValue(), failDesc);
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue(), failDesc);
		assertEquals(4, SampleRestClientGroup.RETRY_COUNT.getValue(), failDesc);
		assertFalse(SampleRestClientGroup.REQUEST_META_DATA.getValue(), failDesc);
		assertTrue(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue(), failDesc);
	}
	
	@Test
	public void testMinimumPropsAreSetViaCmdLineArgAndPropFile() {

		String[] cmdLineArgs = new String[] {
				GROUP_PATH + ".CLASSPATH_PROP_FILE" + KVP_DELIMITER + CLASSPATH_BEGINNING + "minimum.props.speced.properties"
		};		
				
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.setCmdLineArgs(cmdLineArgs)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);
		
		assertEquals("/org/yarnandtail/andhow/StdConfigSimulatedAppTest.minimum.props.speced.properties",
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue());
		assertEquals(8080, SampleRestClientGroup.REST_PORT.getValue());
		assertEquals("query/", SampleRestClientGroup.REST_SERVICE_NAME.getValue());	//a default value
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue());
		assertEquals(2, SampleRestClientGroup.RETRY_COUNT.getValue());	//a default
		assertTrue(SampleRestClientGroup.REQUEST_META_DATA.getValue());	//a default
		assertFalse(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue());	//a default

	}
	
	@Test
	public void testInvalidValuesViaCmdLineArgAndPropFile() throws Exception {
		
		SimpleNamingContextBuilder jndi = getJndi();
		jndi.activate();

		String[] cmdLineArgs = new String[] {
				GROUP_PATH + ".CLASSPATH_PROP_FILE" + KVP_DELIMITER + CLASSPATH_BEGINNING + "invalid.properties"
		};


		//Error expected b/c some values are invalid
		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SampleRestClientGroup.class)
				.setCmdLineArgs(cmdLineArgs)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired();

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		//Value Problems (validation)
		//Due to loading from a prop file, the order of the file is not preserved,
		//so we cannot know the order that problems were encountered.
		ArrayList<Property<?>> expectedProblemPoints = new ArrayList();
		expectedProblemPoints.add(SampleRestClientGroup.REST_HOST);
		expectedProblemPoints.add(SampleRestClientGroup.REST_PORT);
		expectedProblemPoints.add(SampleRestClientGroup.REST_SERVICE_NAME);

		assertEquals(3, e.getProblems().filter(ValueProblem.class).size());
		assertTrue(expectedProblemPoints.contains(e.getProblems().filter(ValueProblem.class).get(0).getBadValueCoord().getProperty()));
		assertTrue(expectedProblemPoints.contains(e.getProblems().filter(ValueProblem.class).get(1).getBadValueCoord().getProperty()));
		assertTrue(expectedProblemPoints.contains(e.getProblems().filter(ValueProblem.class).get(2).getBadValueCoord().getProperty()));

		//
		// Loader problems
		assertEquals(1, e.getProblems().filter(LoaderProblem.class).size());
		assertEquals(SampleRestClientGroup.RETRY_COUNT, e.getProblems().filter(LoaderProblem.class).get(0).getBadValueCoord().getProperty());

	}

	interface SampleRestClientGroup {
		StrProp CLASSPATH_PROP_FILE = StrProp.builder().desc("Classpath location of a properties file w/ props").build();
		StrProp APP_NAME = StrProp.builder().aliasIn("app.name").aliasIn("app_name").build();
		StrProp REST_HOST = StrProp.builder().mustMatchRegex(".*\\.usgs\\.gov") .mustBeNonNull().build();
		IntProp REST_PORT = IntProp.builder().mustBeNonNull().mustBeGreaterThanOrEqualTo(80).mustBeLessThan(10000).build();
		StrProp REST_SERVICE_NAME = StrProp.builder().defaultValue("query/").mustEndWith("/").build();
		StrProp AUTH_KEY = StrProp.builder().mustBeNonNull().build();
		IntProp RETRY_COUNT = IntProp.builder().defaultValue(2).build();
		FlagProp REQUEST_META_DATA = FlagProp.builder().defaultValue(true).build();
		FlagProp REQUEST_SUMMARY_DATA = FlagProp.builder().build();
	}

}
