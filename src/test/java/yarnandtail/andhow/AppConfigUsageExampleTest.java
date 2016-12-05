package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.point.IntConfigPoint;
import yarnandtail.andhow.point.StringConfigPoint;

/**
 *
 * @author eeverman
 */
public class AppConfigUsageExampleTest extends AppConfigTestBase {
	
	String uiFullPath = UI_CONFIG.class.getCanonicalName() + ".";
	String svsFullPath = SERVICE_CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgsWFullClassName = new String[0];

	
	@Before
	public void setup() {
		
		cmdLineArgsWFullClassName = new String[] {
			uiFullPath + "DISPLAY_NAME" + AppConfig.KVP_DELIMITER + "My App",
			uiFullPath + "BACKGROUP_COLOR" + AppConfig.KVP_DELIMITER + "ffffff",
			svsFullPath + "REST_ENDPOINT_URL" + AppConfig.KVP_DELIMITER + "google.com",
			svsFullPath + "RETRY_COUNT" + AppConfig.KVP_DELIMITER + "4",
			svsFullPath + "TIMEOUT_SECONDS" + AppConfig.KVP_DELIMITER + "10"
		};
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		AppConfigBuilder.init()
				.addGroup(UI_CONFIG.class).addGroup(SERVICE_CONFIG.class)
				.addLoader(new CmdLineLoader())
				.setCmdLineArgs(cmdLineArgsWFullClassName)
				.build(reloader);
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertEquals("ffffff", UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("google.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(new Integer(4), SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(new Integer(10), SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}
	
	@Test
	public void testOptionalValuesAreUnset() {
		AppConfigBuilder.init()
				.addGroup(UI_CONFIG.class).addGroup(SERVICE_CONFIG.class)
				.addLoader(new CmdLineLoader())
				.addCmdLineArg(uiFullPath + "DISPLAY_NAME", "My App")
				.addCmdLineArg(svsFullPath + "REST_ENDPOINT_URL", "yahoo.com")
				.addCmdLineArg(svsFullPath + "TIMEOUT_SECONDS", "99")
				.build(reloader);
		
		assertEquals("My App", UI_CONFIG.DISPLAY_NAME.getValue());
		assertNull(UI_CONFIG.BACKGROUP_COLOR.getValue());
		assertEquals("yahoo.com", SERVICE_CONFIG.REST_ENDPOINT_URL.getValue());
		assertEquals(new Integer(3), SERVICE_CONFIG.RETRY_COUNT.getValue());
		assertEquals(new Integer(99), SERVICE_CONFIG.TIMEOUT_SECONDS.getValue());
	}
	
	@Test
	public void testMissingValuesException() {
		
		try {
			AppConfigBuilder.init()
					.addGroup(UI_CONFIG.class).addGroup(SERVICE_CONFIG.class)
					.addLoader(new CmdLineLoader())
					.build(reloader);
			fail();
		} catch (AppFatalException ce) {
			assertEquals(3, ce.getRequirementsProblems().size());
			assertEquals(UI_CONFIG.DISPLAY_NAME, ((RequirmentProblem)(ce.getRequirementsProblems().get(0))).getPoint());
			assertEquals(SERVICE_CONFIG.REST_ENDPOINT_URL, ((RequirmentProblem)(ce.getRequirementsProblems().get(1))).getPoint());
			assertEquals(SERVICE_CONFIG.TIMEOUT_SECONDS, ((RequirmentProblem)(ce.getRequirementsProblems().get(2))).getPoint());
		}
	}
	
	public static interface UI_CONFIG extends ConfigPointGroup {
		StringConfigPoint DISPLAY_NAME = StringConfigPoint.builder().required().build();
		StringConfigPoint BACKGROUP_COLOR = StringConfigPoint.builder().build();
	}
	
	public static interface SERVICE_CONFIG extends ConfigPointGroup {
		StringConfigPoint REST_ENDPOINT_URL = StringConfigPoint.builder().required().build();
		IntConfigPoint RETRY_COUNT = IntConfigPoint.builder().setDefault(3).build();
		IntConfigPoint TIMEOUT_SECONDS = IntConfigPoint.builder().required().build();
	}
	

}
