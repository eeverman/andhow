package org.yarnandtail.andhow.compile;

import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class __PropertySample_PropertyRegistrationForClassTest {
	
	@Test
	public void testPropertyRegistrations() throws Exception {
		
		String CN = "org.yarnandtail.andhow.compile.PropertySample";
		String CN_DOT = CN + ".";
		
		PropertyRegistrar regsForClass = new __PropertySample_PropertyRegistrationForClass();
		List<PropertyRegistration> regs = regsForClass.getRegistrationList();
		
		assertEquals(CN, regsForClass.getRootCanonicalName());	//test assumption
		
		//
		//Canonical Property Names
		assertEquals(CN_DOT + "STRING", regs.get(0).getCanonicalPropertyName());
		assertEquals(CN_DOT + "STRING_PUB", regs.get(1).getCanonicalPropertyName());
		//
		assertEquals(CN_DOT + "PI.STRING", regs.get(2).getCanonicalPropertyName());
		//
		assertEquals(CN_DOT + "PI.PI_DC.STRING", regs.get(3).getCanonicalPropertyName());
		assertEquals(CN_DOT + "PI.PI_DC.STRING_PUB", regs.get(4).getCanonicalPropertyName());
		//
		assertEquals(CN_DOT + "PI.PI_DI.STRING", regs.get(5).getCanonicalPropertyName());
		assertEquals(CN_DOT + "PI.PI_DI.STRING_PUB", regs.get(6).getCanonicalPropertyName());
		//
		assertEquals(CN_DOT + "PC.STRING", regs.get(7).getCanonicalPropertyName());
		assertEquals(CN_DOT + "PC.STRING_PUB", regs.get(8).getCanonicalPropertyName());
		//
		assertEquals(CN_DOT + "PC.PC_PC.STRING", regs.get(9).getCanonicalPropertyName());
		//
		assertEquals(CN_DOT + "PC.PC_PI.STRING", regs.get(10).getCanonicalPropertyName());
		
		//
		//A few other aspects for a few properties
		assertEquals(CN, regs.get(0).getCanonicalRootName());
		assertEquals(CN, regs.get(0).getJavaCanonicalParentName());
		assertNull(regs.get(0).getInnerPath());
		//
		assertEquals(CN, regs.get(6).getCanonicalRootName());
		assertEquals(CN + "$PI$PI_DI", regs.get(6).getJavaCanonicalParentName());
		assertArrayEquals(new String[] {"PI", "PI_DI"}, regs.get(6).getInnerPath());
		
		
	}

}
