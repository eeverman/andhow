package org.yarnandtail.andhow;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * A test base class that COMPLETELY kills AndHow between each test and test classes.
 * <p>
 * This util class is intentionally placed in the test directory because it is
 * never intended to be distributed, not even for use by others in their testing.
 * <p>
 * <em>THIS CLASS DOES DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em>
 * <p>
 * For utilities to test your application code with AndHow, see the AndHow
 * Test Harness module.  For examples using the test harness, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 * 
 * 
 * @author ericeverman
 */
public class AndHowTestingTestBase {
	
	public static final String PERMISSION_MSG = 
			"There is some type of permissions/access error while trying to access and modify"
			+ "private fields during testing. "
			+ "Is there a security manager enforcing security during testing?";
	
	/**
	 * Builder for a temporary JNDI context
	 */
	private static SimpleNamingContextBuilder builder;
	
	@BeforeAll
	public static void killAndHowStateBeforeClass() {
		destroyAndHow();
	}
	
	
	@BeforeEach
	public void killAndHowStateBeforeTest() {
		destroyAndHow();
	}
	
	
	@AfterEach
	public void killAndHowStateAfterTest() {
		destroyAndHow();
	}
	
	@AfterAll
	public static void killAndHowStateAfterClass() {
		destroyAndHow();
	}
	
	public static void destroyAndHow() {
		setAndHowInstance(null);
	}
	
	public static AndHow setAndHowInstance(AndHow newInstance) {

		try {

			Field ahInstanceField = AndHow.class.getDeclaredField("singleInstance");
			ahInstanceField.setAccessible(true);

			AndHow oldInstance = (AndHow)(ahInstanceField.get(null));
			ahInstanceField.set(null, newInstance);
			
			return oldInstance;

		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
		
	}
	
}
