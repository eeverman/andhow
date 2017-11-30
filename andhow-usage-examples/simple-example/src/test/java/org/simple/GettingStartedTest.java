package org.simple;

import org.junit.Test;
import org.yarnandtail.andhow.*;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class GettingStartedTest extends AndHowTestBase {
	
	/**
	 * Simple test
	 */
	@Test
	public void testLaunch1_SetPropertiesWithSystemProps() {
		AndHowNonProductionUtil.destroyAndHow();	//Start w/ a fresh AndHow instance
		
		//Set the LAUNCH_COMMAND via a system property
		System.setProperty("org.simple.GettingStarted.COUNT_DOWN_START", "1");
		System.setProperty("org.simple.GettingStarted.LAUNCH_COMMAND", "Go!Go!Go!");

		GettingStarted gs = new GettingStarted();
		
		assertEquals(Integer.valueOf(1), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("1...Go!Go!Go!", gs.launch());
	}
	
	/**
	 * AndHow initializes only for the entire lifecycle of the application.
	 * AndHowTestBase harness 'breaks' AndHow so that it is re-initialized for each test.
	 */
	@Test
	public void testLaunch2_SetPropertyWithSystemPropsAndDefault() {
		AndHowNonProductionUtil.destroyAndHow();	//Start w/ a fresh AndHow instance
		
		//2 is the default for COUNT_DOWN_START, so let it be used
		System.setProperty("org.simple.GettingStarted.LAUNCH_COMMAND", "Gone!");

		GettingStarted gs = new GettingStarted();
		
		assertEquals(Integer.valueOf(2), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("2...1...Gone!", gs.launch());
	}
	
	@Test
	public void testLaunch3_UsingCommandLineArguments() throws Exception {
		NonProductionConfig.instance().addCmdLineArgs(new String[] {
			"org.simple.GettingStarted.COUNT_DOWN_START=3",
			"org.simple.GettingStarted.LAUNCH_COMMAND=GoManGo!"
		}).forceBuild();
		
		GettingStarted gs = new GettingStarted();
		
		assertEquals(Integer.valueOf(3), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("3...2...1...GoManGo!", gs.launch());
	}
	
}
