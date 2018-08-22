package org.yarnandtail.andhow;

import org.junit.*;

/**
 *
 * @author ericeverman
 */
public class AndHowCoreTestBase {
	
	@BeforeClass
	public static void killAndHowStateBeforeClass() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@Before
	public void killAndHowStateBeforeTest() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@After
	public void killAndHowStateAfterTest() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
	@AfterClass
	public static void killAndHowStateAfterClass() {
		AndHowCoreTestUtil.destroyAndHow();
	}
	
}
