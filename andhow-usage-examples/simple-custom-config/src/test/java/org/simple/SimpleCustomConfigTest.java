package org.simple;

import org.junit.Test;
import org.yarnandtail.andhow.*;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class SimpleCustomConfigTest {
	
	/**
	 * Simple test
	 */
	@Test
	public void testLaunch1_DefaultPropsBasedOnCustomConfigurationAutoDiscovered() {

		SimpleCustomConfig gs = new SimpleCustomConfig();
		
		assertEquals(Integer.valueOf(2), SimpleCustomConfig.COUNT_DOWN_START.getValue());
		assertEquals("2...1...GoGoGone", gs.launch());
	}
	
	
}
