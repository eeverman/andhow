package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.property.StringProp;

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
				.addGroup(UI_CONFIG.class).addGroup(SERVICE_CONFIG.class)
				.addLoader(new CmdLineLoader())
				.setCmdLineArgs(cmdLineArgsWFullClassName)
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
				.addGroup(UI_CONFIG.class).addGroup(SERVICE_CONFIG.class)
				.addLoader(new CmdLineLoader())
				.addCmdLineArg(uiFullPath + "DISPLAY_NAME", "My App")
				.addCmdLineArg(svsFullPath + "REST_ENDPOINT_URL", "yahoo.com")
				.addCmdLineArg(svsFullPath + "TIMEOUT_SECONDS", "99")
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
					.addGroup(UI_CONFIG.class).addGroup(SERVICE_CONFIG.class)
					.addLoader(new CmdLineLoader())
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
		StringProp DISPLAY_NAME = StringProp.builder().required().build();
		StringProp BACKGROUP_COLOR = StringProp.builder().build();
	}
	
	public static interface SERVICE_CONFIG extends PropertyGroup {
		StringProp REST_ENDPOINT_URL = StringProp.builder().required().build();
		IntProp RETRY_COUNT = IntProp.builder().setDefault(3).build();
		IntProp TIMEOUT_SECONDS = IntProp.builder().required().build();
	}
	

}
