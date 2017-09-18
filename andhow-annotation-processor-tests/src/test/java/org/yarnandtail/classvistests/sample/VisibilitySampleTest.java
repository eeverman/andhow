package org.yarnandtail.classvistests.sample;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * These are some characterization tests of how visibility of non-public
 * inner classes works
 * @author ericeverman
 */
public class VisibilitySampleTest {

	public VisibilitySampleTest() {
	}

	@Test
	public void testRootAccess() throws Exception {
		Class<?> root = VisibilitySample.class;

		Field STRING = root.getDeclaredField("STRING");
		assertFalse(STRING.isAccessible());
		STRING.setAccessible(true);
		assertTrue(STRING.isAccessible());
		assertNotNull(STRING.get(null));
		
		Field STRING_PUB = root.getDeclaredField("STRING_PUB");
		
		//Always false b/c it just indicates that std access controls are in effect
		assertFalse(STRING_PUB.isAccessible());	
		STRING_PUB.setAccessible(true);

		//Setting a field accessible only applies to an instance of a field
		assertFalse(root.getDeclaredField("STRING").isAccessible());
		
		//Class<?> pi = VisibilitySample.PI;	//Not visible
		//Sgtring s = VisibilitySample.STRING;	//NOT VISIBLE
		String sp = VisibilitySample.STRING_PUB;	//visible
	}

	@Test
	public void testPrivateInterfaceAccess() throws Exception {
		Class<?> root = VisibilitySample.class;

		Class<?> PI = findInnerClass(root, "PI");

		assertEquals(
				"org.yarnandtail.classvistests.sample.VisibilitySample.PI",
				PI.getCanonicalName());

		assertTrue(PI.isInterface());
		assertTrue(PI.isMemberClass());
		assertTrue(Modifier.isPrivate(PI.getModifiers()));

		
		Field STRING = PI.getDeclaredField("STRING");
		STRING.setAccessible(true);
		assertNotNull(STRING.get(null));
		
		//Class<?> pi = VisibilitySample.PI;	//Not visible
		//Also Not visible
		//DefaultClass.PI = (VisibilitySample.PI)Class.forName(org.yarnandtail.classvistests.sample..VisibilitySample$PI);

	}

	@Test
	public void testPrivateInterfaceInnerClassAccess() throws Exception {
		Class<?> root = VisibilitySample.class;
		Class<?> PI = findInnerClass(root, "PI");
		Class<?> PI_DC = findInnerClass(PI, "PI_DC");
		
		Class<?> PI_DC_forname = Class.forName("org.yarnandtail.classvistests.sample.VisibilitySample$PI$PI_DC");

		assertEquals(PI_DC_forname, PI_DC);
		
		assertEquals(
				"org.yarnandtail.classvistests.sample.VisibilitySample.PI.PI_DC",
				PI_DC.getCanonicalName());
		
		assertFalse(PI_DC.isInterface());
		assertTrue(PI_DC.isMemberClass());
		assertFalse(Modifier.isPrivate(PI_DC.getModifiers()));

		Field STRING = PI_DC.getDeclaredField("STRING");
		assertFalse(STRING.isAccessible());
		STRING.setAccessible(true);
		assertTrue(STRING.isAccessible());
		assertNotNull(STRING.get(null));
		
		Field STRING_PUB = PI_DC.getDeclaredField("STRING_PUB");
		STRING_PUB.setAccessible(true);
		assertNotNull(STRING_PUB.get(null));
		
	}
	
	@Test
	public void testPrivateInterfaceInnerInterfaceAccess() throws Exception {
		Class<?> root = VisibilitySample.class;
		Class<?> PI = findInnerClass(root, "PI");
		Class<?> PI_DI = findInnerClass(PI, "PI_DI");

		assertEquals(
				"org.yarnandtail.classvistests.sample.VisibilitySample.PI.PI_DI",
				PI_DI.getCanonicalName());

		assertTrue(PI_DI.isInterface());
		assertTrue(PI_DI.isMemberClass());
		assertFalse(Modifier.isPrivate(PI_DI.getModifiers()));

		Field STRING = PI_DI.getDeclaredField("STRING");
		STRING.setAccessible(true);
		assertNotNull(STRING.get(null));
	}

	public Class<?> findInnerClass(Class<?> parent, String innerName) {
		for (Class<?> clazz : parent.getDeclaredClasses()) {
			if (innerName.equals(clazz.getSimpleName())) {
				return clazz;
			}
		}

		return null;
	}

}
