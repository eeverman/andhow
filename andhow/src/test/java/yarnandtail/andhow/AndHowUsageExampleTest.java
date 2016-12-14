package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class AndHowUsageExampleTest extends AndHowTestBase {
	
	String uiFullPath = UI_CONFIG.class.getCanonicalName() + ".";
	String svsFullPath = SERVICE_CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgsWFullClassName = new String[0];

	
	@Before
	public void setup() {
		
		cmdLineArgsWFullClassName = new String[] {
			uiFullPath + "DISPLAY_NAME" + AndHow.KVP_DELIMITER + "My App",
			uiFullPath + "BACKGROUP_COLOR" + AndHow.KVP_DELIMITER + "ffffff",
			svsFullPath + "REST_ENDPOINT_URL" + AndHow.KVP_DELIMITER + "google.com",
			svsFullPath + "RETRY_COUNT" + AndHow.KVP_DELIMITER + "4",
			svsFullPath + "TIMEOUT_SECONDS" + AndHow.KVP_DELIMITER + "10"
		};
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		AndHow.builder()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
				.loader(new CmdLineLoader())
				.cmdLineArgs(cmdLineArgsWFullClassName)
				.reloadForUnitTesting(reloader);
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertEquals("ffffff", UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("google.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(new Integer(4), SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(new Integer(10), SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}
	
	@Test
	public void testOptionalValuesAreUnset() {
		AndHow.builder()
				.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
				.loader(new CmdLineLoader())
				.cmdLineArg(uiFullPath + "DISPLAY_NAME", "My App")
				.cmdLineArg(svsFullPath + "REST_ENDPOINT_URL", "yahoo.com")
				.cmdLineArg(svsFullPath + "TIMEOUT_SECONDS", "99")
				.reloadForUnitTesting(reloader);
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertNull(UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("yahoo.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(new Integer(3), SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(new Integer(99), SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}
	
	@Test
	public void testMissingValuesException() {
		
		try {
			AndHow.builder()
					.group(UI_CONFIG.class).group(SERVICE_CONFIG.class)
					.loader(new CmdLineLoader())
					.reloadForUnitTesting(reloader);
			fail();
		} catch (AppFatalException ce) {
			assertEquals(3, ce.getRequirementsProblems().size());
			assertEquals(UI_CONFIG.DISPLAY_NAME, ((RequirementProblem)(ce.getRequirementsProblems().get(0))).getPoint());
			assertEquals(SERVICE_CONFIG.REST_ENDPOINT_URL, ((RequirementProblem)(ce.getRequirementsProblems().get(1))).getPoint());
			assertEquals(SERVICE_CONFIG.TIMEOUT_SECONDS, ((RequirementProblem)(ce.getRequirementsProblems().get(2))).getPoint());
		}
	}
	
	public static interface UI_CONFIG extends PropertyGroup {
		StrProp DISPLAY_NAME = StrProp.builder().required().build();
		StrProp BACKGROUP_COLOR = StrProp.builder().build();
	}
	
	public static interface SERVICE_CONFIG extends PropertyGroup {
		StrProp REST_ENDPOINT_URL = StrProp.builder().required().build();
		IntProp RETRY_COUNT = IntProp.builder().defaultValue(3).build();
		IntProp TIMEOUT_SECONDS = IntProp.builder().required().build();
	}
	

}
