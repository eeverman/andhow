package org.yarnandtail.andhow.service;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrarLoaderTest {

	@Test
	public void testAll() throws Exception {
		PropertyRegistrarLoader loader = new PropertyRegistrarLoader();
		assertEquals(2, loader.getPropertyRegistrars().size());
	}
}
