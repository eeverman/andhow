/*
 */
package org.simple;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class SimpleSampleTest {
	
	/**
	 * Test of main method, of class SimpleSample.
	 */
	@Test
	public void testMain() {
		SimpleSample.main(new String[] {"cmd=Launch!Launch!Launch!"});
		
		assertEquals(Integer.valueOf(10), SimpleSample.COUNT_DOWN_START.getValue());
		assertEquals(Integer.valueOf(0), SimpleSample.COUNT_DOWN_END.getValue());
		assertEquals("Launch!Launch!Launch!", new SimpleSample().tellMeTheLaunchCommand());
	}
	
}
