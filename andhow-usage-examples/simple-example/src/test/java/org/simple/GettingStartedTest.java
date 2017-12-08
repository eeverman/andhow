package org.simple;

import org.junit.Test;
import org.yarnandtail.andhow.*;

import static org.junit.Assert.*;

/**
 * Simple testing example.
 * 
 * The AndHowTestBase can be used to simplify testing by killing the AndHow
 * instance before each test so that each test reinitializes the AndHow framework,
 * making it easy to test under multiple configuration scenarios.
 * 
 */
public class GettingStartedTest extends AndHowTestBase {
	
	/**
	 * Simple test
	 */
	@Test
	public void testLaunch1_SetPropertiesWithSystemProps() {

		//Set the LAUNCH_CMD via a system property
		System.setProperty("org.simple.GettingStarted.COUNT_DOWN_START", "1");
		System.setProperty("org.simple.GettingStarted.LAUNCH_CMD", "Go!Go!Go!");

		GettingStarted gs = new GettingStarted();
		
		//AndHow initiation happens automatically as soon as one of the properties
		//is accessed.
		assertEquals(Integer.valueOf(1), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("1...Go!Go!Go!", gs.launch());
	}
	
	@Test
	public void testLaunch2_SetPropertyWithSystemPropsAndDefault() {

		//2 is the default for COUNT_DOWN_START, so let it be used
		System.setProperty("org.simple.GettingStarted.LAUNCH_CMD", "Gone!");

		GettingStarted gs = new GettingStarted();
		
		assertEquals(Integer.valueOf(3), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("3...2...1...Gone!", gs.launch());
	}
	

	@Test
	public void testLaunch3_UsingCommandLineArguments() throws Exception {
		
		//findConfig() finds the configuration that would be used if AndHow
		//initiated on its own, auto-discovering any AndHowInit or AndHowTestInit
		//configuration providers to provide that configuration.
		//For this test there are no AndHowInit classes, so a StdConfig instance
		//is returned from findConfig().
		AndHow.findConfig().setCmdLineArgs(new String[] {
			"org.simple.GettingStarted.COUNT_DOWN_START=4",
			"org.simple.GettingStarted.LAUNCH_CMD=GoManGo!"
		}).build();
		
		//Note that if the AndHowTestBase class is not used, calling build()
		//would likely fail because AndHow would already be initialized.  In
		//that case, use AndHowNonProduction instead (see the next test example).
		
		GettingStarted gs = new GettingStarted();
		
		assertEquals(Integer.valueOf(4), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("4...3...2...1...GoManGo!", gs.launch());
	}
	
	@Test
	public void testLaunch4_ForcingARebuildOfAndHow() throws Exception {
		
		//If the AndHowTestBase class is not used for testing, using a construct
		//like this to for a rebuild of AndHow with a new configuration.  Note
		//the use of NonProductionConfig and the forceBuild() method.
		NonProductionConfig.instance().setCmdLineArgs(new String[] {
			"org.simple.GettingStarted.COUNT_DOWN_START=5",
			"org.simple.GettingStarted.LAUNCH_CMD=GoManGo!"
		}).forceBuild();
		
		GettingStarted gs = new GettingStarted();
		
		assertEquals(Integer.valueOf(5), GettingStarted.COUNT_DOWN_START.getValue());
		assertEquals("5...4...3...2...1...GoManGo!", gs.launch());
	}
	
}
