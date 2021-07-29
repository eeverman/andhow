package org.yarnandtail.andhow;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.RequirementProblem;
import org.yarnandtail.andhow.load.KeyValuePairLoader;
import org.yarnandtail.andhow.property.*;

/**
 *
 * @author eeverman
 */
public class AndHowUsageExampleTest extends AndHowCoreTestBase {
	
	String uiFullPath = UI_CONFIG.class.getCanonicalName() + ".";
	String svsFullPath = SERVICE_CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgsWFullClassName = new String[0];

	
	@BeforeEach
	public void setup() {
		
		cmdLineArgsWFullClassName = new String[] {
			uiFullPath + "DISPLAY_NAME" + KeyValuePairLoader.KVP_DELIMITER + "My App",
			uiFullPath + "BACKGROUP_COLOR" + KeyValuePairLoader.KVP_DELIMITER + "ffffff",
			svsFullPath + "REST_ENDPOINT_URL" + KeyValuePairLoader.KVP_DELIMITER + "google.com",
			svsFullPath + "RETRY_COUNT" + KeyValuePairLoader.KVP_DELIMITER + "4",
			svsFullPath + "TIMEOUT_SECONDS" + KeyValuePairLoader.KVP_DELIMITER + "10",
			svsFullPath + "GRAVITY" + KeyValuePairLoader.KVP_DELIMITER + "9.8",
			svsFullPath + "PIE" + KeyValuePairLoader.KVP_DELIMITER + "3.14"
		};
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
				.setCmdLineArgs(cmdLineArgsWFullClassName);
		
		AndHow.setConfig(config);
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertEquals("ffffff", UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("google.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(4, SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(10, SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
		assertEquals(9.8, SERVICE_CONFIG.GRAVITY.getValue(), .00000001d);
		assertEquals(3.14, SERVICE_CONFIG.PIE.getValue(), .00000001d);
	}
	
	@Test
	public void testOptionalValuesAreUnset() {
		AndHowConfiguration config = AndHowTestConfig.instance()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
				.addCmdLineArg(uiFullPath + "DISPLAY_NAME", "My App")
				.addCmdLineArg(svsFullPath + "REST_ENDPOINT_URL", "yahoo.com")
				.addCmdLineArg(svsFullPath + "TIMEOUT_SECONDS", "99");

		AndHow.setConfig(config);
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertNull(UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("yahoo.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(3, SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(99, SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}
	
	@Test
	public void testMissingValuesException() {

		AndHowConfiguration config = AndHowTestConfig.instance()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class);


			AndHow.setConfig(config);

		AppFatalException ex = assertThrows(AppFatalException.class, () -> AndHow.instance());

		assertEquals(3, ex.getProblems().filter(RequirementProblem.class).size());
		assertEquals(UI_CONFIG.DISPLAY_NAME, ex.getProblems().filter(RequirementProblem.class).get(0).getPropertyCoord().getProperty());
		assertEquals(SERVICE_CONFIG.REST_ENDPOINT_URL, ex.getProblems().filter(RequirementProblem.class).get(1).getPropertyCoord().getProperty());
		assertEquals(SERVICE_CONFIG.TIMEOUT_SECONDS, ex.getProblems().filter(RequirementProblem.class).get(2).getPropertyCoord().getProperty());

	}
	
	public static interface UI_CONFIG {
		StrProp DISPLAY_NAME = StrProp.builder().mustBeNonNull().build();
		StrProp BACKGROUP_COLOR = StrProp.builder().build();
	}
	
	public static interface SERVICE_CONFIG {
		StrProp REST_ENDPOINT_URL = StrProp.builder().mustBeNonNull().build();
		IntProp RETRY_COUNT = IntProp.builder().defaultValue(3).build();
		IntProp TIMEOUT_SECONDS = IntProp.builder().mustBeNonNull().build();
		DblProp GRAVITY = DblProp.builder().mustBeGreaterThan(9.1d).mustBeLessThan(10.2d).build();
		DblProp PIE = DblProp.builder().mustBeGreaterThanOrEqualTo(3.1).mustBeLessThanOrEqualTo(3.2).build();
	}

}
