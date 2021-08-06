package org.yarnandtail.andhow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class AndHowTest {
	

	@BeforeEach
	public void setup() {
		AndHow._fullyDestroy();
	}
	
	@AfterEach
	public void after() {
		AndHow._fullyDestroy();
	}
	
	/**
	 * Test of instance method, of class AndHow.
	 * Pretty basic - a lot of stuff is null after init.
	 */
	@Test
	public void testInstance() {
		AndHow single = AndHow.instance();
		
		assertNotNull(single);
		assertSame(single, AndHow.instance());
		assertSame(single, AndHow._getSingleInstance());
		
		assertNotNull(AndHow._getInitialization());
		assertFalse(AndHow._getInitializing());
		assertNull(AndHow._getInProcessConfig());
		assertNull(AndHow._getConfigLocator());
		assertFalse(AndHow._getFindingConfig().get());
		assertNotNull(single._getAndHowCore());
		
		single._setAndHowCore(null);	//zap the core
		
		assertNull(single._getAndHowCore());
		assertSame(single, AndHow.instance(), "Still should have same instance");
		assertNotNull(single._getAndHowCore(), "instance should have created a new core");
		
	}

	/**
	 * Test of _fullyInitialize method, of class AndHow.
	 */
	@Test
	public void test_fullyInitialize() {
		
		AndHow._fullyInitialize();
		
		AndHow single = AndHow._getSingleInstance();
		
		assertNotNull(single);
		assertSame(single, AndHow.instance());
		
		assertNotNull(AndHow._getInitialization());
		assertFalse(AndHow._getInitializing());
		assertNull(AndHow._getInProcessConfig());
		assertNull(AndHow._getConfigLocator());
		assertFalse(AndHow._getFindingConfig().get());
		assertNotNull(single._getAndHowCore());
	}
	
	@Test
	public void test_fullyDestroy() {
		
		AndHow._fullyInitialize();
		
		AndHow single = AndHow._getSingleInstance();
		assertNotNull(single);
		
		AndHow._fullyDestroy();
		
		assertNull(AndHow._getSingleInstance());
		assertNull(AndHow._getInitialization());
		assertFalse(AndHow._getInitializing());
		assertNull(AndHow._getInProcessConfig());
		assertNull(AndHow._getConfigLocator());
		assertFalse(AndHow._getFindingConfig().get());
		
		
		assertNotSame(single, AndHow.instance(), "Creating a new instance should be new");
	}

	
}
